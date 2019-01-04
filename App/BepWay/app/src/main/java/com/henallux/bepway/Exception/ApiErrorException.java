package com.henallux.bepway.Exception;

public class ApiErrorException extends Exception {
    private String errorMessage;

    public ApiErrorException(String errorMessage){
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
