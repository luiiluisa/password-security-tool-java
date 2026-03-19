package repository;

import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDBRepository implements UserRepository {

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users(username, birth_year, password_hash) VALUES (?, ?, ?)";

        try (Connection con = ConexiuneBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getBirthYear());
            ps.setString(3, user.getPasswordHash());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la salvare: " + e.getMessage());
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = ConexiuneBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("birth_year"),
                        rs.getString("password_hash")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la cautare: " + e.getMessage());
        }

        return null;
    }
}
