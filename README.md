# squarefight-for-Android5.1

「几何决斗 / 方形对决」的 **Android 5.1（API 21）32 位** 可安装版本。

- 成品：`apk/squarefight-android5.1-armeabi-v7a.apk`（约 11.3 MB）
- `minSdkVersion`：21（Android 5.0 / 5.1 可安装）
- ABI：仅 `armeabi-v7a`（32 位）
- 签名：APK Signature Scheme v1 + v2 + v3，均已校验通过
- 低体积（11 MB），远低于 40 MB 上限

## 来源

- 上游开源项目：**pama1234/just-some-other-libgdx-game**
  - 仓库：https://github.com/pama1234/just-some-other-libgdx-game
  - 模块：`game0003`（README 中标注为「几何决斗」，移植自 FAL 的学生时期作品）
  - 包名：`pama1234.gdx.game.app.duel`
  - 框架：libGDX 1.12.1（Java）
  - 许可证：**AGPL-3.0**
- 本仓库基于 pama1234 官方构建产出的 APK 做兼容性改造，用于在 Android 5.1 32 位设备上运行。

> 说明：另一个同样叫「几何决斗」的项目 `Yun-OwO/geometry-duel`（PixiJS 网页重制版）与本仓库不是同一个程序，本项目采用 pama1234 原版。

## 对官方 APK 做了哪些改造

在不改动游戏逻辑与资源的前提下，仅调整安卓打包层面的三处设置：

| 项目 | 原版 | 本版 |
|------|------|------|
| `minSdkVersion` | 26（Android 8.0，5.1 装不上） | **21**（Android 5.0/5.1） |
| `android:extractNativeLibs` | `false`（API 21/22 下 .so 加载失败） | **`true`**（安装时解压原生库） |
| `lib/` 原生库 | arm64-v8a / armeabi-v7a / x86 / x86_64 | **仅 armeabi-v7a**（32 位） |

改完后用自签名证书重新签名（v1+v2+v3）。

## 复现步骤

依赖：`python3`、JDK 17、Android SDK `build-tools`（提供 `apksigner.jar` 与 `keytool`）。

```bash
# 1) 改造清单并过滤 ABI，生成未签名 APK
python3 tools/repack_full.py apk/original-几何决斗.apk /tmp/unsigned.apk

# 2) 生成签名密钥（仅首次；请妥善保管，后续更新需同一把钥匙才能覆盖安装）
keytool -genkeypair -v -keystore squarefight.p12 -storetype PKCS12 \
  -alias squarefight -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass <你的密码> -keypass <你的密码> \
  -dname "CN=SquareFight, OU=Dev, O=SquareFight, L=NA, ST=NA, C=CN"

# 3) 签名
java -jar $ANDROID_HOME/build-tools/34.0.0/lib/apksigner.jar sign \
  --ks squarefight.p12 --ks-type PKCS12 --ks-pass pass:<你的密码> \
  --key-pass pass:<你的密码> --ks-key-alias squarefight \
  --min-sdk-version 21 --v1-signing-enabled true --v2-signing-enabled true \
  --out apk/squarefight-android5.1-armeabi-v7a.apk /tmp/unsigned.apk

# 4) 校验
java -jar $ANDROID_HOME/build-tools/34.0.0/lib/apksigner.jar verify --verbose \
  apk/squarefight-android5.1-armeabi-v7a.apk
```

`tools/repack_full.py` 会用内置的 AXML 解析器直接在二进制 `AndroidManifest.xml` 上把 `minSdkVersion` 改为 21、`extractNativeLibs` 改为 `true`，并剔除除 `armeabi-v7a` 之外的原生库，然后重新打包（不经过 aapt）。

## 兼容性说明与已知风险

- 已通过静态校验：签名有效、`minSdkVersion=21`、`armeabi-v7a` 原生库存在、`extractNativeLibs=true`。
- **尚未在真实的 Android 5.1 设备上做安装/运行验证**（本容器为 arm64、无 Android 5.1 设备或模拟器）。
- 该 APK 仅含 32 位 `armeabi-v7a` 库：在「纯 64 位」设备上安装可能提示不兼容；Android 5.1 时代的设备均为 32 位，符合目标场景。
- 若在 5.1 上遇到运行时崩溃（例如某个框架库调用了 API 23+ 才有的接口），需要回到上游源码、把 `minSdk` 真正降到 21 后重新编译，而不是仅改清单。届时可在本仓库补充源码构建流程。

## 目录结构

```
.
├── README.md
├── LICENSE            # AGPL-3.0（继承上游）
├── apk/
│   ├── squarefight-android5.1-armeabi-v7a.apk   # 最终成品（32 位 / API21+）
│   └── original-几何决斗.apk                     # 官方原始 APK（输入）
└── tools/
    ├── repack_full.py  # 清单改写 + ABI 过滤 + 重新打包
    ├── axml.py         # 二进制 AndroidManifest 解析器（辅助查看）
    └── sign.sh         # 签名脚本示例
```

## 许可证

上游项目 pama1234/just-some-other-libgdx-game 采用 **AGPL-3.0**。本仓库对 APK 的改造同样遵循 AGPL-3.0，完整许可证文本见 `LICENSE`，上游源码见：
https://github.com/pama1234/just-some-other-libgdx-game
