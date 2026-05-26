package icu.binglieyan.config;

import lombok.extern.log4j.Log4j2;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author binglieyan
 */
@Configuration
@Log4j2
public class Knife4jConfiguration {
    @Bean
    public GroupedOpenApi adminApi() {
        log.info("创建管理端接口分组");
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .packagesToScan("icu.binglieyan.controller.admin")
                .build();
    }

    @Bean
    public GroupedOpenApi teacherApi() {
        log.info("创建教师端接口分组");
        return GroupedOpenApi.builder()
                .group("教师端接口")
                .packagesToScan("icu.binglieyan.controller.teacher")
                .build();
    }

    @Bean
    public GroupedOpenApi studentApi() {
        log.info("创建学生端接口分组");
        return GroupedOpenApi.builder()
                .group("学生端接口")
                .packagesToScan("icu.binglieyan.controller.student")
                .build();
    }

}
