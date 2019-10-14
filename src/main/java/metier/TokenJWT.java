package metier;

public class TokenJWT {
    private String token;

    public TokenJWT(String token) {
        this.token = token;
    }

    public TokenJWT() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
