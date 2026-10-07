package np.edu.cms.ui;

import np.edu.cms.dao.DatabaseDAO;
import np.edu.cms.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.Map;

public class UniversitiesPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JPanel formPanel = new JPanel();
    private final User user;
    private Map<String, JTextField> fields;
    private int selectedId = -1;

    public UniversitiesPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");

        JPanel toolbar = UIUtils.createToolbar(
                save, delete, refresh, clear
        );

        fields = UIUtils.createForm(
                formPanel,
                new String[]{
                        "University Name",
                        "Country",
                        "City",
                        "Website",
                        "Status"
                }
        );

        JPanel top = new JPanel(new BorderLayout());
        top.add(toolbar, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        top.setBorder(BorderFactory.createTitledBorder("University Details"));

        add(top, BorderLayout.NORTH);
        add(UIUtils.tableScroll(table), BorderLayout.CENTER);

        save.addActionListener(event -> save());
        delete.addActionListener(event -> delete());
        refresh.addActionListener(event -> loadData());
        clear.addActionListener(event -> clearForm());
        table.getSelectionModel().addListSelectionListener(event -> loadSelected());

        if (!user.role().equals("ADMIN")) {
            save.setEnabled(false);
            delete.setEnabled(false);
        }

        loadData();
    }

    private void loadData() {
        try {
            table.setModel(dao.getTableModel(
                    "SELECT id, name, country, city, website, status "
                            + "FROM universities ORDER BY id DESC",
                    new String[]{
                            "ID", "University", "Country", "City",
                            "Website", "Status"
                    }
            ));
            selectedId = -1;
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
        fields.get("University Name").setText(value(row, 1));
        fields.get("Country").setText(value(row, 2));
        fields.get("City").setText(value(row, 3));
        fields.get("Website").setText(value(row, 4));
        fields.get("Status").setText(value(row, 5));
    }

    private String value(int row, int column) {
        Object value = table.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void save() {
        try {
            String name = fields.get("University Name").getText().trim();

            if (name.isEmpty()) {
                UIUtils.showInfo(this, "University Name is required.");
                return;
            }

            if (selectedId == -1) {
                dao.executeUpdate(
                        "INSERT INTO universities "
                                + "(name, country, city, website, status) "
                                + "VALUES (?, ?, ?, ?, ?)",
                        name,
                        fields.get("Country").getText().trim(),
                        fields.get("City").getText().trim(),
                        fields.get("Website").getText().trim(),
                        fields.get("Status").getText().trim()
                );
            } else {
                dao.executeUpdate(
                        "UPDATE universities SET name = ?, country = ?, "
                                + "city = ?, website = ?, status = ? "
                                + "WHERE id = ?",
                        name,
                        fields.get("Country").getText().trim(),
                        fields.get("City").getText().trim(),
                        fields.get("Website").getText().trim(),
                        fields.get("Status").getText().trim(),
                        selectedId
                );
            }

            UIUtils.showInfo(this, "University saved successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            UIUtils.showInfo(this, "Select a university first.");
            return;
        }

        try {
            dao.executeUpdate(
                    "DELETE FROM universities WHERE id = ?",
                    selectedId
            );
            UIUtils.showInfo(this, "University deleted successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();

        for (JTextField field : fields.values()) {
            field.setText("");
        }
    }
}
