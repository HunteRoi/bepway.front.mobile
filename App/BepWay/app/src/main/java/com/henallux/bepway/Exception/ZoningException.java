package com.henallux.bepway.Exception;

import com.henallux.bepway.R;

public class ZoningException extends Exception{
    private int errorMessage;

    public ZoningException(){
        setErrorMessage(R.string.zoning_error);
    }


    public int getMessageCode() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
        this.errorMessage = errorMessage;
    }
}
