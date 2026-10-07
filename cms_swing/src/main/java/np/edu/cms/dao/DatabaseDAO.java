package np.edu.cms.dao;

import np.edu.cms.config.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDAO {

    public DefaultTableModel getTableModel(
            String sql,
            String[] columns
    ) throws SQLException {

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                Object[] row = new Object[columns.length];

                for (int i = 0; i < columns.length; i++) {
                    row[i] = result.getObject(i + 1);
                }

                model.addRow(row);
            }
        }

        return model;
    }

    public int executeUpdate(String sql, Object... values)
            throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setValues(statement, values);
            return statement.executeUpdate();
        }
    }

    public int insertAndGetId(String sql, Object... values)
            throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setValues(statement, values);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    public List<String> getComboValues(
            String sql
    ) throws SQLException {

        List<String> values = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                values.add(result.getString(1));
            }
        }

        return values;
    }

    private void setValues(
            PreparedStatement statement,
            Object[] values
    ) throws SQLException {

        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
    }
}
