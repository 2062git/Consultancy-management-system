package np.edu.cms.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UIUtils {

    private UIUtils() {
    }

    public static void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
    }

    public static JPanel createToolbar(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        for (JButton button : buttons) {
            styleButton(button);
            panel.add(button);
        }

        return panel;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        return new JScrollPane(table);
    }

    public static Map<String, JTextField> createForm(
            JPanel panel,
            String[] labels
    ) {
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<String, JTextField> fields = new LinkedHashMap<>();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.weightx = 0;
            panel.add(new JLabel(labels[i] + ":"), constraints);

            JTextField field = new JTextField(25);
            fields.put(labels[i], field);

            constraints.gridx = 1;
            constraints.weightx = 1;
            panel.add(field, constraints);
        }

        return fields;
    }

    public static void showError(
            java.awt.Component parent,
            Exception exception
    ) {
        JOptionPane.showMessageDialog(
                parent,
                exception.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showInfo(
            java.awt.Component parent,
            String message
    ) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
