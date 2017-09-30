package yaycrawler.worker.initialization;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.zk.ZKTools;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.worker.mapper.TaskTypeInfoMapper;
import yaycrawler.worker.puller.list.InitScheduleConfigPuller;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @ClassName: ZkConfigInitialization
 * @Description:    schedule的ZK初始化
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/12 10:35
 */
@Component
public class ScheduleZkConfigInitialization implements ConfigAndServiceInitialization{

    @Autowired
    @Qualifier("initScheduleConfigPuller")
    private InitScheduleConfigPuller initScheduleConfigPuller;
    @Autowired
    private TaskTypeInfoMapper taskTypeInfoMapper;
    @Autowired
    private TBScheduleManagerFactory scheduleManagerFactory;
    @Value("${job.zkConfig.rootPath}")
    private String zkRootPath;
    @Value("${tfs.temporary.root.directory:d:/tmp}")
    private String temporaryRootDirectory;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleZkConfigInitialization.class);

    @Override
    @PostConstruct
    public void init(){

        this.initDirectory();
        this.initZkConfig();
    }

    private void initZkConfig(){

        logger.info("start initializer config from database for schedule tasks ...");
        // 验证参数
        // 把所有任务类型里面selectTag == null || FALSE 改成TRUE
        //taskTypeInfoMapper.updateAllTaskTag(Boolean.TRUE);

        // 初始化数据到zookeeper
        initScheduleConfigPuller.initScheduleConfig();
    }

    private void initDirectory(){

        File directory = new File(temporaryRootDirectory);
        if (!directory.exists()){
            directory.mkdir();
            logger.info("目录:[{}]不存在,创建目录.", temporaryRootDirectory);
        } else {
            logger.info("目录:[{}]已存在,不再创建目录.", temporaryRootDirectory);
        }
    }

    private void deleteExpiredInfo(){
        try {
            ZooKeeper zooKeeper = scheduleManagerFactory.getZkManager().getZooKeeper();
            if (zooKeeper.exists(zkRootPath + "/factory", false) != null) {
                ZKTools.deleteTree(zooKeeper, zkRootPath + "/factory");
            }
            if (zooKeeper.exists(zkRootPath + "/strategy", false) != null) {
                ZKTools.deleteTree(zooKeeper, zkRootPath + "/strategy");
            }
            if (zooKeeper.exists(zkRootPath + "/baseTaskType", false) != null) {
                ZKTools.deleteTree(zooKeeper, zkRootPath + "/baseTaskType");
            }
            logger.error("删除schedule注册到Zk的信息成功.");
        } catch (Exception e){
            e.printStackTrace();
            logger.error("删除schedule注册到Zk的信息失败.");
        }
    }
}
