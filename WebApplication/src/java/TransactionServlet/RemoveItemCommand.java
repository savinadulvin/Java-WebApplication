/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TransactionServlet;

import inventorysystem.Employee;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author savin
 */
public class RemoveItemCommand implements Command {
    private Map<Integer, Item> items;
    private List<TransactionLog> logItems;
    private Hashtable<String, Employee> employees;
    private List<Item> personalUsage;

    public RemoveItemCommand(Map<Integer, Item> items, List<TransactionLog> logItems, Hashtable<String, Employee> employees, List<Item> personalUsage) {
        this.items = items;
        this.logItems = logItems;
        this.employees = employees;
        this.personalUsage = personalUsage;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Get the PrintWriter object from the response
        // PrintWriter writer = response.getWriter();

        // Set the content type of the response to HTML
        response.setContentType("text/html");

        try {
            String empName = request.getParameter("employeeName");
    //        FindEmployee(empName);

            int itemId = Integer.parseInt(request.getParameter("itemId"));
            Item temp = itemExistInDB(itemId);

            int itemQuantity = Integer.parseInt(request.getParameter("itemQuantity"));
            if (itemQuantity > temp.Quantity || itemQuantity < 0) {
                displayError("ERROR: Quantity too many or below 0", writer);
            } else {
                temp.Quantity -= itemQuantity;

                writer.println("<html>");
                writer.println("<head><title>Manage Transaction System</title></head>");
                writer.println("<body>");
                writer.println("<h1>Item Removed:</h1>");
                writer.println("<p>" + empName + " has removed " + itemQuantity + " of Item ID: " + itemId + " on " + LocalDateTime.now() + "</p>");
                writer.println("<button onclick=\"location.href='index.html'\">Go back</button>");
                writer.println("</body>");
                writer.println("</html>");

                // Establish the database connection
                String url = "jdbc:mysql://localhost:3306/managetransaction";
                String username = "root";
                String password = "";
                Connection conn = null;
                Statement stmt = null;

                try {
                    // Register the JDBC driver
                    Class.forName("com.mysql.jdbc.Driver");

                    // Open a connection
                    conn = DriverManager.getConnection(url, username, password);

                    // Create a statement
                    stmt = conn.createStatement();

                    // Execute the SQL query to insert the item into the items table
                    String insertSql = "UPDATE item SET quantity = quantity - " + itemQuantity + ", dateUpdated = '" + LocalDateTime.now() + "', updatedBy = '" + empName + "' WHERE id = " + itemId;
                    stmt.executeUpdate(insertSql);
                } catch (SQLException e) {
                    System.out.println("Error executing SQL query: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Error loading JDBC driver: " + e.getMessage());
                } finally {
                    // Close the statement and connection
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("Error closing statement: " + e.getMessage());
                        }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            System.out.println("Error closing connection: " + e.getMessage());
                        }
                    }
                }

//                float price = Float.parseFloat(request.getParameter("itemPrice"));
                Item i = new Item(itemId, temp.Name, itemQuantity, empName, temp.Price, LocalDateTime.now());
                createLogEntry("Item Removed", i, temp.Price, LocalDateTime.now());
            }
        } catch (NumberFormatException e) {
            displayError("ERROR: Invalid input format.", writer);
        } catch (Exception e) {
            displayError("ERROR: " + e.getMessage(), writer);
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

