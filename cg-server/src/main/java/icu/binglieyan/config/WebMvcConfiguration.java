package icu.binglieyan.config;

import icu.binglieyan.interceptor.JwtAuthenticationUserInterceptor;
import icu.binglieyan.filters.UserScopeFilter;
import icu.binglieyan.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author binglieyyan
 */
@Configuration
@Log4j2
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final JwtAuthenticationUserInterceptor jwtAuthenticationUserInterceptor;
    private final JwtProperties jwtProperties;
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        log.info("添加拦截器");
        registry.addInterceptor(jwtAuthenticationUserInterceptor)
                .addPathPatterns("/admin/**", "/teacher/**", "/student/**")
                // 排除所有包下的用户登录和新建用户接口，无需 JWT 认证
                .excludePathPatterns(
                        "/admin/users/userslogin",
                        "/teacher/users/userslogin",
                        "/student/users/userslogin"
                );

    }

    @Bean
    public UserScopeFilter userScopeFilter() {
        return new UserScopeFilter(jwtProperties);
    }

    @Bean
    public FilterRegistrationBean<UserScopeFilter> userScopeFilterRegistration(UserScopeFilter userScopeFilter) {
        log.info("注册UserScopeFilter");
        FilterRegistrationBean<UserScopeFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(userScopeFilter);
        registrationBean.addUrlPatterns(
                "/teacher/users/teacherQueryById",
                "/teacher/plagiarismChecks/publish/*",
                "/teacher/plagiarismChecks/queryPlagiarismChecks/*",
                "/teacher/plagiarismChecks/download/*",
                "/teacher/classes/addClasses",
                "/teacher/classes/queryStudentById/*",
                "/teacher/classes/teacherQueryClassesById",
                "/teacher/classes/removeStudent/*",
                "/teacher/assignments/teacherQueryById/*",
                "/teacher/questionSubmissions/manualScore",
                "/student/questionSubmissions/upload-with-file",
                "/student/questionSubmissions/queryById/*",
                "/student/submissions/addSubmissions/*",
                "/student/submissions/queryById/*",
                "/student/users/joinClass/*",
                "/student/users/studentQueryById",
                "/student/assignments/queryById",
                "/student/classes/queryById"
        );
        registrationBean.setOrder(0);
        registrationBean.setName("userScopeFilter");
        return registrationBean;
    }
}
