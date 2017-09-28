package yaycrawler.rocketmq.processor;

import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by  yuananyun on 2017/9/4.
 */
//@Component("defaultMessageProcessor")
//@ConditionalOnMissingBean(name={"defaultMessageProcessor"})
public class DefaultMessageProcessor implements IMessageProcessor {

    private Logger logger= LoggerFactory.getLogger(DefaultMessageProcessor.class);

    @Override
    public boolean handleMessage(MessageExt messageExt) {
        logger.info("receive : " + messageExt.toString());

        return true;
    }
}
