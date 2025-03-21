package com.imes.base.network.exception;

public class ServiceException extends Exception {
    private int respCode;
    private String detailMessage;

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public int getRespCode() {
        return respCode;
    }

    public String getMessage() {
        return detailMessage;
    }


    public ServiceException(int respCode, String detailMessage) {
        this.respCode = respCode;
        this.detailMessage = detailMessage;
    }
}
