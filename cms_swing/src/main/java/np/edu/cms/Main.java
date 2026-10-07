package np.edu.cms;

import np.edu.cms.config.DatabaseConnection;
import np.edu.cms.ui.LoginFrame;
import np.edu.cms.ui.UIUtils;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.initializeDatabase();
                new LoginFrame().setVisible(true);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not connect to MySQL.\n\n"
                                + exception.getMessage()
                                + "\n\nCheck MySQL and your credentials in "
                                + "DatabaseConnection.java.",
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
