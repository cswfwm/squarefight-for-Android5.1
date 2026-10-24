#!/bin/sh
# 用法: ./sign.sh <未签名apk> <输出apk>
# 需要: JDK17 的 java/keytool, 以及 Android build-tools 里的 apksigner.jar
set -e
IN="$1"; OUT="$2"
APKSIGNER_JAR="${ANDROID_HOME:-/opt/android-sdk}/build-tools/34.0.0/lib/apksigner.jar"
KS="${KS:-squarefight.p12}"
PASS="${KS_PASS:?请用环境变量 KS_PASS 传入密钥库密码}"
java -jar "$APKSIGNER_JAR" sign \
  --ks "$KS" --ks-type PKCS12 --ks-pass "pass:$PASS" \
  --key-pass "pass:$PASS" --ks-key-alias "${KS_ALIAS:-squarefight}" \
  --min-sdk-version 21 --v1-signing-enabled true --v2-signing-enabled true \
  --out "$OUT" "$IN"
java -jar "$APKSIGNER_JAR" verify --verbose "$OUT"
