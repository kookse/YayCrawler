package yaycrawler.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import yaycrawler.worker.listener.WorkerRegisterListener;

@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
@EnableJpaRepositories(basePackages = {"yaycrawler.dao.repositories"})
@EntityScan(basePackages = {"yaycrawler.**.domain"})
public class Application {

	public static void main(String[] args) {
        SpringApplication springApplication =new SpringApplication(Application.class);
        springApplication.addListeners(new WorkerRegisterListener());
        springApplication.run(args);
	}

}
