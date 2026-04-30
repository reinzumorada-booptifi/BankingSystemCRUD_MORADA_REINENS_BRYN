/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Admin
 */
public class BankDAO {
    
    // 1. SAVE NEW ACCOUNT
    public boolean saveNewAccount(String id, String fn, String ln, String em, String ph, String type, double balance) {
        String sqlCust = "INSERT INTO customer (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
        String sqlAcc = "INSERT INTO account (account_id, customer_id, account_type, balance) VALUES (?, LAST_INSERT_ID(), ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 
            try (PreparedStatement p1 = conn.prepareStatement(sqlCust);
                 PreparedStatement p2 = conn.prepareStatement(sqlAcc)) {
                
                p1.setString(1, fn); 
                p1.setString(2, ln);
                p1.setString(3, em); 
                p1.setString(4, ph);
                p1.executeUpdate();

                p2.setString(1, id); 
                p2.setString(2, type); 
                p2.setDouble(3, balance); 
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace(); 
                return false;
            }
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 2. GET ACCOUNT DETAILS
    public ResultSet getAccountDetails(String id) {
        String sql = "SELECT c.first_name, c.last_name, c.email, c.phone, a.balance " + 
                     "FROM customer c JOIN account a ON c.customer_id = a.customer_id " +
                     "WHERE a.account_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 3. UPDATE CUSTOMER
    public boolean updateCustomer(String id, String fn, String ln, String em, String ph) {
        String sql = "UPDATE customer c JOIN account a ON c.customer_id = a.customer_id " +
                     "SET c.first_name=?, c.last_name=?, c.email=?, c.phone=? WHERE a.account_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fn);
            pstmt.setString(2, ln);
            pstmt.setString(3, em);
            pstmt.setString(4, ph);
            pstmt.setString(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 4. DELETE ACCOUNT
    public boolean deleteAccount(String id) {
        String sql = "DELETE FROM account WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // 5. TRANSACTIONS
    public String executeTransaction(String id, String type, double amt, String action) {
    // 1. Update the Balance Query
    String updateSql = "UPDATE Account SET balance = balance " + 
                       (action.equals("DEPOSIT") ? "+ ?" : "- ?") + 
                       " WHERE account_id = ? AND account_type = ?";

    // 2. Insert into Transaction History Query (MATCHING YOUR DIAGRAM)
    String logSql = "INSERT INTO Transaction (account_id, transaction_type, amount) VALUES (?, ?, ?)";

    try (java.sql.Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false); // Start transaction

        // --- PART 1: Update Balance ---
        try (java.sql.PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
            psUpdate.setDouble(1, amt);
            psUpdate.setString(2, id);
            psUpdate.setString(3, type);
            
            int rowsAffected = psUpdate.executeUpdate();
            if (rowsAffected == 0) {
                conn.rollback();
                return "NOT_FOUND";
            }
        }

        // --- PART 2: Log the Transaction ---
        // This is where your error was! We ensure 'logSql' is used correctly here.
        try (java.sql.PreparedStatement psLog = conn.prepareStatement(logSql)) {
            psLog.setString(1, id);
            psLog.setString(2, action);
            psLog.setDouble(3, amt);
            psLog.executeUpdate();
        }

        conn.commit(); // Save both changes
        return "SUCCESS";

    } catch (java.sql.SQLException e) {
        e.printStackTrace();
        return "ERROR";
    }
}

    // 6. VIEW ALL
    public ResultSet getAllAccounts() {
        String sql = "SELECT a.account_id, c.first_name, c.last_name, c.email, c.phone, a.account_type, a.balance " +
                     "FROM customer c JOIN account a ON c.customer_id = a.customer_id";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 7. SEARCH
    public ResultSet searchAccounts(String keyword) {
        String sql = "SELECT a.account_id, c.first_name, c.last_name, c.email, c.phone, a.account_type, a.balance " +
                     "FROM customer c JOIN account a ON c.customer_id = a.customer_id " +
                     "WHERE a.account_id LIKE ? OR c.first_name LIKE ? OR c.last_name LIKE ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String query = "%" + keyword + "%"; 
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            pstmt.setString(3, query);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 8. GET ALL TRANSACTIONS
    public java.sql.ResultSet getAllTransactions() {
    String sql = "SELECT * FROM Transaction ORDER BY transaction_date DESC";
    try {
        // Use your DBConnection class to get the connection
        java.sql.Connection conn = DBConnection.getConnection(); 
        java.sql.PreparedStatement pst = conn.prepareStatement(sql);
        return pst.executeQuery();
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
        return null;
    }
}

    // 9. FILTERED TRANSACTIONS
    public ResultSet getFilteredTransactions(String filterType, String value) {
    String sql = "SELECT * FROM Transaction"; // Start with base query
    
    // Add WHERE clause based on what user selected
    if (filterType.equals("Account ID") && !value.isEmpty()) {
        sql += " WHERE account_id = ? ORDER BY transaction_date DESC";
    } else if (filterType.equals("Transaction Type") && !value.isEmpty()) {
        sql += " WHERE transaction_type = ? ORDER BY transaction_date DESC";
    } else {
        sql += " ORDER BY transaction_date DESC";
    }

    try {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        if (sql.contains("?")) {
            ps.setString(1, value);
        }
        return ps.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
}