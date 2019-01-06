package com.henallux.bepway.dataAccess;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

public interface ITokenDAO {
    Token getToken(LoginModel login)throws TokenException, Exception;

}
