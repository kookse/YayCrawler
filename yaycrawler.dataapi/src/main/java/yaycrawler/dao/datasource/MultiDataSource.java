package yaycrawler.dao.datasource;

import java.lang.annotation.*;

/**
 * Created by  yuananyun on 2017/5/4.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiDataSource {
    DatabaseType value();
}