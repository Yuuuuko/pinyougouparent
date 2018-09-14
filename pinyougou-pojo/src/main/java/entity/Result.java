package entity;

import java.io.Serializable;

public class Result implements Serializable{
    private boolean status;
    private String msg;

    public Result(boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Result() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
