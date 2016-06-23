package cs3200.View;

import cs3200.Controller.DatabaseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 * Class representing the GUI View, presenting all of the data to the user.
 */

public class GUIFrame extends JFrame implements IView{

    private String guiUser, guiPass;
    private DatabaseController controller;
    JPanel current;
    private String currentGroupID = "";

    public enum PanelType {
        firstPanel, newUser, mainPage, listGroups, myGroups, createGroup, myNotes,
        sendNote, individualGroup
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

    /**
     * The first page the user sees, allowing the user to either login or create a new account.
     * @return The JPanel with all of the components for the first screen added into it.
     */
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

            String sqlVerify = "SELECT verify_user(?, ?)";
            int isVerified = 0;
            try {
                PreparedStatement prep = controller.conn.prepareStatement(sqlVerify);
                prep.setString(1, this.guiUser);
                prep.setString(2, this.guiPass);
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    isVerified = rs.getInt(1);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            if (isVerified == 1) {
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

    /**
     * The New User panel, allowing users to input their information into the database and create
     * an account.
     * @return The JPanel with all of the containers for the New User panel included in it.
     */
    private JPanel getUserData() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JTextField userText = new JTextField(15);
        JTextField passText = new JTextField(15);
        JTextField firstText = new JTextField(15);
        JTextField lastText = new JTextField(15);
        JButton makeAccount = new JButton("Make Account");


        JComboBox collegeBox = new JComboBox(this.getColleges().toArray());
        panel.add(new JLabel("Username:"));
        panel.add(userText);
        panel.add(new JLabel("Password:"));
        panel.add(passText, BorderLayout.LINE_START);
        panel.add(new JLabel("Select your college:"));
        panel.add(collegeBox, BorderLayout.CENTER);
        panel.add(new JLabel("First Name:"));
        panel.add(firstText);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastText);
        panel.add(makeAccount);

        makeAccount.addActionListener((ActionEvent e) -> {
            if (controller.verifyUsername(userText.getText())) {
                JOptionPane.showMessageDialog(this, "Username already used.");
            } else {
                String newS = "INSERT INTO students (student_id, student_password, first_name, last_name," +
                        " college_name)" + " values (?, ?, ?, ?, ?)";

                try {
                    PreparedStatement prep = controller.conn.prepareStatement(newS);
                    prep.setString(1, userText.getText());
                    prep.setString(2, passText.getText());
                    prep.setString(3, firstText.getText());
                    prep.setString(4, lastText.getText());
                    prep.setString(5, collegeBox.getSelectedItem().toString());
                    prep.execute();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                this.guiUser = userText.getText();
                this.guiPass = passText.getText();
                this.setPanel(PanelType.mainPage);
            }
        });

        return panel;
    }

    private JPanel getMainPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton createGroup = new JButton("Create a Group");
        JButton listGroups = new JButton("List of Groups");
        JButton myGroups = new JButton("My Groups");
        JButton myNotes = new JButton("My Notes");
        JButton sendNote = new JButton("Send a Note");

        panel.add(createGroup);
        panel.add(listGroups);
        panel.add(myGroups);
        panel.add(myNotes);
        panel.add(sendNote);

        //Create Group Button
        createGroup.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.createGroup);
        });

        //List All Groups the user is not a part of action listener
        listGroups.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.listGroups);
        });

        //List all of my Groups action listener
        myGroups.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.myGroups);
        });

        //Lists all of my notes. The notes I have received appear first, then the notes I have
        // sent.
        myNotes.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.myNotes);
        });

        //Send Note action listener
        sendNote.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.sendNote);
        });

        return panel;
    }

    private JPanel createGroupPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");

        JLabel enterText = new JLabel("Group Name");
        JButton createGroup = new JButton("Create Group");
        JComboBox colleges = new JComboBox(this.getColleges().toArray());
        JTextField groupName = new JTextField(15);
        JLabel purposeLabel = new JLabel("Group Purpose Statement:");
        JTextArea purposeStatement = new JTextArea(2, 40);

        panel.add(enterText);
        panel.add(groupName);
        panel.add(new JLabel("Select the college for the group"));
        panel.add(colleges);
        panel.add(purposeLabel);
        panel.add(purposeStatement);
        panel.add(createGroup);
        panel.add(mainPage);

        createGroup.addActionListener((ActionEvent e) -> {
            String sqlInsert = "INSERT INTO groups (group_name, college_name, purpose_statement)"
                    + "VALUES (?, ?, ?)";

            String sqlGetID = "SELECT group_id FROM groups WHERE group_name = ? " +
                    "AND purpose_statement = ?";

            String sqlMember = "INSERT INTO members (group_id, student_id) VALUES (?, ?)";
            int groupID = -1;
            try {
                PreparedStatement prep1 = controller.conn.prepareStatement(sqlInsert);
                prep1.setString(1, groupName.getText());
                prep1.setString(2, colleges.getSelectedItem().toString());
                prep1.setString(3, purposeStatement.getText());
                prep1.execute();

                PreparedStatement prep3 = controller.conn.prepareStatement(sqlGetID);
                prep3.setString(1, groupName.getText());
                prep3.setString(2, purposeStatement.getText());
                ResultSet groupIdFind = prep3.executeQuery();
                while (groupIdFind.next()) {
                    groupID = groupIdFind.getInt(1);
                }

                PreparedStatement prep2 = controller.conn.prepareCall(sqlMember);
                prep2.setInt(1, groupID);
                prep2.setString(2, this.guiUser);
                prep2.execute();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.mainPage);
        });

        return panel;
    }

    private JPanel listGroupsPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");
        JLabel groups = new JLabel();
        JTextField joinGroup = new JTextField(15);
        JLabel joinLabel = new JLabel("To join, enter group ID");
        JButton join = new JButton("Join");
        String labelText = "";

        String sqlGroups = "CALL get_other_groups(?)";
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlGroups);
            prep.setString(1, this.guiUser);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                labelText += "Name: " + result.getString(1) + "   ID: " + result.getString(2) +
                        "\n";
            }
            groups.setText(labelText);
            System.out.println(labelText);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        panel.add(groups);
        panel.add(joinLabel);
        panel.add(joinGroup);
        panel.add(join);
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.mainPage);
        });

        //Adds the user to the group
        join.addActionListener((ActionEvent e) -> {
            String sqlStatement = "INSERT INTO members (group_id, student_id) values (?, ?)";
            try {
                PreparedStatement prep = controller.conn.prepareStatement(sqlStatement);
                prep.setString(1, joinGroup.getText());
                prep.setString(2, this.guiUser);
                prep.execute();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        return panel;
    }

    private JPanel myGroupsPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");

        JButton goToGroup = new JButton("Continue");
        JLabel groups = new JLabel();
        JLabel enterLabel = new JLabel("Enter the Group ID to go to group view");
        JTextField enterGroup = new JTextField(15);
        String labelText = "";
        String sqlGroups = "CALL get_groups(?)";
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlGroups);
            prep.setString(1, this.guiUser);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                labelText += "Name: " + rs.getString(1) + "    ID: " + rs.getString(2) +
                        "\n";
            }
            groups.setText(labelText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.add(groups);
        this.add(enterLabel);
        this.add(enterGroup);
        this.add(goToGroup);

        panel.add(groups);
        panel.add(enterLabel);
        panel.add(enterGroup);
        panel.add(goToGroup);
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(PanelType.mainPage);
        });

        goToGroup.addActionListener((ActionEvent e) -> {
            this.currentGroupID = enterGroup.getText();
            this.setPanel(PanelType.individualGroup);
        });

        return panel;

    }

    private JPanel myNotesPage() {
        JPanel panel = new JPanel();
        JButton mainPage = new JButton("Home");

        return panel;

    }

    private JPanel sendNotesPage() {
        JPanel panel = new JPanel();
        JButton mainPage = new JButton("Home");

        return panel;
    }

    private JPanel individualGroup() {
        JPanel panel = new JPanel();
        JButton mainPage = new JButton("Home");

        return panel;
    }

    private ArrayList<String> getColleges() {
        ArrayList<String> colleges = new ArrayList<String>();
        String sqlColleges = "SELECT college_name FROM colleges";
        try {
            Statement stmt = controller.conn.createStatement();
            ResultSet collegeRS = stmt.executeQuery(sqlColleges);

            while (collegeRS.next()) {
                colleges.add(collegeRS.getString("college_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colleges;
    }

    @Override
    public void setPanel(PanelType panel) {
        switch (panel) {
            case firstPanel:
                this.remove(current);
                this.current = getUserPass();
                this.add(current);
                this.refresh();
                break;
            case newUser:
                this.remove(current);
                this.current = getUserData();
                this.add(current);
                this.refresh();

                break;
            case mainPage:
                this.remove(current);
                this.current = getMainPage();
                this.add(current);
                this.refresh();

                break;
            case createGroup:
                this.remove(current);
                this.current = createGroupPage();
                this.add(current);
                this.refresh();
                break;
            case listGroups:
                this.remove(current);
                this.current = listGroupsPage();
                this.add(current);
                this.refresh();
                break;
            case myGroups:
                this.remove(current);
                this.current = myGroupsPage();
                this.add(current);
                this.refresh();
                break;
            case myNotes:
                this.remove(current);
                this.current = myNotesPage();
                this.add(current);
                this.refresh();
                break;
            case sendNote:
                this.remove(current);
                this.current = sendNotesPage();
                this.add(current);
                this.refresh();
                break;
            case individualGroup:
                this.remove(current);
                this.current = individualGroup();
                this.add(current);
                this.refresh();
        }
    }

    private void refresh() {
        this.revalidate();
        this.repaint();
        this.pack();
    }
}
