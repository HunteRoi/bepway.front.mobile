package com.henallux.bepway.dataAccess;


import java.util.Date;

public class MainDAO {
    void checkToken(float expiresDate){
        float actualDate = new Date().getTime()/1000;
        if(actualDate > expiresDate){

        }
    }
}
