package dev.leapmean;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BookDialog extends JDialog {
    private static final String[] GENRES = {
            "Fiction",
            "Fantasy",
            "Science Fiction",
            "Romance",
            "History",
            "Biography",
            "Education",
            "Technology",
            "Other"
    };

    private final JTextField titleField = new JTextField(24);
    private final JTextField authorField = new JTextField(24);
    private final JComboBox<String> genreBox = new JComboBox<>(GENRES);
    private final JTextField dateField = new JTextField(24);

    private BookData result;

    public BookDialog(Frame owner, String dialogTitle, BookData existing) {
        super(owner, dialogTitle, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        genreBox.setEditable(true);
        dateField.setText(LocalDate.now().toString());

        Theme.styleField(titleField);
        Theme.styleField(authorField);
        Theme.styleField(genreBox);
        Theme.styleField(dateField);

        if (existing != null) {
            titleField.setText(existing.title());
            authorField.setText(existing.author());
            genreBox.setSelectedItem(existing.genre());
            dateField.setText(existing.registeredDate());
        }

        JPanel content = Theme.createCard(26, 24);
        content.setLayout(new BorderLayout(0, 20));
        content.setBackground(Theme.SURFACE);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(Theme.SURFACE);

        JLabel eyebrow = new JLabel("KOKORO LIBRARY");
        eyebrow.setForeground(Theme.GOLD);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 10f));

        JLabel heading = new JLabel(dialogTitle);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 21f));
        heading.setForeground(Theme.PRIMARY);

        headingPanel.add(eyebrow);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(heading);
        content.add(headingPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(form, gbc, 0, "Book title", titleField);
        addField(form, gbc, 1, "Author", authorField);
        addField(form, gbc, 2, "Genre", genreBox);
        addField(form, gbc, 3, "Registered date", dateField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        JLabel formatHint = new JLabel("Use YYYY-MM-DD, for example 2026-09-11");
        formatHint.setForeground(Theme.MUTED_TEXT);
        form.add(formatHint, gbc);

        content.add(form, BorderLayout.CENTER);

        JButton cancel = new JButton("Cancel");
        JButton save = new JButton(existing == null ? "Add Book" : "Save Changes");
        Theme.styleSecondaryButton(cancel);
        Theme.stylePrimaryButton(save);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(Theme.SURFACE);
        actions.add(cancel);
        actions.add(save);
        content.add(actions, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> save());

        setContentPane(content);
        getRootPane().setDefaultButton(save);
        pack();
        setLocationRelativeTo(owner);
    }

    public BookData showDialog() {
        setVisible(true);
        return result;
    }

    private void save() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        Object selectedGenre = genreBox.getEditor().getItem();
        String genre = selectedGenre == null ? "" : selectedGenre.toString().trim();
        String date = dateField.getText().trim();

        if (title.isBlank()) {
            showValidation("Book title is required.");
            titleField.requestFocusInWindow();
            return;
        }

        if (author.isBlank()) {
            showValidation("Author is required.");
            authorField.requestFocusInWindow();
            return;
        }

        if (genre.isBlank()) {
            showValidation("Genre is required.");
            genreBox.requestFocusInWindow();
            return;
        }

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            showValidation("Registered date must use YYYY-MM-DD.");
            dateField.requestFocusInWindow();
            return;
        }

        result = new BookData(title, author, genre, date);
        dispose();
    }

    private void showValidation(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Check Book Details",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private static void addField(JPanel panel, GridBagConstraints gbc, int row,
                                 String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setForeground(Theme.TEXT);
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    public record BookData(String title, String author, String genre, String registeredDate) {
    }
}
