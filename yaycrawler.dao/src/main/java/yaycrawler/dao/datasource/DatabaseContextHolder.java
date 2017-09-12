package yaycrawler.dao.datasource;

/**
 * 保存一个线程安全的DatabaseType容器
 * Created by  yuananyun on 2017/5/4.
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();

    public static void setDatabaseType(DatabaseType type) {
        contextHolder.set(type);
    }

    public static DatabaseType getDatabaseType() {
        return contextHolder.get();
    }

    public static ThreadLocal<DatabaseType> getContextHolder() {
        return contextHolder;
    }

    public static void clearDatabaseType() {
        contextHolder.remove();
    }
}
