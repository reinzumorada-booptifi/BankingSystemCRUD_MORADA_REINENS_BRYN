## 🏦 System Description
This is a Java Swing-based Banking Management System that allows users to manage customer accounts and track financial history. The system connects to a MySQL database to perform full CRUD (Create, Read, Update, Delete) operations.

## 📊 ERD Explanation
The database consists of three main tables:
* **Customer:** Stores personal details (Name, Email, Phone).
* **Account:** Linked to Customer; stores account types and current balances.
* **Transaction:** Linked to Account; logs every Deposit and Withdrawal with a timestamp.

## 🚀 How to Run the Program
1. **Database Setup:** - Import the `banking_system.sql` file into your MySQL Server.
   - Ensure your database credentials match in `DBConnection.java`.
2. **Library Requirement:** - Add the `mysql-connector-j` JAR to your project libraries in NetBeans.
3. **Execution:**
   - Open the project in NetBeans.
   - Run `AccountManagerForm.java`.
