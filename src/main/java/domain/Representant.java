package domain;

public class Representant {
    private String nom;
    private String prenom;
    private String relation;

    public Representant(String nom, String prenom, String relation) {
        this.nom = nom;
        this.prenom = prenom;
        this.relation = relation;
    }

    public Representant() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
