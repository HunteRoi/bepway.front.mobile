package com.henallux.bepway.model;

public class Company {
    private String name;
    private String zoning;
    private String city;
    private String rueNumero;
    private String sector;
    private Coordinate location;
    private boolean isPremium;
    //private Zoning zoning

    public Company(String name, String zoning, String city, String rueNumero, String sector /*Coordinate location,Zoning zoning*/){
        setName(name);
        setRueNumero(rueNumero);
        setSector(sector);
        setCity(city);
        setZoning(zoning);
        setPremium(false);
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

    public String getAddress(){
        StringBuilder output = new StringBuilder();
        output.append((getRueNumero() == null)? "adresse non connue, " : getRueNumero()+", ");
        output.append((getCity() == null)? " ville non connue" : getCity());
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) { this.city = city; }

    public String getRueNumero() {
        return rueNumero;
    }

    public void setRueNumero(String rueNumero) {
        this.rueNumero = rueNumero;
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
}
