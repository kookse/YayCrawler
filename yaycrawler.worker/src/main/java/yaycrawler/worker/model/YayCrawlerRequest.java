package yaycrawler.worker.model;

import yaycrawler.common.utils.IdentificationGenerator;

/**
 * 爬虫请求对象
 */
public class YayCrawlerRequest {
    public enum RequestCategory {
        GONGJIJIN("公积金", 0), SHEBAO("社保", 1), GESHUI("个税", 2);

        private String name;
        private int value;

        RequestCategory(String name, int value) {
            this.name = name;
            this.value = value;
        }
        public enum AccountType{
            ID_CARD("身份证",0),PERSON_NAME("姓名",1),MOBILE("手机号",2);
            private String name;
            private int value;

            AccountType(String name, int value) {
                this.name = name;
                this.value = value;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private int category;
    private String cityCode;
    private String accountType;
    private String account;
    private String password;


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YayCrawlerRequest that = (YayCrawlerRequest) o;

        if (category != that.category) return false;
        if (!cityCode.equals(that.cityCode)) return false;
        if (!accountType.equals(that.accountType)) return false;
        if (!account.equals(that.account)) return false;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        int result = category;
        result = 31 * result + cityCode.hashCode();
        result = 31 * result + accountType.hashCode();
        result = 31 * result + account.hashCode();
        return result;
    }

    /**
     * 生成唯一标识
     *
     * @return
     */
    public String getIdentification() {
        return IdentificationGenerator.fromHash(String.valueOf(category), cityCode, accountType, account);
    }

}
