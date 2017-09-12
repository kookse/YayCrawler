package yaycrawler.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import yaycrawler.admin.communication.MasterActor;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Controller
public class GlobalErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);
    @Override
    public String getErrorPath() {
        return "error";
    }
}
