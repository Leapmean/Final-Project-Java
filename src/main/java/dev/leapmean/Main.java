package dev.leapmean;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing will fall back to its default look and feel.
        }
        Theme.applyGlobalDefaults();

        Database.initialize();
        SwingUtilities.invokeLater(Main::showLogin);
    }

    public static void showLogin() {
        JFrame frame = new JFrame("Kokoro Library - Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setMinimumSize(new Dimension(720, 560));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(new EmptyBorder(36, 36, 36, 36));

        JPanel card = createLoginCard(frame);
        root.add(card, new GridBagConstraints());

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static JPanel createLoginCard(JFrame frame) {
        JPanel card = Theme.createCard(42, 38);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 500));

        JPanel accent = new JPanel();
        accent.setBackground(Theme.GOLD);
        accent.setMaximumSize(new Dimension(64, 5));
        accent.setPreferredSize(new Dimension(64, 5));
        accent.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eyebrow = new JLabel("KOKORO");
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 12f));
        eyebrow.setForeground(Theme.GOLD);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Welcome back");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setForeground(Theme.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to manage the library catalogue");
        subtitle.setForeground(Theme.MUTED_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField username = new Theme.PlaceholderTextField("Enter your username");
        username.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        username.setPreferredSize(new Dimension(360, 42));
        Theme.styleField(username);

        JPasswordField password = new JPasswordField();
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        password.setPreferredSize(new Dimension(360, 42));
        Theme.styleField(password);

        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.setForeground(Theme.MUTED_TEXT);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton login = new JButton("Sign In");
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        login.setPreferredSize(new Dimension(360, 42));
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.stylePrimaryButton(login);

        JButton signup = new JButton("Create Account");
        signup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signup.setPreferredSize(new Dimension(360, 42));
        signup.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.styleSecondaryButton(signup);

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Theme.TEXT);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Theme.TEXT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accent);
        card.add(Box.createVerticalStrut(22));
        card.add(eyebrow);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(username);
        card.add(Box.createVerticalStrut(18));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(password);
        card.add(Box.createVerticalStrut(9));
        card.add(showPassword);
        card.add(Box.createVerticalGlue());
        card.add(login);
        card.add(Box.createVerticalStrut(10));
        card.add(signup);

        frame.getRootPane().setDefaultButton(login);

        char defaultEcho = password.getEchoChar();
        showPassword.addActionListener(e ->
                password.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho));

        AuthService auth = new AuthService();
        login.addActionListener(e -> {
            String name = username.getText().trim();
            String pass = new String(password.getPassword());

            if (name.isBlank() || pass.isBlank()) {
                showError(frame, "Enter your username and password.");
                return;
            }

            User user = auth.login(name, pass);
            if (user == null) {
                showError(frame, "Incorrect username or password.");
                return;
            }

            frame.dispose();
            new LibraryFrame(user).setVisible(true);
        });

        signup.addActionListener(e -> showSignup(frame, auth));
        return card;
    }

    private static void showSignup(JFrame parent, AuthService auth) {
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JPasswordField confirm = new JPasswordField(20);
        Theme.styleField(username);
        Theme.styleField(password);
        Theme.styleField(confirm);

        JPanel panel = Theme.createCard(18, 16);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, "Username", username);
        addField(panel, gbc, 1, "Password", password);
        addField(panel, gbc, 2, "Confirm Password", confirm);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Create User Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = username.getText().trim();
        String pass = new String(password.getPassword());
        String conf = new String(confirm.getPassword());

        if (name.isBlank() || pass.isBlank()) {
            showError(parent, "Username and password are required.");
            return;
        }

        if (pass.length() < 4) {
            showError(parent, "Password must contain at least 4 characters.");
            return;
        }

        if (!pass.equals(conf)) {
            showError(parent, "Passwords do not match.");
            return;
        }

        boolean created = auth.signUp(name, pass);
        JOptionPane.showMessageDialog(
                parent,
                created ? "Account created successfully. You can now sign in."
                        : "That username is already in use.",
                "Kokoro Library",
                created ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
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

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Kokoro Library",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
