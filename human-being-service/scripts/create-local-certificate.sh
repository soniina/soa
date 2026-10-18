#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p certs
keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore certs/localhost.p12 \
  -storepass "${SSL_KEY_STORE_PASSWORD:-changeit}" -validity 365 \
  -dname 'CN=localhost' -ext 'SAN=dns:localhost,ip:127.0.0.1'
