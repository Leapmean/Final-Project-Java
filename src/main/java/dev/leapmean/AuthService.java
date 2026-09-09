package dev.leapmean;

import java.sql.*;

public class AuthService {
    public User login(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users WHERE username=? AND password=?";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean signUp(String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        String sql = "INSERT INTO users(username,password,role) VALUES(?,?,?)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
