package cs3200.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;



/**
 * GUI Class for the front end program.
 */
public class GUIFrame extends JFrame implements IView{

    String guiUser, guiPass;

    /**
     * Constructs an instance of this class, setting the size, close operation, and layout.
     */
    public GUIFrame() {
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.pack();
    }

    /**
     * Enumeration to represent the next frame to be opened, when the current frame is cleared.
     */
    private enum NextFrame {
        login, newUser
    }

    @Override
    public void getUserPass() {
        JTextField username = new JTextField(15);
        JTextField password = new JTextField(15);
        JButton login = new JButton("Login");
        JButton newUser = new JButton("New User");
        JLabel user = new JLabel("Username:");
        JLabel pass = new JLabel("Password:");
        this.getContentPane().add(user);
        this.getContentPane().add(username);
        this.getContentPane().add(pass);
        this.getContentPane().add(password);
        this.getContentPane().add(login);
        this.getContentPane().add(newUser);

        login.addActionListener((ActionEvent e) -> {
            guiUser = username.getText();
            guiPass = password.getText();
            username.setText("");
            password.setText("");
            this.clearAll(null); //TODO Add next frame
        });

        newUser.addActionListener((ActionEvent e) -> {
            this.clearAll(NextFrame.newUser);
        });
        this.pack();
    }

    @Override
    public void initialize() {
        this.setVisible(true);
    }

    private void clearAll(NextFrame next) {
        this.removeAll();

        switch (next) {
            case newUser:
                this.newUserFrame();
                break;
            default:
                throw new IllegalArgumentException("Unknown NextType enumeration");
        }
    }

    private void newUserFrame() {
        this.getContentPane().add(new JLabel("Enter a username:"));
        JTextField newUsername = new JTextField(15);
        this.getContentPane().add(newUsername);
    }
}
