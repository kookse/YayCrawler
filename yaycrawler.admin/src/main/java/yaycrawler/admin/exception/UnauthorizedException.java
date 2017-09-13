package yaycrawler.admin.exception;

/**
 * Created by  yuananyun on 2017/3/14.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("您无权访问");
    }

}
