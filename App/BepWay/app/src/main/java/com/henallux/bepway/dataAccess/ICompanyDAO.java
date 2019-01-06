package com.henallux.bepway.dataAccess;

import com.henallux.bepway.Exception.ApiErrorException;
import com.henallux.bepway.Exception.JSONException;
import com.henallux.bepway.model.Company;

import java.util.ArrayList;

public interface ICompanyDAO {
    ArrayList<Company> getAllCompanies(String token, int pageIndex, String filterKey, String filterValue) throws ApiErrorException, Exception;
    ArrayList<Company> getCompaniesByZoning(String token, int zoningId, int pageIndex, int pageSize, String filterKey, String filterValue) throws Exception;
    ArrayList<Company> getCompaniesByZoning(String token, int zoningId, int pageIndex, String filterKey, String filterValue) throws Exception;
    ArrayList<Company> jsonToCompanies(String stringJSON) throws Exception, JSONException;

}
