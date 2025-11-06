package com.example.shardingjdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationStartupTest {

    @Test
    public void testApplicationContextStartup() {
        // 这个测试只验证Spring应用上下文是否能正常启动
        // 使用测试配置文件，包含H2内存数据库配置
    }
}