package com.agriferme.model;

public class Stock {
    private String nomProduit;
    private Double quantite;
    private String unite;
    private Double seuilAlerte;
    private Double prixUnitaire;
    private Double depense;
    private Double prixTotal;

    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

    public Double getQuantite() { return quantite; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public Double getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(Double seuilAlerte) { this.seuilAlerte = seuilAlerte; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Double getDepense() { return depense; }
    public void setDepense(Double depense) { this.depense = depense; }

    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }
}