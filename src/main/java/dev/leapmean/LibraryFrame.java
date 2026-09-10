package dev.leapmean;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class LibraryFrame extends JFrame {
    private final User currentUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Genre", "Registered Date"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public LibraryFrame(User user) {
        this.currentUser = user;
        setTitle("Kokoro- Book Management");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem refresh = new JMenuItem("Display");
        JMenuItem add = new JMenuItem("New");
        JMenuItem update = new JMenuItem("Update");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem exit = new JMenuItem("Exit");

        refresh.addActionListener(e -> loadBooks());
        add.addActionListener(e -> addBook());
        update.addActionListener(e -> updateBook());
        delete.addActionListener(e -> deleteBook());
        exit.addActionListener(e -> System.exit(0));

        file.add(add); file.add(refresh);
        if (currentUser.isAdmin()) {
            file.add(update); file.add(delete);
        }
        file.addSeparator(); file.add(exit);
        bar.add(file);
        setJMenuBar(bar);

        add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel status = new JLabel(" Logged in as: " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        add(status, BorderLayout.SOUTH);

        loadBooks();
    }

    private void loadBooks() {
        model.setRowCount(0);
        String sql = "SELECT id,title,author,genre,registered_date FROM books ORDER BY id";
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("title"),
                        rs.getString("author"), rs.getString("genre"),
                        rs.getString("registered_date")});
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void addBook() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField genre = new JTextField();
        JTextField date = new JTextField(LocalDate.now().toString());
        JPanel p = formPanel(title, author, genre, date);
        if (JOptionPane.showConfirmDialog(this, p, "Book Registration",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        if (title.getText().isBlank() || author.getText().isBlank() || genre.getText().isBlank()) {
            showError("Title, author and genre are required.");
            return;
        }

        String sql = "INSERT INTO books(title,author,genre,registered_date) VALUES(?,?,?,?)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.getText().trim());
            ps.setString(2, author.getText().trim());
            ps.setString(3, genre.getText().trim());
            ps.setString(4, date.getText().trim());
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void updateBook() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a book first."); return; }

        int id = (int) model.getValueAt(row, 0);
        JTextField title = new JTextField((String) model.getValueAt(row, 1));
        JTextField author = new JTextField((String) model.getValueAt(row, 2));
        JTextField genre = new JTextField((String) model.getValueAt(row, 3));
        JTextField date = new JTextField((String) model.getValueAt(row, 4));
        JPanel p = formPanel(title, author, genre, date);

        if (JOptionPane.showConfirmDialog(this, p, "Update Book",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String sql = "UPDATE books SET title=?,author=?,genre=?,registered_date=? WHERE id=?";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.getText().trim());
            ps.setString(2, author.getText().trim());
            ps.setString(3, genre.getText().trim());
            ps.setString(4, date.getText().trim());
            ps.setInt(5, id);
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a book first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String title = (String) model.getValueAt(row, 1);

        if (JOptionPane.showConfirmDialog(this, "Delete \"" + title + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private JPanel formPanel(JTextField title, JTextField author, JTextField genre, JTextField date) {
        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.add(new JLabel("Book Title")); p.add(title);
        p.add(new JLabel("Author")); p.add(author);
        p.add(new JLabel("Book Genre")); p.add(genre);
        p.add(new JLabel("Registered Date")); p.add(date);
        return p;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kokoro", JOptionPane.ERROR_MESSAGE);
    }
}
