package com.henallux.bepway.model;

public class Company {
    private String name;
    private String zoning;
    private String ville;
    private String rueNumero;
    private String sector;

    public Company(String name, String zoning, String ville, String rueNumero, String sector){
        setName(name);
        setRueNumero(rueNumero);
        setSector(sector);
        setVille(ville);
        setZoning(zoning);
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append("Zoning : " + getZoning());
        output.append("\nName : " + getName());
        output.append("\nSector : " + getSector());
        output.append((getRueNumero() == null)? "\nadresse non connue, " : "\nAddress : "+getRueNumero()+", ");
        output.append((getVille() == null)? " ville non connue" : getVille());
        return output.toString();
    }

    public String getAddress(){
        StringBuilder output = new StringBuilder();
        output.append((getRueNumero() == null)? "adresse non connue, " : getRueNumero()+", ");
        output.append((getVille() == null)? " ville non connue" : getVille());
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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) { this.ville = ville; }

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
}
