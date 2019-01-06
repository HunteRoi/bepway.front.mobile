package com.henallux.bepway.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Zoning {
    private int id;
    private String name;
    private Coordinate zoningCenter;
    private String city;
    private String commune;
    private String url;
    private int superficie;
    private int nbImplantations;

    public Zoning(){}

    public Zoning(String name, String city, String commune, String url, int superficie, Coordinate zoningCenter) {
        setName(name);
        setCity(city);
        setCommune(commune);
        setUrl(url);
        setSuperficie(superficie);
    }

    public Coordinate getZoningCenter(){return zoningCenter;}

    public void setZoningCenter(Coordinate zoningCenter){this.zoningCenter = zoningCenter;}

    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSuperficie() {
        return superficie;
    }

    public void setSuperficie(int superficie) {
        this.superficie = superficie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbImplantations() {
        return nbImplantations;
    }

    public void setNbImplantations(int nbImplantations) {
        this.nbImplantations = nbImplantations;
    }
}
