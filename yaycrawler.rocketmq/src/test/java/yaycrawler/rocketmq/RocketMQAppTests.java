package yaycrawler.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RocketMQApp.class)
//@ContextConfiguration({"classpath:/*.xml"})
public class RocketMQAppTests {

    @Test
    public void contextLoads() {
    }

    @Autowired(required = false)
    private DefaultMQProducer producer;

    @Autowired(required = false)
    private DefaultMQPushConsumer consumer;

    @Test
    public void 测试发送消息() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message msg = new Message("TEST",// topic
                "TEST",// tag
                "KKK",//key用于标识业务的唯一性
                ("Hello RocketMQ !!!!!!!!!!").getBytes()// body 二进制字节数组
        );
        SendResult result = producer.send(msg);
        System.out.println(result);
    }


}
