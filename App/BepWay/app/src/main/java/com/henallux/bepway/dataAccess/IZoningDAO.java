package com.henallux.bepway.dataAccess;

import com.henallux.bepway.Exception.ApiErrorException;
import com.henallux.bepway.model.Zoning;

import java.util.ArrayList;

public interface IZoningDAO {
    ArrayList<Zoning> getAllZoningsAPI(String token, int pageIndex, String filterKey, String filterValue) throws ApiErrorException, Exception;
    ArrayList<Zoning> jsonToAPIZonings(String stringJSON) throws Exception;
}
