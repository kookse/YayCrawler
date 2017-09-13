package yaycrawler.dao.config;

import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yaycrawler.dao.datasource.DatabaseType;
import yaycrawler.dao.datasource.DynamicDataSource;
import yaycrawler.dao.transaction.MultiDataSouceTransactionFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MyBatis基础配置
 */
@Configuration
@ConditionalOnBean(value = DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MyBatisConfig {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTransactionFactory(new MultiDataSouceTransactionFactory());

        Properties properties = new Properties();
        properties.setProperty("logImpl", "STDOUT_LOGGING");
        bean.setConfigurationProperties(properties);


        StringBuilder aliasesPackageBuilder = new StringBuilder();
        aliasesPackageBuilder.append("yaycrawler.**.model");
        bean.setTypeAliasesPackage(aliasesPackageBuilder.toString());

        //分页插件
        //PageHelper pageHelper = new PageHelper();
        //Properties properties = new Properties();
        //properties.setProperty("reasonable", "true");
        //properties.setProperty("supportMethodsArguments", "true");
        //properties.setProperty("returnPageInfo", "check");
        //properties.setProperty("params", "count=countSql");
        //pageHelper.setProperties(properties);

        //添加插件
        //bean.setPlugins(new Interceptor[]{pageHelper});

        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
//            bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
            SqlSessionFactory factory = bean.getObject();
//            factory.getConfiguration().setDefaultExecutorType(ExecutorType.BATCH);//batch 模式 update 或 insert不会返回正确的结果
            factory.getConfiguration().setMapUnderscoreToCamelCase(true);
            factory.getConfiguration().setLogImpl(Log4j2Impl.class);
            factory.getConfiguration().setCacheEnabled(true);

            //配置默认的数据库类型
            if (dataSource instanceof DynamicDataSource) {
                String defaultDbType = ((DynamicDataSource) dataSource).getDefaultDbType();
                DatabaseType.setDefault(Enum.valueOf(DatabaseType.class, defaultDbType));
            }
            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
