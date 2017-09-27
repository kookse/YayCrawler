package com.smartdata360.rocketmq.service;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.smartdata360.rocketmq.exception.RocketMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by  yuananyun on 2017/9/5.
 */
@Service
public class MQProduceService {

    @Autowired
    @Qualifier("defaultMQProducer")
    private DefaultMQProducer producer;

    public MQProduceService(@Autowired DefaultMQProducer producer) {
        this.producer = producer;
    }

    /**
     * 发送一个消息到队列
     *
     * @param topic 消息所属的话题
     * @param tags  消息的标签，自定义
     * @param keys  消息的唯一标识
     * @param data  消息的内容转字符串
     * @return
     */
    public boolean sendMsg(String topic, String tags, String keys, String data) {
        try {
            SendResult result = producer.send(new Message(topic, tags, keys, data.getBytes(Charset.forName("utf-8"))));
            if (result.getSendStatus() == SendStatus.SEND_OK) return true;
            return false;
        } catch (Exception ex) {
            throw new RocketMQException(ex);
        }
    }


    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }
}
