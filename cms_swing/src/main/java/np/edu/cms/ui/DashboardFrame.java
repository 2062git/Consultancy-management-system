package np.edu.cms.ui;

import np.edu.cms.model.Session;
import np.edu.cms.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        User user = Session.getCurrentUser();

        setTitle("Consultancy Management System - Dashboard");
        setSize(1250, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(createHeader(user), BorderLayout.NORTH);
        add(createTabs(user), BorderLayout.CENTER);
    }

    private JPanel createHeader(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Consultancy Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        String userText = user.fullName()
                + " ("
                + user.role()
                + ")";

        JLabel loggedIn = new JLabel(userText);
        loggedIn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logout = new JButton("Logout");
        UIUtils.styleButton(logout);
        logout.addActionListener(event -> logout());

        JPanel right = new JPanel(new GridLayout(1, 2, 10, 0));
        right.add(loggedIn);
        right.add(logout);

        panel.add(title, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane createTabs(User user) {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Dashboard", new HomePanel());
        tabs.addTab("Students", new StudentsPanel());
        tabs.addTab("Universities", new UniversitiesPanel(user));
        tabs.addTab("Applications", new ApplicationsPanel());
        tabs.addTab("Appointments", new AppointmentsPanel());
        tabs.addTab("Payments", new PaymentsPanel());
        tabs.addTab("Documents", new DocumentsPanel());
        tabs.addTab("Staff", new StaffPanel(user));

        return tabs;
    }

    private void logout() {
        Session.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
