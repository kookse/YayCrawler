package yaycrawler.dao.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据库切面拦截器
 * Created by  yuananyun on 2017/5/4.
 */
@Aspect
@Component
@Order(1)
public class DataSourceAspect {

    private static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("execution(* yaycrawler..mapper.*Mapper.*(..))")
    public void datasourcePoint() {
    }

    @Around("datasourcePoint()")
    public Object handler(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        MultiDataSource multiDataSource = method.getAnnotation(MultiDataSource.class);
        if (multiDataSource == null) {
            Class<?> clazz = method.getDeclaringClass();
            multiDataSource = clazz.getAnnotation(MultiDataSource.class);
        }
        DatabaseType databaseType = DatabaseType.getDefault();
        if (multiDataSource != null)
            databaseType = multiDataSource.value();
        logger.debug("current datasource : {}", databaseType.getValue());
        try {
            DatabaseContextHolder.setDatabaseType(databaseType);
            Object result = point.proceed();
            DatabaseContextHolder.clearDatabaseType();
            return result;
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }

}
