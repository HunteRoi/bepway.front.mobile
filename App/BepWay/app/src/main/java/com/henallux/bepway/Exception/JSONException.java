package com.henallux.bepway.Exception;

public class JSONException extends Exception {
    private String errorMessage;

    public JSONException(String errorMessage){
        setErrorMessage("Error while reading data : " + errorMessage);
    }
    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
