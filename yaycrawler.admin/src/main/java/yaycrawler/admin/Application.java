package yaycrawler.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
@EntityScan(basePackages = {"yaycrawler.dao.domain"})
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            container.addErrorPages(
                    new ErrorPage(HttpStatus.FORBIDDEN, "/error/403"),
                    new ErrorPage(HttpStatus.NOT_FOUND, "/assets/404.html"),
                    new ErrorPage(HttpStatus.NOT_FOUND, "/assets/500.html"),
                    new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/error/error"),
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/error"),
                    new ErrorPage(Exception.class, "/error/error"));
        });
    }
}
