package yaycrawler.common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: NoFreeSpaceProcesser
 * @Description:
 */
public class NoFreeSpaceProcesser implements RejectedExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(NoFreeSpaceProcesser.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        // TODO 可以把任务加入队列,定时执行
        logger.error("线程池处理器:[{}]没有空闲线程处理任务.", executor.toString());
    }
}
