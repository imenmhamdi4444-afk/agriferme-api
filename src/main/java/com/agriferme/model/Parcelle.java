package com.agriferme.model;

public class Parcelle {
    private String nom;
    private Double surface;
    private String localisation;
    private String cultureActuelle;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getCultureActuelle() { return cultureActuelle; }
    public void setCultureActuelle(String cultureActuelle) { this.cultureActuelle = cultureActuelle; }
}