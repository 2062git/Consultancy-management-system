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

public class AppointmentsPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JComboBox<String> studentBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(12);
    private final JTextField timeField = new JTextField(10);
    private final JTextField purposeField = new JTextField(20);
    private final JComboBox<String> statusBox = new JComboBox<>(
            new String[]{"Scheduled", "Completed", "Cancelled"}
    );

    private int selectedId = -1;

    public AppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadStudents();
        loadData();
    }

    private JPanel createTop() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                "Appointment Details"
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, c, 0, "Student:", studentBox);
        addField(panel, c, 1, "Date:", dateField);
        addField(panel, c, 2, "Time:", timeField);
        addField(panel, c, 3, "Purpose:", purposeField);
        addField(panel, c, 4, "Status:", statusBox);

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
                    "SELECT a.id, s.full_name, a.appointment_date, "
                            + "a.appointment_time, a.purpose, a.status "
                            + "FROM appointments a "
                            + "JOIN students s ON a.student_id = s.id "
                            + "ORDER BY a.id DESC",
                    new String[]{
                            "ID", "Student", "Date", "Time", "Purpose", "Status"
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
        dateField.setText(value(row, 2));
        timeField.setText(value(row, 3));
        purposeField.setText(value(row, 4));
        statusBox.setSelectedItem(value(row, 5));
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

            java.sql.Date date = dateField.getText().trim().isEmpty()
                    ? null
                    : java.sql.Date.valueOf(dateField.getText().trim());

            if (selectedId == -1) {
                dao.executeUpdate(
                        "INSERT INTO appointments "
                                + "(student_id, appointment_date, "
                                + "appointment_time, purpose, status) "
                                + "VALUES (?, ?, ?, ?, ?)",
                        studentId,
                        date,
                        timeField.getText().trim(),
                        purposeField.getText().trim(),
                        statusBox.getSelectedItem()
                );
            } else {
                dao.executeUpdate(
                        "UPDATE appointments SET student_id = ?, "
                                + "appointment_date = ?, appointment_time = ?, "
                                + "purpose = ?, status = ? WHERE id = ?",
                        studentId,
                        date,
                        timeField.getText().trim(),
                        purposeField.getText().trim(),
                        statusBox.getSelectedItem(),
                        selectedId
                );
            }

            UIUtils.showInfo(this, "Appointment saved successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            UIUtils.showInfo(this, "Select an appointment first.");
            return;
        }
        try {
            dao.executeUpdate(
                    "DELETE FROM appointments WHERE id = ?",
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
        dateField.setText("");
        timeField.setText("");
        purposeField.setText("");
        statusBox.setSelectedIndex(0);
    }
}
