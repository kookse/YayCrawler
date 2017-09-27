package com.smartdata360.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@SpringBootApplication
@ImportResource(locations = {"classpath*:spring/*.xml"})
public class RocketMQApp {
    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, IOException {

        ApplicationContext context = SpringApplication.run(RocketMQApp.class,args);
        DefaultMQProducer defaultMQProducer = context.getBean(DefaultMQProducer.class);
        Message msg = new Message("TEST",// topic
                "TEST",// tag
                "KKK",//key用于标识业务的唯一性
                ("Hello RocketMQ !!!!!!!!!!" ).getBytes()// body 二进制字节数组
        );
        SendResult result = defaultMQProducer.send(msg);
        System.out.println(result);
        DefaultMQPushConsumer consumer = context.getBean(DefaultMQPushConsumer.class);

//        SpringApplication app = new SpringApplication(RocketMQApp.class);
//        Properties properties = new Properties();
//        InputStream in =
//                RocketMQApp.class.getClassLoader().getResourceAsStream("rocketmq.properties");
//        properties.load(in);
//        app.setDefaultProperties(properties);
//        app.run(args);

    }
}