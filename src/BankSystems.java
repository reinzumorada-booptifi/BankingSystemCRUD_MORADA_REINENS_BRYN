/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Admin
 */
public class BankSystems {
    private BankDAO dao = new BankDAO();

    // 1. ADD ACCOUNT LOGIC
    public void addCustomerAndAccount(String id, String fn, String ln, String em, String ph, String ty, String balStr) {
        if (id.isEmpty() || fn.isEmpty() || balStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all required fields!");
            return;
        }

        if (!id.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(null, "Account ID must be exactly 4 digits!");
            return;
        }

        try {
            double bal = Double.parseDouble(balStr);
            if (dao.saveNewAccount(id, fn, ln, em, ph, ty, bal)) {
                JOptionPane.showMessageDialog(null, "Account " + id + " created successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error: Account ID might already exist.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format for balance!");
        }
    }

    // 2. TRANSACTION LOGIC (Deposit/Withdraw)
    public void performTransaction(String id, String type, String amtStr, String action) {
        if (id.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter Account ID and Amount!");
            return;
        }

        try {
            double amt = Double.parseDouble(amtStr);
            String result = dao.executeTransaction(id, type, amt, action);

            switch (result) {
                case "SUCCESS" -> JOptionPane.showMessageDialog(null, action + " successful!");
                case "NO_FUNDS" -> JOptionPane.showMessageDialog(null, "Insufficient Balance!");
                case "NOT_FOUND" -> JOptionPane.showMessageDialog(null, "Account ID not found for this type!");
                default -> JOptionPane.showMessageDialog(null, "Transaction Failed!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric amount!");
        }
    }

    // 3. POPULATE MAIN TABLE
    public void populateTable(JTable table) {
        try {
            ResultSet rs = dao.getAllAccounts();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (rs != null && rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("account_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone")); // Fixed column name
                row.add(rs.getString("account_type"));
                row.add(rs.getDouble("balance"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. LOAD TO FIELDS (Fixed Phone Number Crash)
    public void loadToFields(String id, JTextField fn, JTextField ln, JTextField em, JTextField ph, JTextField bal) {
        try {
            ResultSet rs = dao.getAccountDetails(id);
            if (rs != null && rs.next()) {
                fn.setText(rs.getString("first_name"));
                ln.setText(rs.getString("last_name"));
                em.setText(rs.getString("email"));
                ph.setText(rs.getString("phone")); // Changed from "phone_number"
                bal.setText(String.valueOf(rs.getDouble("balance")));
            } else {
                JOptionPane.showMessageDialog(null, "No record found for ID: " + id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    // 5. UPDATE, DELETE, & COMBOBOX
    public boolean updateCustomerInfo(String id, String fn, String ln, String em, String ph) {
        return dao.updateCustomer(id, fn, ln, em, ph);
    }

    public boolean deleteAccount(String id) {
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an Account ID to delete.");
            return false;
        }
        return dao.deleteAccount(id);
    }

    public void fillAccountCombo(javax.swing.JComboBox combo) {
        try {
            ResultSet rs = dao.getAllAccounts();
            combo.removeAllItems();
            while (rs.next()) {
                combo.addItem(rs.getString("account_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}