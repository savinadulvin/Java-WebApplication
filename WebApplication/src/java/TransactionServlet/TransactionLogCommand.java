/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TransactionServlet;

import inventorysystem.Item;
import inventorysystem.TransactionLog;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 *
 * @author savin
 */
public class TransactionLogCommand implements Command {
    private List<TransactionLog> logItems;
    private ServletContext servletContext;
    
    public TransactionLogCommand(List<TransactionLog> logItems, ServletContext servletContext) {
        this.logItems = logItems;
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
                String sql = "SELECT * FROM transactionlog";
                ResultSet resultSet = stmt.executeQuery(sql);

                // Read the content of the transaction_log.html file
                InputStream inputStream = servletContext.getResourceAsStream("/transaction_log.html");
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

                // Write the HTML content to the PrintWriter
                String line;
                while ((line = fileReader.readLine()) != null) {
                    if (line.contains("<!--TRANSACTION_TABLE-->")) {
                        // Insert the transaction table dynamically
                        writer.println("<table>");
                        writer.println("<tr>");
                        writer.println("<th>Date</th>");
                        writer.println("<th>Type</th>");
                        writer.println("<th>ID</th>");
                        writer.println("<th>Name</th>");
                        writer.println("<th>Employee</th>");
                        writer.println("<th>Price</th>");
                        writer.println("<th>Quantity</th>");
                        writer.println("</tr>");
                        // Display the transaction items in the HTML table
                        while (resultSet.next()) {
                            String DateCreated = resultSet.getString("DateCreated");
                            String type = resultSet.getString("Type");
                            int id = resultSet.getInt("id");
                            String name = resultSet.getString("Name");
                            String employeeName = resultSet.getString("EmployeeName");
                            float price = resultSet.getFloat("Price");
                            int quantity = resultSet.getInt("Quantity");

                            writer.println("<tr>");
                            writer.println("<td>" + DateCreated + "</td>");
                            writer.println("<td>" + type + "</td>");
                            writer.println("<td>" + id + "</td>");
                            writer.println("<td>" + name + "</td>");
                            writer.println("<td>" + employeeName + "</td>");
                            writer.println("<td>" + price + "</td>");
                            writer.println("<td>" + quantity + "</td>");
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
        } catch (IOException e) {
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

