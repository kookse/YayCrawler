package yaycrawler.dao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * 分布式事物管理配置
 * Created by  yuananyun on 2017/7/18.
 */
//@Configuration
//@EnableTransactionManagement
public class TransactionManagerConfig implements TransactionManagementConfigurer {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    /****************************************单数据库事务处理************************************/
    @Bean("transactionManager")
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }

    /************************************分布式数据库事务处理************************************/
//    使用分布式事务时设置Postgresql的max_prepared_transactions为大于0的值,该值默认是0
//    Postgresql的max_prepared_transactions参数值默认是0，要开启分布式事务需要设置为大于0的值，
//    该参数在PostgreSQL\9.3\data\postgresql.conf文件中。


//    @Bean(name = "userTransaction")
//    public UserTransaction userTransaction() throws Throwable {
//        UserTransactionImp userTransactionImp = new UserTransactionImp();
//        userTransactionImp.setTransactionTimeout(10000);
//        return userTransactionImp;
//    }
//
//    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
//    public TransactionManager atomikosTransactionManager() throws Throwable {
//        UserTransactionManager userTransactionManager = new UserTransactionManager();
//        userTransactionManager.setForceShutdown(false);
//        return userTransactionManager;
//    }
//
//    @Bean(name = "transactionManager")
//    @DependsOn({ "userTransaction", "atomikosTransactionManager" })
//    public PlatformTransactionManager transactionManager() throws Throwable {
//        UserTransaction userTransaction = userTransaction();
//        JtaTransactionManager manager =
//                new JtaTransactionManager(userTransaction,atomikosTransactionManager());
//        return manager;
//    }
//
//    @Bean("transactionManager")
//    public  PlatformTransactionManager txManager() {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Override
//    public  PlatformTransactionManager annotationDrivenTransactionManager() {
//        return txManager();
//    }

}
