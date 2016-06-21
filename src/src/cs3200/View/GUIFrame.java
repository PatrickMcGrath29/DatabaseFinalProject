package cs3200.View;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GUIFrame extends JFrame implements IView{

    public GUIFrame() {
        this.setSize(400, 400);
        this.pack();

    }

    @Override
    public void getUserPass() {
        JTextField username = new JTextField("Username");
        JTextField password = new JTextField("Password");
        JButton login = new JButton("Login");
        JButton newUser = new JButton("New User");
        this.setLayout(new FlowLayout());
        this.getContentPane().add(username);
        this.getContentPane().add(password);
        this.getContentPane().add(login);
        this.getContentPane().add(newUser);

        this.pack();
    }

    @Override
    public void initialize() {
        this.setVisible(true);
    }
}
