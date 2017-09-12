package yaycrawler.common.model;

/**
 * Created by bill on 2017/5/3.
 */
public class FtpTransferFileVo {

    private String fileName;
    private String httpFileUrl;

    public String getFileName() {
        return fileName;
    }

    public String getHttpFileUrl() {
        return httpFileUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setHttpFileUrl(String httpFileUrl) {
        this.httpFileUrl = httpFileUrl;
    }

    @Override
    public String toString() {
        return "FtpTransferFileVo{" +
                "fileName='" + fileName + '\'' +
                ", httpFileUrl='" + httpFileUrl + '\'' +
                '}';
    }
}
