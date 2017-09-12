package yaycrawler.dao.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源（需要继承AbstractRoutingDataSource）
 * Created by  yuananyun on 2017/5/4.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private String defaultDbType;

    protected Object determineCurrentLookupKey() {
        DatabaseType type = DatabaseContextHolder.getDatabaseType();
        if (type == null) type = DatabaseType.getDefault();
        return type.getValue();
    }

    public String getDefaultDbType() {
        return defaultDbType;
    }

    public void setDefaultDbType(String defaultDbType) {
        this.defaultDbType = defaultDbType;
    }
}
