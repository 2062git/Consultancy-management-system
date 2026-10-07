
        package np.edu.cms.ui;

import np.edu.cms.dao.DatabaseDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.Map;

public class StudentsPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JPanel formPanel = new JPanel();

    private Map<String, JTextField> fields;
    private int selectedId = -1;

    public StudentsPanel() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buttons
        JButton add = new JButton("New");
        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");

        // Student input fields
        fields = UIUtils.createForm(
                formPanel,
                new String[]{
                        "Full Name",
                        "Email",
                        "Phone",
                        "Address",
                        "Education"
                }
        );

        // Top section
        JPanel top = new JPanel(new BorderLayout());

        // Buttons at the top
        top.add(
                UIUtils.createToolbar(
                        add,
                        save,
                        delete,
                        refresh,
                        clear
                ),
                BorderLayout.NORTH
        );

        // Student form below buttons
        top.add(formPanel, BorderLayout.CENTER);

        top.setBorder(
                BorderFactory.createTitledBorder("Student Details")
        );

        // Add top section and table
        add(top, BorderLayout.NORTH);
        add(UIUtils.tableScroll(table), BorderLayout.CENTER);

        // Button actions
        add.addActionListener(event -> clearForm());

        clear.addActionListener(event -> clearForm());

        refresh.addActionListener(event -> loadData());

        save.addActionListener(event -> save());

        delete.addActionListener(event -> delete());

        // Table selection
        table.getSelectionModel().addListSelectionListener(
                event -> loadSelected()
        );

        // Load existing students
        loadData();
    }

    // Load students from database
    private void loadData() {

        try {

            table.setModel(
                    dao.getTableModel(
                            "SELECT id, full_name, email, phone, "
                                    + "address, education "
                                    + "FROM students "
                                    + "ORDER BY id DESC",

                            new String[]{
                                    "ID",
                                    "Full Name",
                                    "Email",
                                    "Phone",
                                    "Address",
                                    "Education"
                            }
                    )
            );

            selectedId = -1;

        } catch (Exception exception) {

            UIUtils.showError(this, exception);
        }
    }

    // Load selected student into the form
    private void loadSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        selectedId = Integer.parseInt(
                table.getValueAt(row, 0).toString()
        );

        fields.get("Full Name")
                .setText(value(row, 1));

        fields.get("Email")
                .setText(value(row, 2));

        fields.get("Phone")
                .setText(value(row, 3));

        fields.get("Address")
                .setText(value(row, 4));

        fields.get("Education")
                .setText(value(row, 5));
    }

    // Get table value safely
    private String value(int row, int column) {

        Object value = table.getValueAt(row, column);

        return value == null
                ? ""
                : value.toString();
    }

    // Save new student or update existing student
    private void save() {

        try {

            String fullName =
                    fields.get("Full Name")
                            .getText()
                            .trim();

            if (fullName.isEmpty()) {

                UIUtils.showInfo(
                        this,
                        "Full Name is required."
                );

                return;
            }

            // Add new student
            if (selectedId == -1) {

                dao.executeUpdate(
                        "INSERT INTO students "
                                + "(full_name, email, phone, address, education) "
                                + "VALUES (?, ?, ?, ?, ?)",

                        fullName,

                        fields.get("Email")
                                .getText()
                                .trim(),

                        fields.get("Phone")
                                .getText()
                                .trim(),

                        fields.get("Address")
                                .getText()
                                .trim(),

                        fields.get("Education")
                                .getText()
                                .trim()
                );

            }

            // Update existing student
            else {

                dao.executeUpdate(
                        "UPDATE students SET "
                                + "full_name = ?, "
                                + "email = ?, "
                                + "phone = ?, "
                                + "address = ?, "
                                + "education = ? "
                                + "WHERE id = ?",

                        fullName,

                        fields.get("Email")
                                .getText()
                                .trim(),

                        fields.get("Phone")
                                .getText()
                                .trim(),

                        fields.get("Address")
                                .getText()
                                .trim(),

                        fields.get("Education")
                                .getText()
                                .trim(),

                        selectedId
                );
            }

            UIUtils.showInfo(
                    this,
                    "Student saved successfully."
            );

            clearForm();
            loadData();

        } catch (Exception exception) {

            UIUtils.showError(this, exception);
        }
    }

    // Delete selected student
    private void delete() {

        if (selectedId == -1) {

            UIUtils.showInfo(
                    this,
                    "Select a student first."
            );

            return;
        }

        try {

            dao.executeUpdate(
                    "DELETE FROM students WHERE id = ?",
                    selectedId
            );

            UIUtils.showInfo(
                    this,
                    "Student deleted successfully."
            );

            clearForm();
            loadData();

        } catch (Exception exception) {

            UIUtils.showError(this, exception);
        }
    }

    // Clear form
    private void clearForm() {

        selectedId = -1;

        table.clearSelection();

        for (JTextField field : fields.values()) {

            field.setText("");
        }
    }
}

