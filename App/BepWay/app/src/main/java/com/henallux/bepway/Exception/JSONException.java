package com.henallux.bepway.Exception;

public class JSONException extends Exception {
    private String errorMessage;

    public JSONException(String errorMessage){
        setErrorMessage(errorMessage);
    }
    @Override
    public String getMessage() {
        return getErrorMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
