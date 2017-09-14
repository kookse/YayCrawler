package yaycrawler.admin;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
//    }

    /**
     * 自定义错误页面
     *
     * @return
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            container.addErrorPages(
                    new ErrorPage(HttpStatus.FORBIDDEN, "/error/403"),
                    new ErrorPage(HttpStatus.NOT_FOUND, "/assets/404"),
                    new ErrorPage(HttpStatus.NOT_FOUND, "/assets/500"),
                    new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/error/error"),
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/error"),
                    new ErrorPage(Exception.class, "/error/error"));
        });
    }

    /**
     * 自定义错误view解析器
     *
     * @return
     */
    @Bean
    public ErrorViewResolver portalErrorViewParser() {
        return new CrawlerErrorViewParser();
    }

}
