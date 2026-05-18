#!/bin/bash
# Believe 微服务部署脚本
set -e

ENV=${1:-dev}
SERVICE=${2:-all}

echo "=== Believe 部署脚本 ==="
echo "环境: ${ENV}"
echo "服务: ${SERVICE}"

case ${ENV} in
  dev)
    NAMESPACE="believe-dev"
    ;;
  staging)
    NAMESPACE="believe-staging"
    ;;
  prod)
    NAMESPACE="believe-prod"
    ;;
  *)
    echo "未知环境: ${ENV}，可选: dev, staging, prod"
    exit 1
    ;;
esac

kubectl apply -f "../k8s/${ENV}/" -n "${NAMESPACE}"
echo "部署完成: ${SERVICE} -> ${ENV}"
