package yaycrawler.rocketmq.config;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import yaycrawler.rocketmq.exception.RocketMQException;
import yaycrawler.rocketmq.listener.MessageListener;
import yaycrawler.rocketmq.processor.IMessageProcessor;
import yaycrawler.rocketmq.properties.ConsumerProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@SpringBootConfiguration
@EnableConfigurationProperties(ConsumerProperties.class)
@ConditionalOnProperty(value = "rocketmq.consumer.default.init", havingValue = "true")
public class DefaultConsumerConfiguration {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultConsumerConfiguration.class);

    @Autowired
    private ConsumerProperties properties;

    @Autowired
    @Qualifier(value = "defaultMessageProcessor")
    private IMessageProcessor messageProcessor;

    @Value("${rocketmq.consumer.default.groupName:''}")
    private String groupName;
    @Value("${rocketmq.consumer.default.topic:''}")
    private String topic;
    @Value("${rocketmq.consumer.default.tag:''}")
    private String tag;

    @Bean(value = "defaultMQConsumer")
    public DefaultMQPushConsumer getRocketMQConsumer() throws RocketMQException {
        if (StringUtils.isBlank(groupName)) {
            throw new RocketMQException("groupName is null !!!");
        }
        String namesrvAddr = properties.getNamesrvAddr();
        if (StringUtils.isBlank(namesrvAddr)) {
            throw new RocketMQException("namesrvAddr is null !!!");
        }
        if (StringUtils.isBlank(topic)) {
            throw new RocketMQException("topic is null !!!");
        }
        if (StringUtils.isBlank(tag)) {
            throw new RocketMQException("tag is null !!!");
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumeThreadMin(properties.getConsumeThreadMin());
        consumer.setConsumeThreadMax(properties.getConsumeThreadMax());
        MessageListener messageListener = new MessageListener();
        messageListener.setMessageProcessor(messageProcessor);
        consumer.registerMessageListener(messageListener);
        try {
            consumer.subscribe(topic, tag);
            consumer.start();
            LOGGER.info("consumer config is start !!! groupName:{},topic:{},namesrvAddr:{}", groupName, topic, namesrvAddr);
        } catch (MQClientException e) {
            LOGGER.error("consumer config is start !!! groupName:{},topic:{},namesrvAddr:{},Exception:{}", groupName, topic, namesrvAddr, e);
            throw new RocketMQException(e);
        }
        return consumer;
    }
}
