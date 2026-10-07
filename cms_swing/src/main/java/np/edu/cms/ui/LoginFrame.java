package np.edu.cms.ui;

import np.edu.cms.dao.LoginDAO;
import np.edu.cms.model.Session;
import np.edu.cms.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final LoginDAO loginDAO = new LoginDAO();

    public LoginFrame() {
        setTitle("Consultancy Management System - Login");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(createHeader(), BorderLayout.NORTH);
        add(createLoginPanel(), BorderLayout.CENTER);

        loginButton.addActionListener(event -> login());
        passwordField.addActionListener(event -> login());
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 20, 15, 20));

        JLabel title = new JLabel(
                "Consultancy Management System",
                JLabel.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "Java Swing + MySQL",
                JLabel.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(title, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        form.add(new JLabel("Username:"), constraints);

        usernameField.setPreferredSize(new Dimension(220, 32));
        constraints.gridx = 1;
        form.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        form.add(new JLabel("Password:"), constraints);

        passwordField.setPreferredSize(new Dimension(220, 32));
        constraints.gridx = 1;
        form.add(passwordField, constraints);

        loginButton.setPreferredSize(new Dimension(220, 35));
        UIUtils.styleButton(loginButton);

        constraints.gridx = 1;
        constraints.gridy = 2;
        form.add(loginButton, constraints);


        outer.add(form);
        return outer;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            User user = loginDAO.authenticate(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Session.login(user);
            dispose();
            new DashboardFrame().setVisible(true);

        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }
}
