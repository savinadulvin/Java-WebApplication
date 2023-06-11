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
public class AddItemCommand implements Command {
    private Map<Integer, Item> items;
    private List<TransactionLog> logItems;

    public AddItemCommand(Map<Integer, Item> items, List<TransactionLog> logItems) {
        this.items = items;
        this.logItems = logItems;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Parse item details from the request parameters
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        String itemName = request.getParameter("itemName");
        float itemPrice = Float.parseFloat(request.getParameter("itemPrice"));
        int itemQuantity = Integer.parseInt(request.getParameter("itemQuantity"));
        String employeeName = request.getParameter("employeeName");

        // Create a new item object
        Item newItem = new Item(itemId, itemName, itemQuantity, employeeName, itemPrice, LocalDateTime.now());
        // Add the item to the map
        items.put(newItem.getID(), newItem);

        // Establish the database connection
        Connection con = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
            stmt = con.createStatement();

            // Insert the item details into the database
            String sql = "INSERT INTO item (ID, Name, Quantity, EmpName, Price, DateCreated) VALUES (" + itemId + ", '" + itemName + "', " + itemQuantity + ", '" + employeeName + "', '" + itemPrice + "', NOW())";
            stmt.executeUpdate(sql);

            // Display a success message
            writer.println("<html>");
            writer.println("<head><title>Manage Transaction System</title></head>");
            writer.println("<body>");
            writer.println("<h1>Item Added:</h1>");
            writer.println("<p>Item ID: " + newItem.getID() + "</p>");
            writer.println("<p>Name: " + newItem.getName() + "</p>");
            writer.println("<p>Price: " + newItem.getPrice() + "</p>");
            writer.println("<p>Quantity: " + newItem.getQuantity() + "</p>");
            writer.println("<button onclick=\"location.href='index.html'\">Go back</button>");
            writer.println("</body>");
            writer.println("</html>");         
            
            // Create a log entry for the item addition
            createLogEntry("Item Added", newItem, itemPrice, LocalDateTime.now());  
            
        } catch (Exception e) {
            writer.println("<html>");
            writer.println("<head><title>Manage Transaction System</title></head>");
            writer.println("<body>");
            writer.println("<h1>Error Adding Item:</h1>");
            writer.println("<p>" + e.getMessage() + "</p>");
            writer.println("<button onclick=\"location.href='index.html'\">Go back</button>");
            writer.println("</body>");
            writer.println("</html>");
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

}

