package dev.leapmean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    public User login(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users WHERE username=? AND password=?";

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to sign in.", e);
        }

        return null;
    }

    public boolean signUp(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, "USER");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
