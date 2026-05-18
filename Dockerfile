# 多模块 Maven 构建（build context 必须为项目根目录）
# believe-gateway/auth/user 不在 believe-framework reactor 中，需两步构建
FROM crpi-27zlqugq2208c0pz.cn-hangzhou.personal.cr.aliyuncs.com/xiaofeiyang930112/maven:3.9-eclipse-temurin-25 AS build
ARG SERVICE_DIR
WORKDIR /build

# 阿里云 Maven 镜像
COPY scripts/settings.xml /root/.m2/settings.xml

# Step 1: 安装框架模块到本地 Maven 仓库
# believe-common/* 和 believe-job 都在 believe-framework/ 目录下
COPY believe-framework/ believe-framework/
RUN mvn install -DskipTests -f believe-framework/pom.xml

# Step 2: 构建目标服务
COPY ${SERVICE_DIR}/ ${SERVICE_DIR}/
RUN mvn package -DskipTests -f ${SERVICE_DIR}/pom.xml -q

# 运行时镜像
FROM crpi-27zlqugq2208c0pz.cn-hangzhou.personal.cr.aliyuncs.com/xiaofeiyang930112/eclipse-temurin:25-jre-alpine
ARG SERVICE_DIR
WORKDIR /app
COPY --from=build /build/${SERVICE_DIR}/target/*.jar app.jar
EXPOSE 8080 8081 8082 8101 8102 8201 8202
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:--Xmx256m} -jar app.jar"]