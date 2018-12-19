package com.henallux.bepway.model;

public class Token {
    private String token;
    private long expiresDateInSec;
    private String exception;

    public Token(){}

    public Token(String token, long expiresIn) {
        setToken(token);
        setExpiresin(expiresIn);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresin() {
        return expiresDateInSec;
    }

    public void setExpiresin(long expiresin) {
        this.expiresDateInSec = expiresin;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
