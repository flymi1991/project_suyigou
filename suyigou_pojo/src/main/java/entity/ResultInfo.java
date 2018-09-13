package entity;

import java.io.Serializable;

public class ResultInfo implements Serializable {
    private Boolean success;
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultInfo(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public ResultInfo() {
    }
}
