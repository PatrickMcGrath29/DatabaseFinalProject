package cs3200.View;

import cs3200.Controller.DatabaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * Class representing the GUI View, presenting all of the data to the user.
 */

public class GUIFrame extends JFrame implements IView{

    private String guiUser, guiPass;
    private DatabaseController controller;
    JPanel current;

    public enum PanelType {
        firstPanel, newUser, mainPage
    }



    public GUIFrame(DatabaseController controller) {
        this.controller = controller;
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.current = this.getUserPass();
        this.add(current);

        this.pack();
    }

    @Override
    public void initialize() {
        this.setVisible(true);
        this.pack();
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

        login.addActionListener((ActionEvent e) -> {
            this.guiUser = username.getText();
            this.guiPass = password.getText();
            if (controller.verifyUsername(guiUser)) {
                this.setPanel(PanelType.mainPage);
            } else {
                JOptionPane.showMessageDialog(this, "Unknown Username.");
            }
        });

        newUser.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.newUser);
        });

        this.pack();
        return panel;
    }

    private JPanel getUserData() {
        JPanel panel = new JPanel();
        JLabel userLable = new JLabel("Enter a username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Enter a password:");
        JTextField passText = new JTextField(15);

        panel.add(userLable);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);

        return panel;
    }

    private JPanel getMainPage() {
        JPanel panel = new JPanel();
        return panel;
    }

    @Override
    public void setPanel(PanelType panel) {
        switch (panel) {
            case firstPanel:
                this.remove(current);
                this.current = getUserPass();
                this.add(current);
                break;
            case newUser:
                this.remove(current);
                this.current = getUserData();
                this.add(current);
                this.revalidate();
                this.repaint();
                break;
            case mainPage:
                this.remove(current);
                this.current = getMainPage();
                this.add(current);
        }
    }
}
