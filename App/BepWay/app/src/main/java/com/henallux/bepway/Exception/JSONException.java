package com.henallux.bepway.Exception;

public class JSONException extends Exception {
    private int errorMessage;

    public JSONException(int errorMessage){
        setErrorMessage(errorMessage);
    }

    public int getMessageCode() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
        this.errorMessage = errorMessage;
    }
}
