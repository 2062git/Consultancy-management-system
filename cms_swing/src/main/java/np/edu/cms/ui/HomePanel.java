package np.edu.cms.ui;

import np.edu.cms.config.DatabaseConnection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HomePanel extends JPanel {

    private final JLabel students = new JLabel("0", JLabel.CENTER);
    private final JLabel universities = new JLabel("0", JLabel.CENTER);
    private final JLabel applications = new JLabel("0", JLabel.CENTER);
    private final JLabel payments = new JLabel("0", JLabel.CENTER);

    public HomePanel() {
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(createCard("Total Students", students));
        add(createCard("Universities", universities));
        add(createCard("Applications", applications));
        add(createCard("Payments", payments));

        refresh();
    }

    private JPanel createCard(String title, JLabel value) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        value.setFont(new Font("Segoe UI", Font.BOLD, 32));
        panel.add(value);
        panel.add(new JLabel("", JLabel.CENTER));
        return panel;
    }

    private void refresh() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            students.setText(count(statement, "students"));
            universities.setText(count(statement, "universities"));
            applications.setText(count(statement, "applications"));
            payments.setText(count(statement, "payments"));

        } catch (Exception exception) {
            students.setText("-");
            universities.setText("-");
            applications.setText("-");
            payments.setText("-");
        }
    }

    private String count(Statement statement, String table)
            throws Exception {

        try (ResultSet result = statement.executeQuery(
                "SELECT COUNT(*) FROM " + table
        )) {
            result.next();
            return String.valueOf(result.getInt(1));
        }
    }
}
