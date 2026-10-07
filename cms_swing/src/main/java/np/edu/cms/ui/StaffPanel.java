package np.edu.cms.ui;

import np.edu.cms.dao.DatabaseDAO;
import np.edu.cms.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;

public class StaffPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JPanel formPanel = new JPanel();
    private final User currentUser;
    private final JComboBox<String> roleBox = new JComboBox<>(
            new String[]{"ADMIN", "COUNSELOR"}
    );
    private Map<String, JTextField> fields;
    private int selectedId = -1;

    public StaffPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");

        fields = UIUtils.createForm(
                formPanel,
                new String[]{"Username", "Password", "Full Name"}
        );

        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        rolePanel.add(new javax.swing.JLabel("Role:"));
        rolePanel.add(roleBox);

        JPanel top = new JPanel(new BorderLayout());
        top.add(UIUtils.createToolbar(save, delete, refresh, clear),
                BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        top.add(rolePanel, BorderLayout.SOUTH);
        top.setBorder(BorderFactory.createTitledBorder("Staff Details"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        save.addActionListener(event -> save());
        delete.addActionListener(event -> delete());
        refresh.addActionListener(event -> loadData());
        clear.addActionListener(event -> clearForm());
        table.getSelectionModel().addListSelectionListener(
                event -> loadSelected()
        );

        boolean admin = currentUser.role().equals("ADMIN");
        save.setEnabled(admin);
        delete.setEnabled(admin);
        formPanel.setEnabled(admin);

        if (!admin) {
            UIUtils.showInfo(
                    this,
                    "Only administrators can manage staff accounts."
            );
        }

        loadData();
    }

    private void loadData() {
        try {
            table.setModel(dao.getTableModel(
                    "SELECT id, username, full_name, role FROM users "
                            + "ORDER BY id DESC",
                    new String[]{"ID", "Username", "Full Name", "Role"}
            ));
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedId = Integer.parseInt(table.getValueAt(row, 0).toString());
        fields.get("Username").setText(value(row, 1));
        fields.get("Password").setText("");
        fields.get("Full Name").setText(value(row, 2));
        roleBox.setSelectedItem(value(row, 3));
    }

    private String value(int row, int column) {
        Object value = table.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void save() {
        if (!currentUser.role().equals("ADMIN")) {
            UIUtils.showInfo(this, "Administrator access is required.");
            return;
        }

        try {
            String username = fields.get("Username").getText().trim();
            String password = fields.get("Password").getText().trim();
            String fullName = fields.get("Full Name").getText().trim();

            if (username.isEmpty() || fullName.isEmpty()) {
                UIUtils.showInfo(this, "Username and Full Name are required.");
                return;
            }

            if (selectedId == -1) {
                if (password.isEmpty()) {
                    UIUtils.showInfo(this, "Password is required for a new user.");
                    return;
                }

                dao.executeUpdate(
                        "INSERT INTO users "
                                + "(username, password, full_name, role) "
                                + "VALUES (?, ?, ?, ?)",
                        username,
                        password,
                        fullName,
                        roleBox.getSelectedItem()
                );
            } else {
                if (password.isEmpty()) {
                    dao.executeUpdate(
                            "UPDATE users SET username = ?, full_name = ?, "
                                    + "role = ? WHERE id = ?",
                            username,
                            fullName,
                            roleBox.getSelectedItem(),
                            selectedId
                    );
                } else {
                    dao.executeUpdate(
                            "UPDATE users SET username = ?, password = ?, "
                                    + "full_name = ?, role = ? WHERE id = ?",
                            username,
                            password,
                            fullName,
                            roleBox.getSelectedItem(),
                            selectedId
                    );
                }
            }

            UIUtils.showInfo(this, "Staff account saved successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void delete() {
        if (!currentUser.role().equals("ADMIN")) {
            UIUtils.showInfo(this, "Administrator access is required.");
            return;
        }

        if (selectedId == -1) {
            UIUtils.showInfo(this, "Select a staff account first.");
            return;
        }

        if (selectedId == currentUser.id()) {
            UIUtils.showInfo(this, "You cannot delete your own account.");
            return;
        }

        try {
            dao.executeUpdate(
                    "DELETE FROM users WHERE id = ?",
                    selectedId
            );
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        fields.get("Username").setText("");
        fields.get("Password").setText("");
        fields.get("Full Name").setText("");
        roleBox.setSelectedIndex(0);
    }
}
