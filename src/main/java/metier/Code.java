package metier;

public class Code {


    private String code;
    private long expireTime;


    public Code (String code) {
        this.code = code;
        this.expireTime = Utils.getExpireEpochSecond();
    }

    public Code () {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
