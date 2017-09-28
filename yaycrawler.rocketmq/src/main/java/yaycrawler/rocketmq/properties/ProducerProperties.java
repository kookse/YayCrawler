package yaycrawler.rocketmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static yaycrawler.rocketmq.properties.ProducerProperties.PREFIX;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@ConfigurationProperties(prefix = PREFIX)
//@PropertySource(value = "classpath:/rocketmq.properties")
public class ProducerProperties {

    public static final String PREFIX = "rocketmq.producer";

    private String namesrvAddr;
    private int maxMessageSize;
    private int sendMsgTimeout;


    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public int getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }
}
