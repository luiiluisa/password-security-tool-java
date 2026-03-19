package domain;

public class User {
    private int id;
    private String username;
    private String birthYear;
    private String passwordHash;

    public User(int id, String username, String birthYear, String passwordHash) {
        this.id = id;
        this.username = username;
        this.birthYear = birthYear;
        this.passwordHash = passwordHash;
    }

    public User(String username, String birthYear, String passwordHash) {
        this.username = username;
        this.birthYear = birthYear;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}