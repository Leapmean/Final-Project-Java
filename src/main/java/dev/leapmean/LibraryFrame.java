package dev.leapmean;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LibraryFrame extends JFrame {
    private final User currentUser;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Genre", "Registered Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new Theme.PlaceholderTextField("Search title, author or genre...");
    private final JComboBox<String> genreFilter = new JComboBox<>();
    private final JLabel bookCountLabel = new JLabel("0 books");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");

    public LibraryFrame(User user) {
        this.currentUser = user;

        setTitle("Kokoro Library - Book Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        setContentPane(createContent());
        configureTable();
        configureFiltering();
        loadBooks();
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(new EmptyBorder(28, 34, 24, 34));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainArea(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel brand = new JLabel("KOKORO LIBRARY");
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 11f));
        brand.setForeground(Theme.GOLD);

        JLabel title = new JLabel("Book Management");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 27f));
        title.setForeground(Theme.PRIMARY);

        JLabel subtitle = new JLabel("A simple view of your library catalogue");
        subtitle.setForeground(Theme.MUTED_TEXT);

        titles.add(brand);
        titles.add(Box.createVerticalStrut(5));
        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        JPanel account = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        account.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser.getUsername() + "  ·  " + currentUser.getRole());
        userLabel.setForeground(Theme.MUTED_TEXT);

        JButton logoutButton = new JButton("Logout");
        Theme.styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> logout());

        account.add(userLabel);
        account.add(logoutButton);

        header.add(titles, BorderLayout.WEST);
        header.add(account, BorderLayout.EAST);
        return header;
    }

    private JComponent createMainArea() {
        JPanel card = Theme.createCard(22, 22);
        card.setLayout(new BorderLayout(0, 18));
        card.setBackground(Theme.SURFACE);

        JPanel toolbar = new JPanel(new BorderLayout(14, 0));
        toolbar.setBackground(Theme.SURFACE);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        filters.setBackground(Theme.SURFACE);

        searchField.setPreferredSize(new Dimension(310, 38));
        Theme.styleField(searchField);

        genreFilter.setPreferredSize(new Dimension(155, 38));
        genreFilter.addItem("All Genres");
        Theme.styleField(genreFilter);

        filters.add(searchField);
        filters.add(genreFilter);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(Theme.SURFACE);

        JButton refreshButton = new JButton("Refresh");
        Theme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> loadBooks());
        actions.add(refreshButton);

        if (currentUser.isAdmin()) {
            JButton addButton = new JButton("+ Add Book");
            Theme.stylePrimaryButton(addButton);
            addButton.addActionListener(e -> addBook());

            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            Theme.styleSecondaryButton(editButton);
            Theme.styleDangerButton(deleteButton);
            editButton.addActionListener(e -> updateBook());
            deleteButton.addActionListener(e -> deleteBook());

            actions.add(editButton);
            actions.add(deleteButton);
            actions.add(addButton);
        }

        toolbar.add(filters, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        scrollPane.getViewport().setBackground(Theme.SURFACE);

        card.add(toolbar, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel permissionLabel = new JLabel(
                currentUser.isAdmin()
                        ? "Administrator access · add, edit and delete enabled"
                        : "User access · view and search books"
        );
        permissionLabel.setForeground(Theme.MUTED_TEXT);

        bookCountLabel.setForeground(Theme.PRIMARY);
        bookCountLabel.setFont(bookCountLabel.getFont().deriveFont(Font.BOLD));

        footer.add(permissionLabel, BorderLayout.WEST);
        footer.add(bookCountLabel, BorderLayout.EAST);
        return footer;
    }

    private void configureTable() {
        table.setRowSorter(sorter);
        table.setRowHeight(42);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEA, 0xE4, 0xD9));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Theme.SURFACE);
        table.setForeground(Theme.TEXT);
        table.setSelectionBackground(Theme.SAGE_LIGHT);
        table.setSelectionForeground(Theme.TEXT);
        table.getTableHeader().setBackground(Theme.SAGE_LIGHT);
        table.getTableHeader().setForeground(Theme.PRIMARY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));

        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(0).setPreferredWidth(50);
        columns.getColumn(0).setMaxWidth(70);
        columns.getColumn(1).setPreferredWidth(290);
        columns.getColumn(2).setPreferredWidth(210);
        columns.getColumn(3).setPreferredWidth(160);
        columns.getColumn(4).setPreferredWidth(150);

        if (currentUser.isAdmin()) {
            table.getSelectionModel().addListSelectionListener(e -> {
                boolean selected = table.getSelectedRow() >= 0;
                editButton.setEnabled(selected);
                deleteButton.setEnabled(selected);
            });
        }
    }

    private void configureFiltering() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        genreFilter.addActionListener(e -> applyFilters());
    }

    private void applyFilters() {
        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();

        String search = searchField.getText().trim();
        if (!search.isBlank()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(search), 1, 2, 3));
        }

        Object selected = genreFilter.getSelectedItem();
        if (selected != null && !"All Genres".equals(selected.toString())) {
            filters.add(RowFilter.regexFilter(
                    "(?i)^" + Pattern.quote(selected.toString()) + "$",
                    3
            ));
        }

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        updateBookCount();
    }

    private void loadBooks() {
        model.setRowCount(0);
        String sql = "SELECT id,title,author,genre,registered_date FROM books ORDER BY id";

        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("registered_date")
                });
            }

            rebuildGenreFilter();
            applyFilters();
        } catch (SQLException e) {
            showError("Unable to load books: " + e.getMessage());
        }
    }

    private void rebuildGenreFilter() {
        Object previous = genreFilter.getSelectedItem();
        List<String> genres = new ArrayList<>();

        for (int row = 0; row < model.getRowCount(); row++) {
            String genre = String.valueOf(model.getValueAt(row, 3));
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }

        genres.sort(String.CASE_INSENSITIVE_ORDER);

        genreFilter.removeAllItems();
        genreFilter.addItem("All Genres");
        for (String genre : genres) {
            genreFilter.addItem(genre);
        }

        if (previous != null) {
            genreFilter.setSelectedItem(previous);
            if (genreFilter.getSelectedIndex() < 0) {
                genreFilter.setSelectedIndex(0);
            }
        }
    }

    private void addBook() {
        if (!requireAdmin()) {
            return;
        }

        BookDialog.BookData data = new BookDialog(this, "Add New Book", null).showDialog();
        if (data == null) {
            return;
        }

        String sql = "INSERT INTO books(title,author,genre,registered_date) VALUES(?,?,?,?)";

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, data.title());
            ps.setString(2, data.author());
            ps.setString(3, data.genre());
            ps.setString(4, data.registeredDate());
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) {
            showError("Unable to add book: " + e.getMessage());
        }
    }

    private void updateBook() {
        if (!requireAdmin()) {
            return;
        }

        int modelRow = selectedModelRow();
        if (modelRow < 0) {
            return;
        }

        int id = (int) model.getValueAt(modelRow, 0);
        BookDialog.BookData existing = new BookDialog.BookData(
                String.valueOf(model.getValueAt(modelRow, 1)),
                String.valueOf(model.getValueAt(modelRow, 2)),
                String.valueOf(model.getValueAt(modelRow, 3)),
                String.valueOf(model.getValueAt(modelRow, 4))
        );

        BookDialog.BookData updated = new BookDialog(this, "Edit Book", existing).showDialog();
        if (updated == null) {
            return;
        }

        String sql = "UPDATE books SET title=?,author=?,genre=?,registered_date=? WHERE id=?";

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, updated.title());
            ps.setString(2, updated.author());
            ps.setString(3, updated.genre());
            ps.setString(4, updated.registeredDate());
            ps.setInt(5, id);
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) {
            showError("Unable to update book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        if (!requireAdmin()) {
            return;
        }

        int modelRow = selectedModelRow();
        if (modelRow < 0) {
            return;
        }

        int id = (int) model.getValueAt(modelRow, 0);
        String title = String.valueOf(model.getValueAt(modelRow, 1));

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + title + "\"? This cannot be undone.",
                "Delete Book",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            loadBooks();
        } catch (SQLException e) {
            showError("Unable to delete book: " + e.getMessage());
        }
    }

    private int selectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            showError("Select a book first.");
            return -1;
        }
        return table.convertRowIndexToModel(viewRow);
    }

    private boolean requireAdmin() {
        if (currentUser.isAdmin()) {
            return true;
        }

        showError("Administrator access is required for this action.");
        return false;
    }

    private void updateBookCount() {
        int visible = table.getRowCount();
        int total = model.getRowCount();

        if (visible == total) {
            bookCountLabel.setText(total + (total == 1 ? " book" : " books"));
        } else {
            bookCountLabel.setText(visible + " of " + total + " books");
        }
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Log out of Kokoro Library?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            Main.showLogin();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Kokoro Library",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
