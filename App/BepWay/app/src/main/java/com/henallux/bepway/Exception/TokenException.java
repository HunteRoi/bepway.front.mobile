package com.henallux.bepway.Exception;


public class TokenException extends Exception {

    private String errorMessage;

    public TokenException(String errorMessage){
        setErrorMessage(errorMessage);
    }

    public TokenException(){
        setErrorMessage("Erreur lors de l'obtention du token");
    }
    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
