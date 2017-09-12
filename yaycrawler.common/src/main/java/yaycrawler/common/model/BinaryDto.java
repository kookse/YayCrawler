package yaycrawler.common.model;

/**
 * @author bill
 * @create 2017-08-29 10:42
 * @desc 二值化的图像
 **/
public class BinaryDto {

    private int sw;
    private int topMin;
    private int topMax;
    private String img;
    private String src;
    private String dest;
    private String language = "eng";
    private String cookie;

    public BinaryDto() {

    }

    public BinaryDto(String dest, String img,String language) {
        this.dest = dest;
        this.language = language;
        this.img = img;
    }

    public BinaryDto(int sw, int topMin, int topMax, String img, String src, String dest, String language) {
        this.sw = sw;
        this.topMin = topMin;
        this.topMax = topMax;
        this.img = img;
        this.src = src;
        this.dest = dest;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public int getSw() {
        return sw;
    }

    public void setSw(int sw) {
        this.sw = sw;
    }

    public int getTopMin() {
        return topMin;
    }

    public void setTopMin(int topMin) {
        this.topMin = topMin;
    }

    public int getTopMax() {
        return topMax;
    }

    public void setTopMax(int topMax) {
        this.topMax = topMax;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "BinaryDto{" +
                "sw=" + sw +
                ", topMin=" + topMin +
                ", topMax=" + topMax +
                ", img='" + img + '\'' +
                ", src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", language='" + language + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
