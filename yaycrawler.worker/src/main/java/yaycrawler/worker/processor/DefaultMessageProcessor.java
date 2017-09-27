package yaycrawler.worker.processor;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.smartdata360.rocketmq.processor.IMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import yaycrawler.worker.service.TaskScheduleService;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@Component("defaultMessageProcessor")
@ConditionalOnMissingBean(name={"defaultMessageProcessor"})
public class DefaultMessageProcessor implements IMessageProcessor {

    private Logger logger= LoggerFactory.getLogger(DefaultMessageProcessor.class);

    @Autowired
    private TaskScheduleService taskScheduleService;
    
    @Override
    public boolean handleMessage(MessageExt messageExt) {
        logger.info("receive : " + messageExt.toString());

        return true;
    }
}
