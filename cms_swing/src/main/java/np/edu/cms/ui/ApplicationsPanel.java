
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class ApplicationsPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();

    private final JComboBox<String> studentBox = new JComboBox<>();
    private final JComboBox<String> universityBox = new JComboBox<>();
    private final JTextField programField = new JTextField(20);
    private final JComboBox<String> statusBox = new JComboBox<>(
            new String[]{
                    "NEW",
                    "DOCUMENT_PENDING",
                    "SUBMITTED",
                    "OFFER_RECEIVED",
                    "VISA_PROCESSING",
                    "COMPLETED",
                    "REJECTED"
            }
    );
    private final JTextField dateField = new JTextField(12);
    private final JTextField remarksField = new JTextField(20);

    private int selectedId = -1;
    private List<String> studentValues;
    private List<String> universityValues;

    public ApplicationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadComboBoxes();
        loadData();
    }

    private JPanel createForm() {

        JPanel outer = new JPanel(new BorderLayout());

        outer.setBorder(
                BorderFactory.createTitledBorder(
                        "Application Details"
                )
        );

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        addField(
                form,
                c,
                0,
                "Student:",
                studentBox
        );

        addField(
                form,
                c,
                1,
                "University:",
                universityBox
        );

        addField(
                form,
                c,
                2,
                "Program:",
                programField
        );

        addField(
                form,
                c,
                3,
                "Status:",
                statusBox
        );

        addField(
                form,
                c,
                4,
                "Applied Date:",
                dateField
        );

        addField(
                form,
                c,
                5,
                "Remarks:",
                remarksField
        );

        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        UIUtils.styleButton(save);
        UIUtils.styleButton(delete);
        UIUtils.styleButton(refresh);
        UIUtils.styleButton(clear);

        buttons.add(save);
        buttons.add(delete);
        buttons.add(refresh);
        buttons.add(clear);

        save.addActionListener(event -> save());

        delete.addActionListener(event -> delete());

        refresh.addActionListener(event -> {
            loadComboBoxes();
            loadData();
        });

        clear.addActionListener(event -> clearForm());

        table.getSelectionModel().addListSelectionListener(
                event -> loadSelected()
        );

        outer.add(form, BorderLayout.CENTER);
        outer.add(buttons, BorderLayout.SOUTH);

        return outer;
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

        panel.add(
                new JLabel(label),
                c
        );

        c.gridx = 1;
        c.weightx = 1;

        panel.add(
                component,
                c
        );
    }

    private void loadComboBoxes() {

        try {

            studentValues = dao.getComboValues(
                    "SELECT CONCAT(id, ' - ', full_name) " +
                            "FROM students ORDER BY full_name"
            );

            universityValues = dao.getComboValues(
                    "SELECT CONCAT(id, ' - ', name) " +
                            "FROM universities ORDER BY name"
            );

            studentBox.removeAllItems();
            universityBox.removeAllItems();

            for (String value : studentValues) {
                studentBox.addItem(value);
            }

            for (String value : universityValues) {
                universityBox.addItem(value);
            }

        } catch (Exception exception) {

            UIUtils.showError(
                    this,
                    exception
            );
        }
    }

    private void loadData() {

        try {

            table.setModel(
                    dao.getTableModel(

                            "SELECT a.id, " +
                                    "s.full_name, " +
                                    "u.name, " +
                                    "a.program, " +
                                    "a.status, " +
                                    "a.applied_date, " +
                                    "a.remarks " +

                                    "FROM applications a " +

                                    "JOIN students s " +
                                    "ON a.student_id = s.id " +

                                    "JOIN universities u " +
                                    "ON a.university_id = u.id " +

                                    "ORDER BY a.id DESC",

                            new String[]{
                                    "ID",
                                    "Student",
                                    "University",
                                    "Program",
                                    "Status",
                                    "Applied Date",
                                    "Remarks"
                            }
                    )
            );

        } catch (Exception exception) {

            UIUtils.showError(
                    this,
                    exception
            );
        }
    }

    private void loadSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        selectedId =
                Integer.parseInt(
                        table.getValueAt(row, 0).toString()
                );

        programField.setText(
                value(row, 3)
        );

        statusBox.setSelectedItem(
                value(row, 4)
        );

        dateField.setText(
                value(row, 5)
        );

        remarksField.setText(
                value(row, 6)
        );

        selectComboValue(
                studentBox,
                value(row, 1)
        );

        selectComboValue(
                universityBox,
                value(row, 2)
        );
    }

    private void selectComboValue(
            JComboBox<String> box,
            String displayName
    ) {

        for (int i = 0; i < box.getItemCount(); i++) {

            if (box.getItemAt(i).contains(displayName)) {

                box.setSelectedIndex(i);

                break;
            }
        }
    }

    private String value(
            int row,
            int column
    ) {

        Object value =
                table.getValueAt(row, column);

        return value == null
                ? ""
                : value.toString();
    }

    private int selectedId(
            JComboBox<String> box
    ) {

        String value =
                (String) box.getSelectedItem();

        if (
                value == null ||
                        !value.contains(" - ")
        ) {

            return -1;
        }

        return Integer.parseInt(
                value.split(
                        " - ",
                        2
                )[0]
        );
    }

    private void save() {

        try {

            int studentId =
                    selectedId(studentBox);

            int universityId =
                    selectedId(universityBox);

            if (
                    studentId == -1 ||
                            universityId == -1
            ) {

                UIUtils.showInfo(
                        this,
                        "Create a student and university first."
                );

                return;
            }

            String appliedDate =
                    dateField.getText().trim();

            java.sql.Date sqlDate =
                    appliedDate.isEmpty()
                            ? null
                            : java.sql.Date.valueOf(
                            appliedDate
                    );

            if (selectedId == -1) {

                dao.executeUpdate(

                        "INSERT INTO applications " +
                                "(student_id, university_id, " +
                                "program, status, applied_date, remarks) " +
                                "VALUES (?, ?, ?, ?, ?, ?)",

                        studentId,
                        universityId,
                        programField.getText().trim(),
                        statusBox.getSelectedItem(),
                        sqlDate,
                        remarksField.getText().trim()
                );

            } else {

                dao.executeUpdate(

                        "UPDATE applications SET " +
                                "student_id = ?, " +
                                "university_id = ?, " +
                                "program = ?, " +
                                "status = ?, " +
                                "applied_date = ?, " +
                                "remarks = ? " +
                                "WHERE id = ?",

                        studentId,
                        universityId,
                        programField.getText().trim(),
                        statusBox.getSelectedItem(),
                        sqlDate,
                        remarksField.getText().trim(),
                        selectedId
                );
            }

            UIUtils.showInfo(
                    this,
                    "Application saved successfully."
            );

            clearForm();

            loadData();

        } catch (Exception exception) {

            UIUtils.showError(
                    this,
                    exception
            );
        }
    }

    private void delete() {

        if (selectedId == -1) {

            UIUtils.showInfo(
                    this,
                    "Select an application first."
            );

            return;
        }

        try {

            dao.executeUpdate(
                    "DELETE FROM applications WHERE id = ?",
                    selectedId
            );

            UIUtils.showInfo(
                    this,
                    "Application deleted successfully."
            );

            clearForm();

            loadData();

        } catch (Exception exception) {

            UIUtils.showError(
                    this,
                    exception
            );
        }
    }

    private void clearForm() {

        selectedId = -1;

        table.clearSelection();

        programField.setText("");

        dateField.setText("");

        remarksField.setText("");

        if (studentBox.getItemCount() > 0) {
            studentBox.setSelectedIndex(0);
        }

        if (universityBox.getItemCount() > 0) {
            universityBox.setSelectedIndex(0);
        }

        statusBox.setSelectedIndex(0);
    }
}

