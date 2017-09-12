package yaycrawler.common.utils;

/**
 * @author bill
 * @create 2017-09-12 11:33
 * @desc
 **/
public class EnviromentUtil {
    public static void main(String[] args) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.environment().forEach((key,value) -> {
            System.out.println(key + ":" + value);
        });
        System.out.println("process build!");
    }
}
