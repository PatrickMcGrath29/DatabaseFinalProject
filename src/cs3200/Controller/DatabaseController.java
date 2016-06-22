package cs3200.Controller;

import cs3200.Model.IModel;
import cs3200.View.IView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.Scanner;

/**
 * Controller Class for the Database Design Front End program
 */
public class DatabaseController implements ActionListener {
    private String username, password;
    private Formatter ap;
    private IView view;
    private IModel model;

    public DatabaseController(IModel model) {
        this.model = model;
    }

    public void setView(IView view) {
        this.view = view;
        this.view.initialize();
    }

    public void run() {
        Connection conn = null;
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
            /*// executes query to get character names column
            ResultSet rs = stmt.executeQuery("SELECT character_name FROM characters");
            System.out.println("Character name to track: ");
            character_name = sc.nextLine();
            boolean invalid = true;
            // validates your character name
            while(invalid) {
                while(rs.next()) {
                    if(rs.getString(1).equals(character_name)) {
                        invalid = false;
                    }
                }
                if(invalid) {
                    // if given an invalid character name, will display the error message below
                    // and prompt user to try again
                    rs = stmt.executeQuery("SELECT character_name FROM characters");
                    System.out.println("Invalid character name, try again: ");
                    character_name = sc.nextLine();
                }
            }
            // executes track_character on our now valid character name
            rs = stmt.executeQuery("CALL track_character('"+ character_name + "')");
            // prints our result set
            String results = "";
            while(rs.next()) {
                for(int i = 1; i <= rs.getMetaData().getColumnCount() ; i++) {
                    results += rs.getMetaData().getColumnName(i) + ": " + rs.getString(i);
                    if (i < rs.getMetaData().getColumnCount()) {
                        results += ", ";
                    }
                }
                results += "\n";
            }
            results = results.substring(0,results.length() - 1);
            System.out.println(results);
            // closes our connection and ends the program.
            */
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("ERROR: Encountered exception");
            e.printStackTrace();
            return;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Scanner sc = new Scanner(e.getActionCommand());

        switch (sc.nextLine()) {
            case "LOGIN":
                username = sc.nextLine();
                password = sc.nextLine();

        }
    }

    /**
     * Checks if the username included in the parameter currently exists in the database.
     * @param username The username to be checked for existence in the DB
     * @return True if the username is in the database for a student, false otherwise
     */
    private boolean verifyUsername(String username) {

    }
}
