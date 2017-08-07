package yaycrawler.ftpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import yaycrawler.ftpserver.listener.FtpServerListener;

@SpringBootApplication(scanBasePackages = "yaycrawler.ftpserver")
@ImportResource(locations = {"classpath*:spring/ftpserver.xml"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
