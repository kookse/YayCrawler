package com.smartdata360.rocketmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.smartdata360.rocketmq.properties.ConsumerProperties.PREFIX;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@ConfigurationProperties(prefix =PREFIX)
//@PropertySource(value = "classpath:/rocketmq.properties")
public class ConsumerProperties {
    public static final String PREFIX="rocketmq.consumer";

    private String namesrvAddr;
    private int consumeThreadMin;
    private int consumeThreadMax;


    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    public int getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public int getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }
}
