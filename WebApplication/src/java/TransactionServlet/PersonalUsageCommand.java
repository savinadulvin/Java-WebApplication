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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author savin
 */

public class PersonalUsageCommand implements Command {
    private List<TransactionLog> logItems;
    private ServletContext servletContext;
    
    public PersonalUsageCommand(List<TransactionLog> logItems, ServletContext servletContext) {
        this.logItems = logItems;
        this.servletContext = servletContext;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Set the content type of the response to HTML
        response.setContentType("text/html");

        // Read the content of the personal_usage_report.html file
        InputStream inputStream = servletContext.getResourceAsStream("/personal_usage_report.html");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Write the HTML content to the PrintWriter
        String line;
        while ((line = fileReader.readLine()) != null) {
            writer.println(line);
            String empname = request.getParameter("employeeName");
            if (line.contains("<!--PERSONAL_USAGE_TABLE-->")) {
                boolean isEmployeeFound = false;

                writer.println("<h1>Personal Usage Report</h1>");

                // Establish the database connection
                String url = "jdbc:mysql://localhost:3306/managetransaction";
                String username = "root";
                String password = "";
                Connection conn = null;
                Statement stmt = null;
                ResultSet resultSet = null;

                try {
                    // Register the JDBC driver
                    Class.forName("com.mysql.jdbc.Driver");

                    // Open a connection
                    conn = DriverManager.getConnection(url, username, password);

                    // Create a statement
                    stmt = conn.createStatement();

                    // Execute the SQL query to retrieve the personal usage report data
                    String selectSql = "SELECT * FROM transactionlog WHERE type='Item Removed' AND employeeName='" + empname + "'";
                    resultSet = stmt.executeQuery(selectSql);

                    if (resultSet.next()) {
                        isEmployeeFound = true;
                        writer.println("<table>");
                        writer.println("<tr>");
                        writer.println("<th>Date Taken</th>");
                        writer.println("<th>ID</th>");
                        writer.println("<th>Name</th>");
                        writer.println("<th>Quantity</th>");
                        writer.println("</tr>");

                        // Iterate over the result set and populate the personal usage report table
                        do {
                            writer.println("<tr>");
                            writer.println("<td>" + resultSet.getString("DateCreated") + "</td>");
                            writer.println("<td>" + resultSet.getInt("id") + "</td>");
                            writer.println("<td>" + resultSet.getString("name") + "</td>");
                            writer.println("<td>" + resultSet.getInt("quantity") + "</td>");
                            writer.println("</tr>");
                        } while (resultSet.next());

                        writer.println("</table>");
                    } else {
                        writer.println("<p>You entered an employee name that is not available.</p>");
                    }
                } catch (SQLException e) {
                    // Handle any errors that occur during database access
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // Handle any errors related to the JDBC driver
                    e.printStackTrace();
                } finally {
                    // Close the result set, statement, and connection
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        writer.flush();
    }

}