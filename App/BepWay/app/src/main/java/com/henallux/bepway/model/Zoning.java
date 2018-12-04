package com.henallux.bepway.model;

public class Zoning {
    private String name;
    private Coordinate[] roads;
    private String city;
    private String commune;
    private String url;
    private int superficie;

    public Zoning(String name, String city, String commune, String url, int superficie, Coordinate[] roads) {
        setName(name);
        setCity(city);
        setCommune(commune);
        setUrl(url);
        setSuperficie(superficie);
        setRoads(roads);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate[] getRoads() {
        return roads;
    }

    public void setRoads(Coordinate[] roads) {
        this.roads = roads;
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
}
