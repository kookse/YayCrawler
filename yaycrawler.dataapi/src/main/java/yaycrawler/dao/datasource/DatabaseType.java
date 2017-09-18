package yaycrawler.dao.datasource;

/**
 * *列出所有的数据源key（常用数据库名称来命名）
 * 注意：
 * 1）这里数据源与数据库是一对一的
 * 2）DatabaseType中的变量名称就是数据库的名称
 * Created by  yuananyun on 2017/5/4.
 */
public enum DatabaseType {
    //    核心数据库
    primary("primary"),
    //    非核心数据源
    second("second"),
    third("third"),
    forth("fourth"),
    //    账务分析库
    analysis("analysis");


    private String value;

    DatabaseType(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


    /**
     * 默认的数据库类型
     */
    private static DatabaseType defaultDbType=DatabaseType.primary;

    public static DatabaseType getDefault() {
        return defaultDbType;
    }

    public static void setDefault(DatabaseType dbType) {
        defaultDbType = dbType;
    }
}
