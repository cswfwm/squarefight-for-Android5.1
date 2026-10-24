import sys, struct

ANDROID_NS = "http://schemas.android.com/apk/res/android"

def parse_string_pool(data, off):
    # chunk header already read by caller? We parse from off point (start of header)
    typ, hsz, csz = struct.unpack_from('<HHI', data, off)
    count, style_count, flags, str_start, style_start = struct.unpack_from('<IIIII', data, off+8)
    utf8 = (flags & 0x100) != 0
    offsets = struct.unpack_from('<%dI' % count, data, off+hsz)
    strings = []
    base = off + str_start
    for so in offsets:
        p = base + so
        if utf8:
            # utf16 length then utf8 length (u8), then bytes
            # skip u16 length
            p2 = p
            # first varint = utf16 len
            l, p2 = read_varlen(data, p2)
            l2, p2 = read_varlen(data, p2)
            s = data[p2:p2+l2].decode('utf-8', 'replace')
        else:
            l, p2 = read_varlen16(data, p)
            s = data[p2:p2+l*2].decode('utf-16-le', 'replace')
        strings.append(s)
    return strings

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

def parse_axml(path):
    data = open(path, 'rb').read()
    typ, hsz, csz = struct.unpack_from('<HHI', data, 0)
    assert typ == 0x0003, hex(typ)
    off = hsz
    strings = []
    resmap = []
    out = []
    depth = 0
    while off < csz:
        ctyp, chsz, ccsz = struct.unpack_from('<HHI', data, off)
        if ctyp == 0x0001:  # string pool
            strings = parse_string_pool(data, off)
        elif ctyp == 0x0180:  # resource map
            n = (ccsz - chsz)//4
            resmap = list(struct.unpack_from('<%dI' % n, data, off+chsz))
        elif ctyp == 0x0102:  # START_ELEMENT
            # line, comment, ns, name, attributeStart, attributeSize, attributeCount, ...
            ns, name = struct.unpack_from('<II', data, off+16)
            attr_start, attr_size, attr_count = struct.unpack_from('<HHH', data, off+24)
            tag = strings[name]
            attrs = []
            aoff = off + 16 + attr_start
            for i in range(attr_count):
                a = aoff + i*attr_size
                ai_ns, ai_name, ai_raw = struct.unpack_from('<III', data, a)
                a_size, a_res0, a_dtype, a_data = struct.unpack_from('<HBBI', data, a+12)
                nm = strings[ai_name] if ai_name < len(strings) else '?'
                # value
                if a_dtype == 0x03 and ai_raw != 0xffffffff and ai_raw < len(strings):
                    val = repr(strings[ai_raw])
                elif a_dtype == 0x10:
                    val = str(a_data)
                elif a_dtype == 0x12:
                    val = 'true' if a_data!=0 else 'false'
                elif a_dtype == 0x01:
                    val = '#%08x' % a_data
                elif a_dtype == 0x04:
                    val = 'float:%r' % struct.unpack('<f', struct.pack('<I', a_data))[0]
                else:
                    val = '[type=0x%02x data=0x%x]' % (a_dtype, a_data)
                res_id = 0
                # find res id: resource map index is ai_name? Actually in start elem, name index is string idx; resmap maps string idx->resource id
                if ai_name < len(resmap):
                    res_id = resmap[ai_name]
                attrs.append((nm, val, res_id))
            indent = '  '*depth
            out.append(f'{indent}<{tag}>')
            for nm,val,rid in attrs:
                out.append(f'{indent}    @{nm} = {val}   (resId=0x{rid:08x})' if rid else f'{indent}    @{nm} = {val}')
            depth += 1
        elif ctyp == 0x0103:  # END_ELEMENT
            depth = max(0, depth-1)
        off += ccsz
    return '\n'.join(out)

print(parse_axml(sys.argv[1]))
