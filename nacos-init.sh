#!/bin/sh
set -e

NACOS_URL="http://nacos:8848/nacos"

echo "==> Waiting for Nacos to be ready..."
until curl -s "${NACOS_URL}/v1/console/health/readiness" > /dev/null 2>&1; do
  sleep 2
done
echo "==> Nacos is ready"

# 创建 dev 命名空间（customNamespaceId 即命名空间 ID）
echo "==> Creating namespace 'dev'..."
curl -s -X POST "${NACOS_URL}/v1/console/namespaces" \
  -d "customNamespaceId=dev&namespaceName=dev&namespaceDesc=开发环境" \
  -o /dev/null

# 发布 Redis 共享配置
echo "==> Publishing common-redis.yaml..."
curl -s -X POST "${NACOS_URL}/v1/cs/configs" \
  --data-urlencode "dataId=common-redis.yaml" \
  --data-urlencode "group=DEFAULT_GROUP" \
  --data-urlencode "tenant=dev" \
  --data-urlencode "type=yaml" \
  --data-urlencode "content@/nacos-config/common-redis.yaml" \
  -o /dev/null

echo "==> Nacos initialization complete"
