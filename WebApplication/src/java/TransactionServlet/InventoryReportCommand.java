/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TransactionServlet;

import inventorysystem.Item;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 *
 * @author savin
 */

public class InventoryReportCommand implements Command {
    private Map<Integer, Item> items;
    private ServletContext servletContext;

    public InventoryReportCommand(Map<Integer, Item> items, ServletContext servletContext) {
        this.items = items;
        this.servletContext = servletContext;
    }
    

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Set the content type of the response to HTML
        response.setContentType("text/html");

        try {
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

                // Execute the SQL query to retrieve the inventory items
                String sql = "SELECT * FROM item";
                ResultSet rs = stmt.executeQuery(sql);

                // Read the content of the inventory_report.html file
                InputStream inputStream = servletContext.getResourceAsStream("/inventory_report.html");
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

                // Write the HTML content to the PrintWriter
                String line;
                while ((line = fileReader.readLine()) != null) {
                    if (line.contains("<!--INVENTORY_TABLE-->")) {
                        // Insert the inventory table dynamically
                        writer.println("<table>");
//                        writer.println("<tr>");
//                        writer.println("<th>Item ID</th>");
//                        writer.println("<th>Item Name</th>");
//                        writer.println("<th>Item Quantity</th>");
//                        writer.println("</tr>");

                        // Display the inventory items in the HTML table
                        while (rs.next()) {
                            int itemId = rs.getInt("id");
                            String itemName = rs.getString("name");
                            int itemQuantity = rs.getInt("quantity");

                            writer.println("<tr>");
                            writer.println("<td>" + itemId + "</td>");
                            writer.println("<td>" + itemName + "</td>");
                            writer.println("<td>" + itemQuantity + "</td>");
                            writer.println("</tr>");
                        }

                        writer.println("</table>");
                    } else {
                        // Write other lines of the HTML template
                        writer.println(line);
                    }
                }
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
        } catch (Exception e) {
            displayError("ERROR: " + e.getMessage(), writer);
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

