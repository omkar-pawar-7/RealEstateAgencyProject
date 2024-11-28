package com.agency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CORS preflight request handling
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5501");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set CORS headers for the main request
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5501");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Set response content type
        response.setContentType("application/json");

        // Read JSON data from the request body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        String jsonData = jsonBuffer.toString();
        JSONObject jsonObject = new JSONObject(jsonData);

        // Extract data from JSON object
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");
        String mobileNo = jsonObject.getString("mobileno");

        // Database connection details
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; 
        String username = "system"; 
        String password = "manager"; 
        String sql = "INSERT INTO CONTACTS (NAME, EMAIL, MOBILENO) VALUES (?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Register Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Open a connection
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Create the prepared statement
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, mobileNo);

            // Execute the statement
            int rowsInserted = statement.executeUpdate();

            PrintWriter out = response.getWriter();
            if (rowsInserted > 0) {
                out.println("{\"message\": \"Thank you! We will contact you via phone or email shortly.\"}");
            } else {
                out.println("{\"message\": \"There was an error processing your request. Please try again.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"message\": \"Server error occurred.\"}");
        } finally {
            // Clean-up environment
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
