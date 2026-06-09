package com.agriferme.model;

public class Revenu {
    private String source;
    private Double montant;
    private String date;
    private String description;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}