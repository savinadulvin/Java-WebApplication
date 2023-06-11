/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TransactionServlet;

import inventorysystem.Item;
import inventorysystem.TransactionLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author savin
 */
public class AddStockCommand implements Command {
    private Map<Integer, Item> items;
    private List<TransactionLog> logItems;

    public AddStockCommand(Map<Integer, Item> items, List<TransactionLog> logItems) {
        this.items = items;
        this.logItems = logItems;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Set the content type of the response to HTML
        response.setContentType("text/html");

        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            Item temp = itemExistInDB(itemId);

//            float itemPrice = Float.parseFloat(request.getParameter("itemPrice"));
            String itemEmpName = request.getParameter("employeeName");
            int itemQuantity = Integer.parseInt(request.getParameter("itemQuantity"));

            if (itemQuantity < 0 || itemEmpName.isEmpty()) {
                displayError("ERROR: Quantity being added is below 0 or Employee name is empty", writer);
            } else {
                temp.Quantity += itemQuantity;

                // Establish the database connection
                Connection conn = null;
                Statement stmt = null;

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
                    stmt = conn.createStatement();
                    stmt.executeUpdate("UPDATE item SET quantity = quantity + " + itemQuantity + ", dateUpdated = '" + LocalDateTime.now() + "', updatedBy = '" + itemEmpName + "' WHERE id = " + itemId);

                    writer.println("<html>");
                    writer.println("<head><title>Manage Transaction System</title></head>");
                    writer.println("<body>");
                    writer.println("<h1>Stock Updated:</h1>");
                    writer.println("<p>" + itemQuantity + " items have been added to Item ID: " + itemId + " on " + LocalDateTime.now() + "</p>");
                    writer.println("<button onclick=\"location.href='index.html'\">Go back</button>");
                    writer.println("</body>");
                    writer.println("</html>");

                    Item i = new Item(itemId, temp.Name, itemQuantity, itemEmpName, temp.Price, LocalDateTime.now());
                    createLogEntry("Stock Updated", i, temp.Price, LocalDateTime.now());
                } catch (ClassNotFoundException | SQLException e) {
                    displayError("ERROR: Failed to establish MySQL connection or execute the query.", writer);
                } finally {
                    // Close the database resources
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        } catch (NumberFormatException e) {
            displayError("ERROR: Invalid input format.", writer);
        } catch (Exception e) {
            displayError("ERROR: Item not found.", writer);
        }
    }

    // method to check if an item already exists in the database
    public static Item itemExistInDB(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
            String query = "SELECT * FROM item WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int itemId = rs.getInt("id");
                String itemName = rs.getString("name");
                int itemQuantity = rs.getInt("quantity");
                float itemPrice = rs.getFloat("price");
                String itemEmpName = ""; // Provide a default value for empName
                LocalDateTime itemDateCreated = LocalDateTime.now(); // Provide a default value for dateCreated
                return new Item(itemId, itemName, itemQuantity, itemEmpName, itemPrice, itemDateCreated);
            }           
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error checking if item exists in the database: " + e.getMessage());
        } finally {
            // Close the database resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing database resources: " + e.getMessage());
            }
        }
        return null;
    }
    
    public synchronized void createLogEntry(String type, Item item, float itemPrice, LocalDateTime dateAdded) {
        // Add the log entry to the in-memory logItems list
        logItems.add(new TransactionLog(type, item, itemPrice, dateAdded));

        // Establish the database connection
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");

            // Prepare the SQL statement to insert the log entry into the database
            String sql = "INSERT INTO transactionlog (Type, id, Name, Quantity, Price, EmployeeName, DateCreated) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);

            // Set the parameter values in the prepared statement
            stmt.setString(1, type);
            stmt.setInt(2, item.getID());
            stmt.setString(3, item.getName());
            stmt.setInt(4, item.getQuantity());
            stmt.setFloat(5, itemPrice);
            stmt.setString(6, item.getEmpName());
            stmt.setTimestamp(7, Timestamp.valueOf(dateAdded));

            // Execute the SQL statement to insert the log entry into the database
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void displayError(String errorMessage, PrintWriter writer) {
        writer.println("<html>");
        writer.println("<head><title>Error</title></head>");
        writer.println("<body>");
        writer.println("<h1>Error:</h1>");
        writer.println("<p style=\"color: red;\">" + errorMessage + "</p>");
        writer.println("<button onclick=\"location.href='index.html'\">Go back</button>");
        writer.println("</body>");
        writer.println("</html>");
    }
}

