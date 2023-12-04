import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "StarsServlet", urlPatterns = "/api/stars")
public class StarsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("StarsServlet hit");
//        File file =  new File("/Users/mingjia/Desktop/Tomcat/webapps/cs122b_project1_api_example_war/log.txt").getAbsoluteFile();
//        file.createNewFile();
//        System.out.println("File exists: " + file.exists());
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT * from stars";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String star_id = rs.getString("id");
                String star_name = rs.getString("name");
                String star_dob = rs.getString("birthYear");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_dob", star_dob);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            System.out.println("At line 84: response status was set");

            String contextPath = request.getServletContext().getRealPath("/");

            String logfilePath = contextPath + "log.txt";

            System.out.println("logFilePath: " + logfilePath);

//            File log = new File(logfilePath);
//            log.createNewFile();
//            System.out.println("log.exists: " + log.exists());
//            System.out.println("Return value of log.createNewFile(): " + log.createNewFile());
//            try{
//                if (log.createNewFile()) {
//                    System.out.println("We had to make a new file.");
//                }
//                else {
//                    System.out.println("File already exists");
//                }

//                FileWriter writer = new FileWriter(log, true);
                try {
//                    String filePath = contextPath;
//                    String filename = "log.txt";

//                    String logPath = contextPath + filename;

                    // Create File object
//                    File file = new File(filePath, filename);
//
//                    // Ensure the directory exists
//                    file.getParentFile().mkdirs();

                    // Create FileWriter with append mode
                    FileWriter fw = new FileWriter(logfilePath, true);

                    // Create BufferedWriter for better performance
                    BufferedWriter bw = new BufferedWriter(fw);

                    // Append data to the file
                    bw.write("add a line\n");

                    // Close the BufferedWriter (this will also close the FileWriter)
                    bw.close();

                    System.out.println("Data appended to the file successfully.");

                } catch (IOException ioe) {
                    System.err.println("IOException: " + ioe.getMessage());
                    ioe.printStackTrace(); // Print the stack trace for debugging
                }

//                PrintWriter writer = new PrintWriter(new FileWriter(logfilePath, true));
//                writer.append("******* " + System.nanoTime() + " ******* " + "\n");
//                writer.close();
//            }catch(IOException e){
//                System.out.println("COULD NOT LOG!!");
//                e.printStackTrace();
//            }
//            String contextPath = request.getServletContext().getRealPath("/");
//
//            System.out.println("At line 88: RealPath was retrieved");
//
//            String logFilePath = contextPath + "log.txt";
//
//            System.out.println("============ New File Path: " + logFilePath);
//
//            try {
//                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
//
//                // Writing content to the file
//                writer.write("This text was written at: " + System.nanoTime());
//
//                // Closing the writer is important to ensure that the data is flushed and the file is properly closed.
//                writer.close();
//                System.out.println("Data has been written to the file.");
//
//            } catch (IOException e) {
//                System.err.println("Error writing to the file: " + e.getMessage());
//            }

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}