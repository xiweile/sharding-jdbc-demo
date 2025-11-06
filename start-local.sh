#!/bin/bash

# 设置系统属性，禁用Nacos
JAVA_OPTS="-Dspring.cloud.nacos.config.enabled=false"
JAVA_OPTS="$JAVA_OPTS -Dspring.cloud.nacos.discovery.enabled=false"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=local"

# 启动应用程序
java $JAVA_OPTS -jar target/sharding-jdbc-demo-1.0-SNAPSHOT.jar