package com.example.shardingjdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ShardingJdbcConfigTests {

    @Test
    void contextLoads() {
        // 这个测试只验证Spring上下文是否能正常加载
        // 使用测试配置文件，包含H2内存数据库配置
    }
}