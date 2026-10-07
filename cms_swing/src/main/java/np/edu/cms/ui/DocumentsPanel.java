package np.edu.cms.ui;

import np.edu.cms.dao.DatabaseDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.LocalDate;

public class DocumentsPanel extends JPanel {

    private final DatabaseDAO dao = new DatabaseDAO();
    private final JTable table = new JTable();
    private final JComboBox<String> studentBox = new JComboBox<>();
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> typeBox = new JComboBox<>(
            new String[]{
                    "Passport",
                    "Academic Certificate",
                    "Transcript",
                    "Financial Document",
                    "Offer Letter",
                    "Other"
            }
    );
    private final JTextField pathField = new JTextField(30);
    private int selectedId = -1;

    public DocumentsPanel() {
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
                "Document Details"
        ));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, c, 0, "Student:", studentBox);
        addField(panel, c, 1, "Document Name:", nameField);
        addField(panel, c, 2, "Document Type:", typeBox);
        addField(panel, c, 3, "File Path:", createFilePanel());

        JButton save = new JButton("Save");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        JButton clear = new JButton("Clear");
        addField(panel, c, 4, "Actions:",
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

    private JPanel createFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JButton browse = new JButton("Browse...");
        UIUtils.styleButton(browse);
        panel.add(pathField, BorderLayout.CENTER);
        panel.add(browse, BorderLayout.EAST);

        browse.addActionListener(event -> chooseFile());
        return panel;
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            pathField.setText(file.getAbsolutePath());
            if (nameField.getText().trim().isEmpty()) {
                nameField.setText(file.getName());
            }
        }
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
                    "SELECT d.id, s.full_name, d.document_name, "
                            + "d.document_type, d.file_path, d.uploaded_date "
                            + "FROM documents d "
                            + "JOIN students s ON d.student_id = s.id "
                            + "ORDER BY d.id DESC",
                    new String[]{
                            "ID", "Student", "Document", "Type", "File Path",
                            "Uploaded Date"
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
        nameField.setText(value(row, 2));
        typeBox.setSelectedItem(value(row, 3));
        pathField.setText(value(row, 4));
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

            if (selectedId == -1) {
                dao.executeUpdate(
                        "INSERT INTO documents "
                                + "(student_id, document_name, document_type, "
                                + "file_path, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                        studentId,
                        nameField.getText().trim(),
                        typeBox.getSelectedItem(),
                        pathField.getText().trim(),
                        java.sql.Date.valueOf(LocalDate.now())
                );
            } else {
                dao.executeUpdate(
                        "UPDATE documents SET student_id = ?, "
                                + "document_name = ?, document_type = ?, "
                                + "file_path = ? WHERE id = ?",
                        studentId,
                        nameField.getText().trim(),
                        typeBox.getSelectedItem(),
                        pathField.getText().trim(),
                        selectedId
                );
            }

            UIUtils.showInfo(this, "Document saved successfully.");
            clearForm();
            loadData();
        } catch (Exception exception) {
            UIUtils.showError(this, exception);
        }
    }

    private void delete() {
        if (selectedId == -1) {
            UIUtils.showInfo(this, "Select a document first.");
            return;
        }
        try {
            dao.executeUpdate(
                    "DELETE FROM documents WHERE id = ?",
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
        nameField.setText("");
        pathField.setText("");
        typeBox.setSelectedIndex(0);
    }
}
