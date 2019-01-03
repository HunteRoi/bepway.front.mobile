package com.henallux.bepway.model;

import java.io.Serializable;

public class Company implements Serializable {
    private int id;
    private String name;
    private String zoning;
    private String address;
    private String sector;
    private String status;
    private Coordinate location;
    private boolean isPremium;
    private String description;
    private String siteUrl;
    private String imageUrl;
    //private Zoning zoning

    public Company(){}

    public Company(int id, String name, String zoning, String address, String sector, String status, Coordinate location, boolean isPremium, String description, String siteUrl, String imageUrl /*Coordinate location,Zoning zoning*/){
        setId(id);
        setName(name);
        setAddress(address);
        setSector(sector);
        setStatus(status);
        setZoning(zoning);
        setLocation(location);
        setPremium(isPremium);
        setDescription(description);
        setSiteUrl(siteUrl);
        setImageUrl(imageUrl);
        //setZoning(zoning);
        //setLocation(location);
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        //output.append("Zoning : " + zoning.toString());
        output.append("Zoning : " + getZoning());
        output.append("\nName : " + getName());
        output.append("\nSector : " + getSector());
        output.append("\n"+getAddress());
        return output.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZoning() {
        return zoning;
    }

    public void setZoning(String zoning) {
        this.zoning = zoning;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /*public void setZoning(Zoning zoning){
        this.zoning = zoning;
    }*/

    /*public Zoning getZoning(){
        return this.zoning;
    }*/

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
