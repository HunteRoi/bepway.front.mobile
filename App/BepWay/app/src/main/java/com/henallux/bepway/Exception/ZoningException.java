package com.henallux.bepway.Exception;

public class ZoningException extends Exception{
    private String errorMessage;

    public ZoningException(){
        setErrorMessage("Une erreur est survenue lors de l'obtention des zonings");
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
