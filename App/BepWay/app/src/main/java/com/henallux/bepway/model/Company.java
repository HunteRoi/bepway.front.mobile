package com.henallux.bepway.model;

public class Company {
    private String name;
    private String zoning;
    private String city;
    private String rueNumero;
    private String sector;
    //private Zoning zoning

    public Company(String name, String zoning, String city, String rueNumero, String sector/*,Zoning zoning*/){
        setName(name);
        setRueNumero(rueNumero);
        setSector(sector);
        setCity(city);
        setZoning(zoning);
        //setZoning(zoning);
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

    /*public void setZoning(Zoning zoning){
        this.zoning = zoning;
    }*/

    /*public Zoning getZoning(){
        return this.zoning;
    }*/
}
