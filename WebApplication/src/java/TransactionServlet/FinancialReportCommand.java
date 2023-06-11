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

public class FinancialReportCommand implements Command {
    private List<TransactionLog> logItems;
    private ServletContext servletContext;
    
    public FinancialReportCommand(List<TransactionLog> logItems, ServletContext servletContext) {
        this.logItems = logItems;
        this.servletContext = servletContext;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        // Set the content type of the response to HTML
        response.setContentType("text/html");

        // Establish the database connection
        Connection con = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
            stmt = con.createStatement();

            // Execute the SQL query to fetch the financial report data from the database
            String sqlQuery = "SELECT Name, Price, Quantity FROM item";
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            // Read the content of the financial_report.html file
            InputStream inputStream = servletContext.getResourceAsStream("/financial_report.html");
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

            // Write the HTML content to the PrintWriter
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.contains("<!--FINANCIAL_REPORT_CONTENT-->")) {
                    // Display the financial report content
                    float total = 0;
                    writer.println("<table>");
                    writer.println("<tr>");
                    writer.println("<th>Item Name</th>");
                    writer.println("<th>Cost</th>");
                    writer.println("</tr>");
                    while (resultSet.next()) {
                        String itemName = resultSet.getString("name");
                        float itemPrice = resultSet.getFloat("price");
                        int itemQuantity = resultSet.getInt("quantity");

                        float cost = itemPrice * itemQuantity;
                        writer.println("<tr>");
                        writer.println("<td>" + itemName + "</td>");
                        writer.println("<td>" + cost + "</td>");
                        writer.println("</tr>");
                        total += cost;
                    }

                    writer.println("<tr>");
                    writer.println("<td>Total price of all items:</td>");
                    writer.println("<td>" + total + "</td>");
                    writer.println("</tr>");

                    writer.println("</table>");
                    
                } else {
                    writer.println(line);
                }
            }

            // Close the database resources
            resultSet.close();
            stmt.close();
            con.close();

            writer.flush();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            writer.println("<h1>Error: Unable to fetch financial report data</h1>");
            writer.flush();
        } finally {
            // Close the database resources in case of any exception
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