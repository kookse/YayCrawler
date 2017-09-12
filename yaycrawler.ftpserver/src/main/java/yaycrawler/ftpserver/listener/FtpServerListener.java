package yaycrawler.ftpserver.listener;

/**
 * Created by ucs_guoguibiao on 6/13 0013.
 */
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.ftpserver.impl.DefaultFtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import yaycrawler.common.utils.FtpClientUtils;

@Component
public class FtpServerListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(FtpServerListener.class);

    public void contextDestroyed(ServletContextEvent contextEvent) {
        logger.info("Stopping FtpServer");
        DefaultFtpServer server = (DefaultFtpServer) contextEvent.getServletContext()
                .getAttribute("FTPSERVER_CONTEXT_NAME");
        if (server != null) {
            server.stop();
            contextEvent.getServletContext().removeAttribute("FTPSERVER_CONTEXT_NAME");
            logger.info("FtpServer stopped");
        } else {
            logger.info("No running FtpServer found");
        }
    }

    public void contextInitialized(ServletContextEvent contextEvent) {
        logger.info("Starting FtpServer");
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(contextEvent.getServletContext());
        DefaultFtpServer server = (DefaultFtpServer) ctx.getBean("myServer");
        contextEvent.getServletContext().setAttribute("FTPSERVER_CONTEXT_NAME", server);
        try {
            server.start();
            String msg = "FtpServer started，ftp服务启动了";
            msg = new String(msg.getBytes("gbk"));
            System.out.println(msg);
            logger.info("FtpServer started，ftp服务启动了");
            System.out.println("*********************ftp服务启动了*******************");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start FtpServer", e);
        }
    }
}
