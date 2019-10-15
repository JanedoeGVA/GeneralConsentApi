package domain;

import outils.Utils;

public class ChallengeCode {


    private String code;
    private long createTime;


    public ChallengeCode(String code) {
        this.code = code;
        this.createTime = Utils.getEpochSecond();
    }

    public ChallengeCode() {
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
