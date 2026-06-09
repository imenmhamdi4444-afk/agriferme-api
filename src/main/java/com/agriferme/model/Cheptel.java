package com.agriferme.model;

public class Cheptel {
    private String nom;
    private String typeAnimal;
    private String dateNaissance;
    private String etatSante;
    private String maladie;
    private Double quantiteVendue;
    private Double prixUnitaire;
    private Double prixTotal;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTypeAnimal() { return typeAnimal; }
    public void setTypeAnimal(String typeAnimal) { this.typeAnimal = typeAnimal; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getEtatSante() { return etatSante; }
    public void setEtatSante(String etatSante) { this.etatSante = etatSante; }

    public String getMaladie() { return maladie; }
    public void setMaladie(String maladie) { this.maladie = maladie; }

    public Double getQuantiteVendue() { return quantiteVendue; }
    public void setQuantiteVendue(Double quantiteVendue) { this.quantiteVendue = quantiteVendue; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }
}