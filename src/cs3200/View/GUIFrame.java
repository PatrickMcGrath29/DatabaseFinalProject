package cs3200.View;

import cs3200.Controller.DatabaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;




public class GUIFrame extends JFrame implements IView{

    private String guiUser, guiPass;
    private DatabaseController controller;
    JPanel current;

    public GUIFrame(DatabaseController controller) {
        this.controller = controller;
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.current = this.getUserPass();
        this.add(getUserPass());
        this.pack();
    }

    @Override
    public void initialize() {
        this.setVisible(true);
    }

    private JPanel getUserPass() {
        JPanel panel = new JPanel();
        JTextField username = new JTextField(15);
        JTextField password = new JTextField(15);
        JButton login = new JButton("Login");
        JButton newUser = new JButton("New User");
        JLabel user = new JLabel("Username:");
        JLabel pass = new JLabel("Password:");

        panel.add(user);
        panel.add(username);
        panel.add(pass);
        panel.add(password);
        panel.add(login);
        panel.add(newUser);

        login.setActionCommand("LOGIN\n" + username.getText() + "\n" + password.getText());
        login.addActionListener(controller);

        newUser.setActionCommand("NEWUSER");
        newUser.addActionListener(controller);
        this.pack();
        return panel;
    }

    private JPanel getUserData() {
        JPanel panel = new JPanel();
        JLabel userLable = new JLabel("Enter a username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Enter a password:");
        JTextField passText = new JTextField(15);

    }


    private void newUserFrame() {
        this.getContentPane().add(new JLabel("Enter a username:"));
        JTextField newUsername = new JTextField(15);
        this.getContentPane().add(newUsername);
    }
}
