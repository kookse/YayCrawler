package yaycrawler.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
@EntityScan(basePackages = {"yaycrawler.dao.domain"})
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
