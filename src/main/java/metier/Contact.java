package metier;

public class Contact {
    private String nom;
    private String prenom;
    private long birthday;

    public Contact(String nom, String prenom, long birthday) {
        this.nom = nom;
        this.prenom = prenom;
        this.birthday = birthday;
    }

    public Contact() {
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

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
