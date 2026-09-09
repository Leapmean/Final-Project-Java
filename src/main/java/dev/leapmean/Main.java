package dev.leapmean;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        SwingUtilities.invokeLater(Main::showLogin);
    }

    private static void showLogin() {
        JFrame frame = new JFrame("WonderReads - Login");
        frame.setSize(420, 260);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.add(new JLabel("Username")); form.add(username);
        form.add(new JLabel("Password")); form.add(password);

        JButton login = new JButton("Login");
        JButton signup = new JButton("Sign Up");
        form.add(login); form.add(signup);
        frame.add(form);

        AuthService auth = new AuthService();
        login.addActionListener(e -> {
            User user = auth.login(username.getText(), new String(password.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(frame, "Incorrect credentials.");
            } else {
                frame.dispose();
                new LibraryFrame(user).setVisible(true);
            }
        });

        signup.addActionListener(e -> showSignup(frame, auth));
        frame.setVisible(true);
    }

    private static void showSignup(JFrame parent, AuthService auth) {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirm = new JPasswordField();
        JComboBox<String> role = new JComboBox<>(new String[]{"USER", "ADMIN"});

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.add(new JLabel("New Username")); p.add(username);
        p.add(new JLabel("New Password")); p.add(password);
        p.add(new JLabel("Confirm Password")); p.add(confirm);
        p.add(new JLabel("Select Role")); p.add(role);

        if (JOptionPane.showConfirmDialog(parent, p, "Sign Up",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String pass = new String(password.getPassword());
        String conf = new String(confirm.getPassword());
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(parent, "Passwords do not match.");
            return;
        }

        boolean created = auth.signUp(username.getText(), pass, (String) role.getSelectedItem());
        JOptionPane.showMessageDialog(parent,
                created ? "Account created successfully." : "Username already exists.");
    }
}
