package yaycrawler.rocketmq.processor;

import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * Created by  yuananyun on 2017/9/4.
 */
public interface IMessageProcessor {
    /**
     * 处理消息的接口
     * @param messageExt
     * @return
     */
    public boolean handleMessage(MessageExt messageExt);
}
