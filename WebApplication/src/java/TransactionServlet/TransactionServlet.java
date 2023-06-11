/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package TransactionServlet;

import inventorysystem.Employee;
import inventorysystem.Item;
import inventorysystem.TransactionLog;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author savin
 */

@WebServlet(name = "TransactionServlet", urlPatterns = {"/transaction"})
public class TransactionServlet extends HttpServlet{

    private Map<Integer, Item> items;
    private static List<TransactionLog> logItems = new ArrayList<>();
    private static Hashtable<String, Employee> employees = new Hashtable<>();
    private static List<Item> personalUsage = new ArrayList<Item>();
    private Queue<HttpServletRequest> requestQueue;
    private ExecutorService executorService;
    
    
    
    @Override
    public void init() throws ServletException {
        super.init();
        items = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread-safe access
        requestQueue = new ConcurrentLinkedQueue<>(); // Use ConcurrentLinkedQueue for thread-safe queueing
        
        int numThreads = 5; // Number of threads to process requests concurrently
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        String optionParam = request.getParameter("option");
        if (optionParam == null) {
//            displayMenu(writer);
//            response.sendRedirect("index.html");
            response.sendRedirect("landing.html");
        } else {
            try {
                int option = Integer.parseInt(optionParam);              
//                if (option == 4) {
//                    Command inventoryReportCommand = new InventoryReportCommand(items, getServletContext());
//                    inventoryReportCommand.execute(request, response, response.getWriter());
//                } 
//                else {
                handleOption(option, request, response, response.getWriter());
//                }
            } catch (NumberFormatException e) {
                displayError("Invalid option.", writer);
            }
        }
    }
        
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        
        // Add the request to the queue for processing
        requestQueue.add(request);
        
        // Process the requests from the queue and the current request
        while (!requestQueue.isEmpty()) {
            HttpServletRequest queuedRequest = requestQueue.poll();
            try {
                queuedRequest.setCharacterEncoding("UTF-8");  // Set character encoding if needed
                processRequest(queuedRequest, response, writer);
            } catch (ServletException e) {
                e.printStackTrace(); // Handle the ServletException appropriately
            } catch (IOException e) {
                e.printStackTrace(); // Handle the IOException appropriately
            }
        }
        
//        // Process the request
//        processRequest(request, response, writer);

    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException, IOException {
        String optionParam = request.getParameter("option");
        if (optionParam != null && optionParam.equals("1")) {
            Command addItemCommand = new AddItemCommand(items, logItems);
            addItemCommand.execute(request, response, writer);
        } else if (optionParam != null && optionParam.equals("2")) {
            Command addStockCommand = new AddStockCommand(items, logItems);
            addStockCommand.execute(request, response, writer);
        } else if (optionParam != null && optionParam.equals("3")) {
            Command removeItemCommand = new RemoveItemCommand(items, logItems, employees, personalUsage);
            removeItemCommand.execute(request, response, writer);
        } else if (optionParam != null && optionParam.equals("7")) {
            Command personalUsageCommand = new PersonalUsageCommand(logItems, getServletContext());
            personalUsageCommand.execute(request, response, writer);
        }
         else if (optionParam != null && optionParam.equals("9")) {
            handleLogin(request, response);
        }
         else if (optionParam != null && optionParam.equals("10")) {
            handleSignup(request, response);
        }
    }

    private synchronized void handleOption(int option, HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws ServletException {
        try {
            switch (option) {
                case 1:
                    response.sendRedirect("add_item.html");
                    break;
                case 2:
                    response.sendRedirect("add_stock.html");
                    // Handle add to stock
                    break;
                case 3:
                    response.sendRedirect("delete_item.html");
                    // Handle take from stock
                    break;
                case 4:
//                    response.sendRedirect("inventory_report.html");
                    
                    Command inventoryReportCommand = new InventoryReportCommand(items, getServletContext());
                    inventoryReportCommand.execute(request, response, writer);
                       
                    break;
                case 5:                    
                    Command financialReportCommand = new FinancialReportCommand(logItems, getServletContext());
                    financialReportCommand.execute(request, response, writer);
                    
                    // Handle financial report
                    break;
                case 6:                    
                    Command transactionLogCommand = new TransactionLogCommand(logItems, getServletContext());
                    transactionLogCommand.execute(request, response, writer);
                    
                    // Handle display transaction log
                    break;
                case 7:
                    response.sendRedirect("personal_usage_report.html");
                    // Handle report personal usage
                    break;
                case 8:
                    // Handle exit
                    break;
                default:
                    displayError("Invalid option.", response.getWriter());
                    break;
            }
        } catch (IOException e) {
            // Handle the IOException
            e.printStackTrace();
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        executorService.shutdown(); // Shutdown the executor service when the servlet is destroyed
    }


    private void displayError(String errorMessage, PrintWriter writer) {
        writer.println("<html>");
        writer.println("<head><title>Error</title></head>");
        writer.println("<body>");
        writer.println("<h1>Error:</h1>");
        writer.println("<p style=\"color: red;\">" + errorMessage + "</p>");
        writer.println("</body>");
        writer.println("</html>");
    }
    
    private void handleSignup(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        // Save signup details
        if (saveSignupDetails(name, password)) {
            // Redirect to the index page or perform any necessary post-signup actions
            response.sendRedirect("index.html");
        } else {
            // Error occurred while saving the signup details
            response.sendRedirect("signup.html");
        }
    }

    private boolean saveSignupDetails(String name, String password) {
        Connection connection = null;
        Statement statement = null;
        boolean success = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
            statement = connection.createStatement();

            String query = "INSERT INTO employee (EmpName, Password, Role) VALUES ('" + name + "', '" + password + "', 'User')";
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0) {
                success = true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        // Perform login process
        if (isValidLogin(name, password)) {
            // Successful login, perform any necessary post-login actions
            response.sendRedirect("index.html");
        } else {
            // Invalid login, display error or redirect back to the login page
            response.sendRedirect("login.html");
        }
    }

    private boolean isValidLogin(String name, String password) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean valid = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/managetransaction", "root", "");
            statement = connection.createStatement();

            String query = "SELECT * FROM employee WHERE EmpName='" + name + "' AND Password='" + password + "'";
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                // User exists and password matches
                valid = true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return valid;
    }


    
    
}