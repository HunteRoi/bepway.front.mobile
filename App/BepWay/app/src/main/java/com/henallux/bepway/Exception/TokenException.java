package com.henallux.bepway.Exception;


public class TokenException extends Exception {

    private String errorMessage;

    public TokenException(String errorMessage){
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
