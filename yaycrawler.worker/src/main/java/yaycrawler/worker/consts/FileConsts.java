package yaycrawler.worker.consts;

import java.io.File;

/**
 * @ClassName: FileConsts
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/28 14:17
 */
public class FileConsts {

    public static final String DEFAULT_SUBMITTER_ID = "TFS_OF_QLDHLBS";
    /**
     * apiCode和batchId分隔符
     */
    public static final String API_CODE_BATCHID_SEPARATOR = "_";
    /**
     * 文件路径在数据表里面的分隔符
     */
    public static final String FILE_SEPARATOR_AT_TABLE = ",";
    /**
     * 文件分隔符
     */
    public static final String FILE_SEPARATOR = File.separator;
    /**
     * 参数分割符
     */
    public static final String PARAMS_SEPARATOR = "&;";
    /**
     * 默认的查询结果描述符
     */
    public static final String DEFAULT_DESCRIPTION = "-";

    /**
     * 换行
     */
    public static final String RESULT_NEWLINE = "\n";

    /**
     * 校验错误的状态码
     */
    public static final int DEFAULT_VALIDATE_FAILURE_CODE = 56;

    /**
     * 输入数据文件后缀
     */
    public static final String INPUT_DATA_SUFFIX = "DAT";

    /**
     * 输入校验文件后缀
     */
    public static final String INPUT_VALIDATE_SUFFIX = "ARV";

    /**
     * 输出结果文件后缀
     */
    public static final String OUTPUT_RESULT_SUFFIX = "RZT";

    /**
     * 输出校验文件后缀
     */
    public static final String OUTPUT_VALIDATE_SUFFIX = "ARZ";

    /**
     * 临时文件后缀
     */
    public static final String TEMPORARY_SUFFIX = "TMP";

    /**
     * 上传中的校验文件后缀
     */
    public static final String CHECK_SUFFIX = "CHK";

    /**
     * 调用方上传文件夹
     */
    public static final String INPUT_DIRECTORY = "input";

    /**
     * 服务方处理中文件夹
     */
    public static final String WORK_DIRECTORY = "work";

    /**
     * 校验错误文件夹
     */
    public static final String ERROR_DIRECTORY = "error";

    /**
     * 处理结果目录
     */
    public static final String RESULT_DIRECTORY = "result";

    /**
     * 输入暂存文件夹
     */
    public static final String DONE_DIRECTORY = "done";

    /**
     * 上传文件的保存时间,单位小时
     */
    public static final int INPUT_SAVE_TIMES = 2;

    /**
     * 临时文件或者一些杂乱文件保存时间,单位小时
     */
    public static final int TEMPORARY_SVAE_TIMES = 6;

    /**
     * 处理当前请求失败后,回滚前面几步MOVE操作的最多次数
     */
    public static final int MAX_ROLLBACK_TIMES_AFTER_FAILURE = 5;
}
