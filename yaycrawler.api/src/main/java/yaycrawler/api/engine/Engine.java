package yaycrawler.api.engine;

import yaycrawler.common.model.EngineResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bill
 * @create 2017-08-28 17:00
 * @desc 引擎基础类
 **/
public interface Engine<T> {

    /**
     * 引擎执行器
     *
     * @param info
     * @return
     */
    EngineResult execute(T info);

    List<EngineResult> execute(List<T> info);

    /**
     * 执行失败的回调
     * @param info
     * @param exception
     */
    default EngineResult failureCallback(T info, Exception exception) {
        EngineResult engineResult = new EngineResult();
        engineResult.setStatus(Boolean.FALSE);
        List<Exception> exceptions = new ArrayList<>();
        exceptions.add(exception);
        engineResult.setExceptions(exceptions);
        engineResult.setMessage(exception.getMessage());
        return engineResult;
    }
}
