package yaycrawler.worker.tasks;

import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;
import yaycrawler.spider.persistent.PersistentServiceFactory;

/**
 * @author bill
 * @create 2017-10-11 10:10
 * @desc rocketMQ定时程序
 **/
public class RocketMQTask {

    private PersistentServiceFactory persistentServiceFactory;

    public RocketMQTask() {
    }

    public void sendHeartbeart() {
        IResultPersistentService rocketMQPersistentService = persistentServiceFactory.getPersistentServiceByDataType(PersistentDataType.ROCKETMQ);

    }

    public void setRocketMQ(PersistentServiceFactory persistentServiceFactory) {
        this.persistentServiceFactory = persistentServiceFactory;
    }

}
