package com.henallux.bepway.Exception;

public class CompanyException extends Exception{
    private String errorMessage;

    public CompanyException(){
        setErrorMessage("Une erreur est survenue lors de l'obtention des entreprises");
    }
    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
