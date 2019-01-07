package com.henallux.bepway.Exception;


import com.henallux.bepway.R;

public class TokenException extends Exception {

    private int errorMessage;

    public TokenException(int errorMessage){
        setErrorMessage(errorMessage);
    }

    public TokenException(){
        setErrorMessage(R.string.token_getting_error);
    }


    public int getMessageCode() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
        this.errorMessage = errorMessage;
    }
}
