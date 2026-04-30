/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Admin
 */
public class TransactionManager {
    private BankDAO dao = new BankDAO();

    /**
     * Fetches all transactions and populates the provided JTable.
     */
    public void displayFullHistory(JTable table) {
        try {
            ResultSet rs = dao.getAllTransactions();
            populateTableFromResultSet(table, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters transactions based on a category (Account ID, Type, or Date) 
     * and a specific value.
     */
    public void filterHistory(JTable table, String filterType, String value) {
        if (value.isEmpty()) {
            displayFullHistory(table);
            return;
        }
        
        try {
            ResultSet rs = dao.getFilteredTransactions(filterType, value);
            populateTableFromResultSet(table, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal helper to avoid repeating the table-filling logic.
     */
    private void populateTableFromResultSet(JTable table, ResultSet rs) throws SQLException {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear the table first

        while (rs != null && rs.next()) {
            Object[] row = {
                rs.getInt("transaction_id"),
                rs.getString("account_id"),
                rs.getString("transaction_type"),
                rs.getDouble("amount"),
                rs.getTimestamp("transaction_date")
            };
            model.addRow(row);
        }
    }
    
}
