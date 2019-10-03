package metier;

public class Code {


    private String code;
    private long createTime;


    public Code (String code) {
        this.code = code;
        this.createTime = Utils.getEpochSecond();
    }

    public Code () {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
