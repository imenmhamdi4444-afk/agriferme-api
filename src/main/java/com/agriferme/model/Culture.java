package com.agriferme.model;

public class Culture {
    private String nom;
    private String dateSemis;
    private String dateRecoltePrevue;
    private Integer parcelleId;
    private String parcelleNom;
    private String statut;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDateSemis() { return dateSemis; }
    public void setDateSemis(String dateSemis) { this.dateSemis = dateSemis; }

    public String getDateRecoltePrevue() { return dateRecoltePrevue; }
    public void setDateRecoltePrevue(String dateRecoltePrevue) { this.dateRecoltePrevue = dateRecoltePrevue; }

    public Integer getParcelleId() { return parcelleId; }
    public void setParcelleId(Integer parcelleId) { this.parcelleId = parcelleId; }

    public String getParcelleNom() { return parcelleNom; }
    public void setParcelleNom(String parcelleNom) { this.parcelleNom = parcelleNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}