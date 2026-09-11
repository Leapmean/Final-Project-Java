package dev.leapmean;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Small Swing-only theme helper.
 *
 * No third-party look-and-feel library is used. Everything here is built from
 * standard javax.swing and java.awt classes.
 */
public final class Theme {
    public static final Color BACKGROUND = new Color(0xF2, 0xEC, 0xE1);
    public static final Color SURFACE = new Color(0xFF, 0xFC, 0xF5);
    public static final Color SURFACE_ALT = new Color(0xF7, 0xF2, 0xE8);
    public static final Color PRIMARY = new Color(0x2F, 0x55, 0x42);
    public static final Color PRIMARY_HOVER = new Color(0x26, 0x48, 0x37);
    public static final Color PRIMARY_PRESSED = new Color(0x1E, 0x3B, 0x2E);
    public static final Color SAGE = new Color(0x7C, 0x90, 0x79);
    public static final Color SAGE_LIGHT = new Color(0xE3, 0xE9, 0xDE);
    public static final Color GOLD = new Color(0xB8, 0x86, 0x45);
    public static final Color TEXT = new Color(0x24, 0x2D, 0x27);
    public static final Color MUTED_TEXT = new Color(0x70, 0x78, 0x70);
    public static final Color BORDER = new Color(0xD8, 0xD0, 0xC2);
    public static final Color DANGER = new Color(0x9A, 0x59, 0x45);
    public static final Color DANGER_HOVER = new Color(0x86, 0x49, 0x38);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private Theme() {
    }

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", BASE_FONT);
        UIManager.put("Button.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", BASE_FONT);
        UIManager.put("PasswordField.font", BASE_FONT);
        UIManager.put("ComboBox.font", BASE_FONT);
        UIManager.put("Table.font", BASE_FONT);
        UIManager.put("TableHeader.font", BASE_FONT.deriveFont(Font.BOLD));
        UIManager.put("CheckBox.font", BASE_FONT.deriveFont(13f));
        UIManager.put("OptionPane.messageFont", BASE_FONT);
        UIManager.put("OptionPane.buttonFont", BASE_FONT.deriveFont(Font.BOLD));
    }

    public static JPanel createCard(int horizontalPadding, int verticalPadding) {
        RoundedPanel panel = new RoundedPanel(24, SURFACE, BORDER);
        panel.setBorder(new EmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
        return panel;
    }

    public static void stylePrimaryButton(AbstractButton button) {
        styleButton(button, PRIMARY, Color.WHITE, PRIMARY_HOVER, PRIMARY_PRESSED,
                new Color(0, 0, 0, 0));
    }

    public static void styleSecondaryButton(AbstractButton button) {
        styleButton(button, SURFACE_ALT, PRIMARY, SAGE_LIGHT, new Color(0xD8, 0xE1, 0xD3), BORDER);
    }

    public static void styleDangerButton(AbstractButton button) {
        styleButton(button, DANGER, Color.WHITE, DANGER_HOVER, new Color(0x73, 0x3D, 0x30),
                new Color(0, 0, 0, 0));
    }

    private static void styleButton(AbstractButton button, Color normal, Color foreground,
                                    Color hover, Color pressed, Color borderColor) {
        button.setForeground(foreground);
        button.setFont(BASE_FONT.deriveFont(Font.BOLD));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 17, 10, 17));
        button.setUI(new RoundedButtonUI(normal, hover, pressed, borderColor));
    }

    private static final class RoundedButtonUI extends BasicButtonUI {
        private final Color normal;
        private final Color hover;
        private final Color pressed;
        private final Color border;

        private RoundedButtonUI(Color normal, Color hover, Color pressed, Color border) {
            this.normal = normal;
            this.hover = hover;
            this.pressed = pressed;
            this.border = border;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();

            Color fill = normal;
            if (!button.isEnabled()) {
                fill = new Color(normal.getRed(), normal.getGreen(), normal.getBlue(), 120);
            } else if (model.isPressed()) {
                fill = pressed;
            } else if (model.isRollover()) {
                fill = hover;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 16, 16);
            if (border.getAlpha() > 0) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 16, 16);
            }
            g2.dispose();

            super.paint(g, c);
        }
    }

    public static void styleField(JComponent field) {
        field.setBackground(SURFACE_ALT);
        field.setForeground(TEXT);
        field.setFont(BASE_FONT);
        Border line = new LineBorder(BORDER, 1, true);
        Border padding = new EmptyBorder(8, 11, 8, 11);
        field.setBorder(new CompoundBorder(line, padding));
    }

    /** Simple Swing text field that paints prompt text when empty. */
    public static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty() || placeholder == null || placeholder.isBlank()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(MUTED_TEXT);
            g2.setFont(getFont().deriveFont(Font.PLAIN));
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }

    /** Rounded card panel painted with standard Swing/AWT only. */
    private static final class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color border;

        private RoundedPanel(int arc, Color fill, Color border) {
            this.arc = arc;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
