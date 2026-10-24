import sys, struct, zipfile, shutil

SRC = sys.argv[1]          # 原 APK
OUT = sys.argv[2]          # 输出未签名 APK

ANDROID_NS = "http://schemas.android.com/apk/res/android"
RES_MIN_SDK = 0x0101020c
RES_EXTRACT_NATIVE = 0x010104ea

def read_varlen(data, p):
    v = data[p]; p += 1
    if v & 0x80:
        v = ((v & 0x7f) << 8) | data[p]; p += 1
    return v, p

def read_varlen16(data, p):
    v = struct.unpack_from('<H', data, p)[0]; p += 2
    if v & 0x8000:
        v = ((v & 0x7fff) << 16) | struct.unpack_from('<H', data, p)[0]; p += 2
    return v, p

def parse_string_pool(data, off):
    typ, hsz, csz = struct.unpack_from('<HHI', data, off)
    count, style_count, flags, str_start, style_start = struct.unpack_from('<IIIII', data, off+8)
    utf8 = (flags & 0x100) != 0
    offsets = struct.unpack_from('<%dI' % count, data, off+hsz)
    base = off + str_start
    strings = []
    for so in offsets:
        p = base + so
        if utf8:
            l, p2 = read_varlen(data, p)
            l2, p2 = read_varlen(data, p2)
            s = data[p2:p2+l2].decode('utf-8', 'replace')
        else:
            l, p2 = read_varlen16(data, p)
            s = data[p2:p2+l*2].decode('utf-16-le', 'replace')
        strings.append(s)
    return strings

def patch_manifest(data, new_min_sdk=21, new_extract=True):
    data = bytearray(data)
    typ, hsz, csz = struct.unpack_from('<HHI', data, 0)
    off = hsz
    strings = []
    resmap = []
    found = {}
    while off < csz:
        ctyp, chsz, ccsz = struct.unpack_from('<HHI', data, off)
        if ctyp == 0x0001:
            strings = parse_string_pool(data, off)
        elif ctyp == 0x0180:
            n = (ccsz - chsz)//4
            resmap = list(struct.unpack_from('<%dI' % n, data, off+chsz))
        elif ctyp == 0x0102:  # START_ELEMENT
            ns, name = struct.unpack_from('<II', data, off+16)
            attr_start, attr_size, attr_count = struct.unpack_from('<HHH', data, off+24)
            tag = strings[name] if name < len(strings) else '?'
            aoff = off + 16 + attr_start
            for i in range(attr_count):
                a = aoff + i*attr_size
                ai_ns, ai_name, ai_raw = struct.unpack_from('<III', data, a)
                a_size, a_res0, a_dtype, a_data = struct.unpack_from('<HBBI', data, a+12)
                rid = resmap[ai_name] if ai_name < len(resmap) else 0
                if rid == RES_MIN_SDK:
                    found['minSdk'] = (a+16, a_data)
                    struct.pack_into('<I', data, a+16, new_min_sdk)
                elif rid == RES_EXTRACT_NATIVE:
                    found['extractNativeLibs'] = (a+16, a_data)
                    struct.pack_into('<I', data, a+16, 0xFFFFFFFF if new_extract else 0)
        off += ccsz
    return bytes(data), found

def main():
    zin = zipfile.ZipFile(SRC, 'r')
    zout = zipfile.ZipFile(OUT, 'w')
    kept, dropped = [], []
    for item in zin.infolist():
        name = item.filename
        # 丢弃旧签名
        if name.startswith('META-INF/') and name.split('/')[-1].upper().endswith(('.SF', '.RSA', '.DSA', '.EC')):
            dropped.append(name); continue
        if name.startswith('META-INF/') and name.split('/')[-1].upper() in ('MANIFEST.MF', 'CERT.SF'):
            dropped.append(name); continue
        # 只保留 armeabi-v7a 原生库
        if name.startswith('lib/') and not name.startswith('lib/armeabi-v7a/'):
            dropped.append(name); continue
        content = zin.read(name)
        if name == 'AndroidManifest.xml':
            content, found = patch_manifest(content)
            print('清单修补:', {k: (hex(v[1])) for k, v in found.items()})
        # 保持原压缩方式
        zi = zipfile.ZipInfo(name, date_time=item.date_time)
        zi.compress_type = item.compress_type
        zi.external_attr = item.external_attr
        zout.writestr(zi, content)
        kept.append(name)
    zout.close(); zin.close()
    print('保留条目:', len(kept), '| 丢弃条目:', len(dropped))
    for d in dropped: print('  drop:', d)

if __name__ == '__main__':
    main()
