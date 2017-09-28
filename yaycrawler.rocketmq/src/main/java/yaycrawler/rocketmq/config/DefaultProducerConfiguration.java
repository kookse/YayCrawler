package yaycrawler.rocketmq.config;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import yaycrawler.rocketmq.exception.RocketMQException;
import yaycrawler.rocketmq.properties.ProducerProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@SpringBootConfiguration
@EnableConfigurationProperties(ProducerProperties.class)
@ConditionalOnProperty(value = "rocketmq.producer.default.init", havingValue = "true")
public class DefaultProducerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProducerConfiguration.class);

    @Autowired
    private ProducerProperties properties;
    @Value("${rocketmq.producer.default.instanceName:''}")
    private String instanceName;
    @Value("${rocketmq.producer.default.groupName:''}")
    private String groupName;


    @Bean(value = "defaultMQProducer")
    public DefaultMQProducer getRocketMQProducer() throws RocketMQException {
        String namesrvAddr = properties.getNamesrvAddr();
        if (StringUtils.isBlank(namesrvAddr)) {
            throw new RocketMQException("nameServerAddr is blank");
        }
        if (StringUtils.isBlank(groupName)) {
            throw new RocketMQException("groupName is blank");
        }
        if (StringUtils.isBlank(instanceName)) {
            throw new RocketMQException("instanceName is blank");
        }
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(instanceName);
        producer.setMaxMessageSize(properties.getMaxMessageSize());
        producer.setSendMsgTimeout(producer.getSendMsgTimeout());
        try {
            producer.start();
            LOGGER.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]", groupName, namesrvAddr));
        } catch (MQClientException e) {
            LOGGER.error(String.format("producer is error {}", e.getMessage(), e));
            throw new RocketMQException(e);
        }
        return producer;
    }
}
