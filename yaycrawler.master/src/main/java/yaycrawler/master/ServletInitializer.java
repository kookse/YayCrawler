package yaycrawler.master;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		application.listeners(new MasterRegisterListener());
		return application.sources(Application.class);
	}

//	@Override
//	public void onStartup(ServletContext servletContext) throws ServletException {
//		servletContext.setInitParameter("logging.config", "classpath:log4j2-${spring.profiles.active}.xml");
//		super.onStartup(servletContext);
//	}
}
