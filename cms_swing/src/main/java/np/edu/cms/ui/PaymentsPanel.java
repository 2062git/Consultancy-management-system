package np.edu.cms.ui;

import np.edu.cms.dao.DatabaseDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PaymentsPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JComboBox<String> studentBox = new JComboBox<>();
    private final JTextField amountField = new JTextField(12);
    private final JTextField dateField = new JTextField(12);
    private final JComboBox<String> methodBox = new JComboBox<>(
            new String[]{"Cash", "Bank", "Card", "Online"}
    );
    private final JTextField descriptionField = new JTextField(25);
    private int selectedId = -1;

    public PaymentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadStudents();
        loadData();
    }

    private JPanel createTop() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, c, 0, "Student:", studentBox);
        addField(panel, c, 1, "Amount:", amountField);
        addField(panel, c, 2, "Date:", dateField);
        addField(panel, c, 3, "Method:", methodBox);
        addField(panel, c, 4, "Description:", descriptionField);

        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");
        addField(panel, c, 5, "Actions:",
                UIUtils.createToolbar(save, delete, refresh, clear));

        save.addActionListener(event -> save());
        delete.addActionListener(event -> delete());
        refresh.addActionListener(event -> {
            loadStudents();
            loadData();
        });
        clear.addActionListener(event -> clearForm());
        table.getSelectionModel().addListSelectionListener(
                event -> loadSelected()
        );

        return panel;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints c,
            int row,
            String label,
            java.awt.Component component
    ) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(component, c);
    }

    private void loadStudents() {
        try {
            studentBox.removeAllItems();
            for (String value : dao.getComboValues(
                    "SELECT CONCAT(id, ' - ', full_name) "
                            + "FROM students ORDER BY full_name"
            )) {
                studentBox.addItem(value);
            }
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void loadData() {
        try {
            table.setModel(dao.getTableModel(
                    "SELECT p.id, s.full_name, p.amount, p.payment_date, "
                            + "p.method, p.description "
                            + "FROM payments p "
                            + "JOIN students s ON p.student_id = s.id "
                            + "ORDER BY p.id DESC",
                    new String[]{
                            "ID", "Student", "Amount", "Date", "Method",
                            "Description"
                    }
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
        selectStudent(value(row, 1));
        amountField.setText(value(row, 2));
        dateField.setText(value(row, 3));
        methodBox.setSelectedItem(value(row, 4));
        descriptionField.setText(value(row, 5));
    }

    private void selectStudent(String name) {
        for (int i = 0; i < studentBox.getItemCount(); i++) {
            if (studentBox.getItemAt(i).contains(name)) {
                studentBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private int selectedStudentId() {
        String value = (String) studentBox.getSelectedItem();
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value.split(" - ", 2)[0]);
    }

    private String value(int row, int column) {
        Object value = table.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void save() {
        try {
            int studentId = selectedStudentId();
            if (studentId == -1) {
                UIUtils.showInfo(this, "Create a student first.");
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            java.sql.Date date = dateField.getText().trim().isEmpty()
                    ? null
                    : java.sql.Date.valueOf(dateField.getText().trim());

            if (selectedId == -1) {
                dao.executeUpdate(
                        "INSERT INTO payments "
                                + "(student_id, amount, payment_date, method, "
                                + "description) VALUES (?, ?, ?, ?, ?)",
                        studentId,
                        amount,
                        date,
                        methodBox.getSelectedItem(),
                        descriptionField.getText().trim()
                );
            } else {
                dao.executeUpdate(
                        "UPDATE payments SET student_id = ?, amount = ?, "
                                + "payment_date = ?, method = ?, description = ? "
                                + "WHERE id = ?",
                        studentId,
                        amount,
                        date,
                        methodBox.getSelectedItem(),
                        descriptionField.getText().trim(),
                        selectedId
                );
            }

            UIUtils.showInfo(this, "Payment saved successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            UIUtils.showInfo(this, "Select a payment first.");
            return;
        }
        try {
            dao.executeUpdate(
                    "DELETE FROM payments WHERE id = ?",
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
        amountField.setText("");
        dateField.setText("");
        descriptionField.setText("");
        methodBox.setSelectedIndex(0);
    }
}
