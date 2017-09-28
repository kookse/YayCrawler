package yaycrawler.rocketmq.processor;

import com.alibaba.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@Component
public class MessageProcessorImplTest implements IMessageProcessor {
    @Override
    public boolean handleMessage(MessageExt messageExt) {
        System.out.println("receive : " + messageExt.toString());
        return true;
    }
}
