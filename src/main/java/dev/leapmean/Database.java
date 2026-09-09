package dev.leapmean;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:wonderreads.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found.", e);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL)";
        String books = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, author TEXT NOT NULL, genre TEXT NOT NULL, " +
                "registered_date TEXT NOT NULL)";

        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(users);
            s.execute(books);
            seedAdmin(c);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed.", e);
        }
    }

    private static void seedAdmin(Connection c) throws SQLException {
        String sql = "INSERT OR IGNORE INTO users(username,password,role) VALUES(?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "ADMIN");
            ps.executeUpdate();
        }
    }
}
