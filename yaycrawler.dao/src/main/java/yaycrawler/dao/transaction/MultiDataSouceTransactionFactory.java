package yaycrawler.dao.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * 支持Service内多数据源切换的Factory
 * Created by  yuananyun on 2017/7/19.
 */
public class MultiDataSouceTransactionFactory extends SpringManagedTransactionFactory {

    /**
     * {@inheritDoc}
     *
     * @param dataSource
     * @param level
     * @param autoCommit
     */
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new MultiDataSourceTransaction(dataSource);
    }
}
