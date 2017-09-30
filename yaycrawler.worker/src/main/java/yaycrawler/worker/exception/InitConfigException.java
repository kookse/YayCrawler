package yaycrawler.worker.exception;

/**
 * @ClassName: InitConfigException
 * @Description:    初始化必要数据失败,数据库或者缓存不存在必要的初始化数据
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 19:23
 */
public class InitConfigException extends RuntimeException{

    public InitConfigException() {
        super();
    }

    public InitConfigException(String message) {
        super(message);
    }

    public InitConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitConfigException(Throwable cause) {
        super(cause);
    }

    protected InitConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
