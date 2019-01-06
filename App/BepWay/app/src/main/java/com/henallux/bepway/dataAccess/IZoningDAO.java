package com.henallux.bepway.dataAccess;

import com.henallux.bepway.Exception.JSONException;
import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.Exception.ZoningException;
import com.henallux.bepway.model.Zoning;

import java.util.ArrayList;

public interface IZoningDAO {
    ArrayList<Zoning> getAllZoningsAPI(String token, int pageIndex, String filterKey, String filterValue) throws TokenException, ZoningException;
    ArrayList<Zoning> jsonToAPIZonings(String stringJSON) throws JSONException;
}
