package com.henallux.bepway.Exception;

public class CompanyException extends Exception{
    private int errorMessage;

    public CompanyException(int errorMessage){
        setErrorMessage(errorMessage);
    }


    public int getMessageCode() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
        this.errorMessage = errorMessage;
    }
}
