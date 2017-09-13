package yaycrawler.dao.config;


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(MyBatisConfig.class)
@ConditionalOnBean(value = DataSource.class)
public class MyBatisMapperScannerConfig {

    private static final String PACKAGE_SPILT_SIGN = ";";

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        StringBuilder mapperPackageBuilder = new StringBuilder();
        mapperPackageBuilder
//                .append("com.smartdata360.platform.schedule.**.mapper")
//                .append(PACKAGE_SPILT_SIGN)
//                .append("com.smartdata360.**.**.mapper")
//                .append(PACKAGE_SPILT_SIGN)
                .append("yaycrawler.**.mapper");

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(mapperPackageBuilder.toString());
        Properties properties = new Properties();
        properties.setProperty("mappers", "yaycrawler.dao.AbiMapper");
        properties.setProperty("notEmpty", "false");
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }

}
