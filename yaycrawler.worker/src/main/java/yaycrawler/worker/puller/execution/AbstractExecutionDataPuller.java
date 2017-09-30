package yaycrawler.worker.puller.execution;

import java.util.List;

/**
 * @ClassName: AbstractExecutionDataPuller
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/12 15:04
 */
public abstract class AbstractExecutionDataPuller<T> implements ExecutionDataPuller<T> {

//    private static final String DEFAULT_ORDER_PREFIX = "s_p_base_query_";

    protected Boolean isEmpty(List list){
        return list == null || list.isEmpty();
    }

    /**
     * 获取订单表
     * @param tableName
     * @return
     */
//    protected String getAvailableTableName(String tableName){
//
//        return StringUtils.isEmpty(tableName) ? DEFAULT_ORDER_PREFIX + TimeUtils.now("yyyyMM") : tableName;
//    }



}
