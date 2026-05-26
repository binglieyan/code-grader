package icu.binglieyan;

import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author binglieyan
 */
@SpringBootApplication()
@EnableTransactionManagement
@Log4j2
@EnableScheduling
@MapperScan(basePackages = {"icu.binglieyan.mapper"})
public class CgApplication {
    static void main(String[] args) {
        SpringApplication.run(CgApplication.class, args);
        log.info("启动成功，应用已准备就绪");
    }
}
