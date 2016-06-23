package cs3200.Controller;

import cs3200.Model.IModel;
import cs3200.View.GUIFrame;
import cs3200.View.IView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * Controller Class for the Database Design Front End program
 */
public class DatabaseController {
    private String username, password;
    private Formatter ap;
    private IView view;
    private IModel model;
    public Connection conn = null;


    public DatabaseController(IModel model) {
        this.model = model;
    }

    public void setView(IView view) {
        this.view = view;
        this.view.initialize();
    }

    public void run() {
        try {
            conn = this.model.getConnection();
            System.out.println("Connected to database");
        } catch (SQLException e) {
            // if given a bad connection it will give the error message below
            System.out.println("Error: Bad credentials: " + e.getMessage());
            return;
        }
        try {
            Statement stmt = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("ERROR: Encountered exception");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Checks if the username included in the parameter currently exists in the database.
     * @param username The username to be checked for existence in the DB
     * @return True if the username is in the database for a student, false otherwise
     */
    public boolean verifyUsername(String username) {
        String sqlUsername = "SELECT student_id FROM students";
        ArrayList<String> usernames = new ArrayList<>();
        try {
            Statement getUsernames = conn.prepareStatement(sqlUsername);
            ResultSet studentIds = getUsernames.executeQuery(sqlUsername);

            while (studentIds.next()) {
                usernames.add(studentIds.getString("student_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames.contains(username);
    }
}
