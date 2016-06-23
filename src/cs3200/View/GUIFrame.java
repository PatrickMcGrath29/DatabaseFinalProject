package cs3200.View;

import cs3200.Controller.DatabaseController;

import javax.management.StandardEmitterMBean;
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
    private String currentThreadID = "";

    public GUIFrame(DatabaseController controller) {
        this.controller = controller;
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setTitle("University Forums - Designed by Patrick McGrath and Alex Zilberscher");
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
                this.setPanel(getMainPage());
            } else {
                JOptionPane.showMessageDialog(this, "Unknown " +
                        "Username/Password Combo" +
                        ".");
            }
        });

        newUser.addActionListener((ActionEvent e) -> {
            this.setPanel(getUserData());
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
            } else if (userText.getText().equals("")){
                JOptionPane.showMessageDialog(this, "Please enter username.");
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
                this.setPanel(getMainPage());
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
        JButton logout = new JButton("Logout");

        panel.add(createGroup);
        panel.add(listGroups);
        panel.add(myGroups);
        panel.add(myNotes);
        panel.add(sendNote);
        panel.add(logout);


        //Create Group Button
        createGroup.addActionListener((ActionEvent e) -> {
            this.setPanel(createGroupPage());
        });

        //List All Groups the user is not a part of action listener
        listGroups.addActionListener((ActionEvent e) -> {
            this.setPanel(listGroupsPage());
        });

        //List all of my Groups action listener
        myGroups.addActionListener((ActionEvent e) -> {
            this.setPanel(myGroupsPage());
        });

        //Lists all of my notes. The notes I have received appear first, then the notes I have
        // sent.
        myNotes.addActionListener((ActionEvent e) -> {
            this.setPanel(myNotesPage());
        });

        //Send Note action listener
        sendNote.addActionListener((ActionEvent e) -> {
            this.setPanel(sendNotesPage());
        });

        logout.addActionListener((ActionEvent e) -> {
            this.guiUser = "";
            this.guiPass = "";
            this.setPanel(this.getUserPass());
        });

        return panel;
    }

    private JPanel threadsListPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton viewThread = new JButton("View Thread");
        JButton createThread = new JButton("Create a Thread");
        JButton back = new JButton("Back");
        JButton main = new JButton("Home");
        ArrayList<String> threads = new ArrayList<String>();
        String sqlThreads = "SELECT * FROM thread WHERE group_id = ?";
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlThreads);
            prep.setString(1, this.currentGroupID);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                threads.add("Thread ID: " + rs.getString(1) + ", Poster: " + rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JComboBox threadsInGroup = new JComboBox(threads.toArray());
        panel.add(new JLabel("Threads currently in this group."));
        panel.add(threadsInGroup);
        panel.add(viewThread);
        panel.add(createThread);
        panel.add(back);
        panel.add(main);


        back.addActionListener((ActionEvent e) -> {
            this.setPanel(individualGroup());
        });
        main.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });

        viewThread.addActionListener((ActionEvent e) -> {
            this.currentThreadID = threadsInGroup.getSelectedItem().toString();
            this.setPanel(individualThreadPage());
        });

        createThread.addActionListener((ActionEvent e) -> {
            this.setPanel(createThreadPage());
        });

        return panel;
    }

    private JPanel individualThreadPage() {
        JPanel panel = new JPanel();
        JButton back = new JButton("Back");
        panel.setLayout(new GridLayout(0, 1));
        JLabel currentThread = new JLabel("Current Thread: " + this.currentThreadID);
        JButton main = new JButton("Home");
        JLabel threadText = new JLabel();

        String sqlGetText = "SELECT thread_text FROM thread WHERE thread_id = ?";
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlGetText);
            prep.setString(1, this.currentThreadID);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                threadText.setText(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(currentThread);
        panel.add(new JLabel("Thread Text:"));
        panel.add(threadText);
        panel.add(back);
        panel.add(main);

        back.addActionListener((ActionEvent e) -> {
            this.setPanel(threadsListPage());
        });
        main.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });

        return panel;
    }

    private JPanel createThreadPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JLabel threadLabel = new JLabel("Enter the Thread Text below, and click Create");
        JTextField threadText = new JTextField(50);
        JButton create = new JButton("Create");

        panel.add(threadLabel);
        panel.add(threadText);
        panel.add(create);

        create.addActionListener((ActionEvent e) -> {

            String sqlInsert = "INSERT INTO thread (group_id, thread_text, thread_poster) VALUES (?, ?, ?)";
            String sqlGetID = "SELECT thread_id FROM thread WHERE thread_text = ? AND " +
                    "thread_poster = ?";

            try {
                PreparedStatement prep = controller.conn.prepareStatement(sqlInsert);
                prep.setString(1, this.currentGroupID);
                prep.setString(2, threadText.getText());
                prep.setString(3, this.guiUser);
                prep.executeUpdate();

                PreparedStatement prep1 = controller.conn.prepareStatement(sqlGetID);
                prep1.setString(1, threadText.getText());
                prep1.setString(2, this.guiUser);
                ResultSet rs = prep1.executeQuery();
                while (rs.next()) {
                    this.currentThreadID = rs.getInt(1) + "";
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            this.setPanel(individualThreadPage());
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

            String sqlAdmin = "INSERT INTO group_admin (group_id, student_id) " +
                    "VALUES (?, ?)";
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

                PreparedStatement prep4 = controller.conn.prepareCall
                        (sqlAdmin);
                prep4.setInt(1, groupID);
                prep4.setString(2, this.guiUser);
                prep4.execute();
                this.currentGroupID = groupID + "";
                this.setPanel(this.individualGroup());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });

        return panel;
    }

    private JPanel listGroupsPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");
        JLabel joinLabel = new JLabel("To join, enter group number");
        JButton join = new JButton("Join");
        ArrayList<String> groups = new ArrayList<String>();
        ArrayList<String> resultGroups = new ArrayList<String>();
        String sqlGroups = "SELECT * FROM groups";
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlGroups);
            ResultSet result = prep.executeQuery();
            while(result.next()) {
                groups.add(result.getString(2));
            }
            for (int i = 0; i < groups.size(); i++) {
                String sql2 = "SELECT groups.group_id \n" +
                        "\tFROM members JOIN groups \n" +
                        "    ON groups.group_id = members.group_id \n" +
                        "    WHERE members.student_id = '" +
                        guiUser + "'";
                Statement stmt = controller.conn.createStatement();
                result = stmt.executeQuery(sql2);
                if(result.next() && result.getString(1).equals(groups.get(i))) {
                    groups.remove(i);
                }
                stmt.close();
            }
            String sql3 = "";
            for(String s : groups) {
                sql3 = "SELECT groups.group_name, groups.group_id, " +
                        "groups.college_name FROM groups WHERE group_id = '"
                        + s + "'";
                Statement stmt = controller.conn.createStatement();
                result = stmt.executeQuery(sql3);
                while (result.next()) {
                    String labelText =  result.getString(2) + ": " + result
                            .getString(1) +
                            " affiliated with " + result.getString(3);
                    panel.add(new JLabel(labelText));
                    resultGroups.add(result.getString(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JComboBox joinGroup = new JComboBox(resultGroups.toArray());
        panel.add(joinLabel);
        panel.add(joinGroup);
        panel.add(join);
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });

        //Adds the user to the group
        join.addActionListener((ActionEvent e) -> {
            String sqlStatement = "INSERT INTO members (group_id, student_id) values (?, ?)";
            try {
                String sql2 = "SELECT user_is_banned(?, ?)";
                PreparedStatement prep2 = controller.conn.prepareStatement
                        (sql2);
                prep2.setString(1, this.guiUser);
                prep2.setString(2, joinGroup.getSelectedItem().toString());
                ResultSet rs = prep2.executeQuery();
                rs.next();
                if(!rs.getBoolean(1)) {
                    PreparedStatement prep = controller.conn.prepareStatement(sqlStatement);
                    this.currentGroupID = joinGroup.getSelectedItem().toString();
                    prep.setString(1, currentGroupID);
                    prep.setString(2, this.guiUser);
                    prep.execute();
                    this.setPanel(this.individualGroup());
                }
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
        JLabel enterLabel = new JLabel("Enter group number to go to group " +
                "view");


        String sqlGroups = "CALL get_groups(?)";
        ArrayList<String> groups = new ArrayList<String>();
        try {
            PreparedStatement prep = controller.conn.prepareStatement(sqlGroups);
            prep.setString(1, this.guiUser);
            ResultSet result = prep.executeQuery();
            while (result.next()) {
                String labelText =  result.getString(2) + ": " + result
                        .getString(1) +
                        " affiliated with " + result.getString(3);
                panel.add(new JLabel(labelText));
                groups.add(result.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JComboBox enterGroup = new JComboBox(groups.toArray());
        panel.add(enterLabel);
        panel.add(enterGroup);
        panel.add(goToGroup);
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });

        goToGroup.addActionListener((ActionEvent e) -> {
            this.currentGroupID = enterGroup.getSelectedItem().toString();
            this.setPanel(individualGroup());
        });

        return panel;

    }

    private JPanel myNotesPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        try {
            JLabel sent = new JLabel("Sent: ");
            JComboBox sents;
            JButton view = new JButton("View Message");
            JLabel received = new JLabel("Received: ");
            JComboBox from;
            JButton view2 = new JButton("View Message");
            JButton mainPage = new JButton("Home");
            String sql1 = "SELECT student_to_id, note_id FROM notes WHERE " +
                    "student_from_id = '" + this.guiUser + "' ORDER BY note_id";
            String sql2 = "SELECT student_from_id, note_id FROM notes WHERE " +
                    "student_to_id = '" + this.guiUser + "' ORDER BY note_id";
            Statement stmt1 = controller.conn.createStatement();
            Statement stmt2 = controller.conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(sql1);
            ResultSet rs2 = stmt2.executeQuery(sql2);
            ArrayList<String> names1 = new ArrayList<String>();
            ArrayList<String> names2 = new ArrayList<String>();
            while(rs1.next()) {
                names1.add("ID: " + rs1.getString(2) + " To: " + rs1.getString
                        (1));
            }
            while(rs2.next()) {
                names2.add("ID: " + rs2.getString(2) + " To: " + rs2.getString(1));
            }
            sents = new JComboBox(names1.toArray());
            from = new JComboBox(names2.toArray());
            panel.add(sent);
            panel.add(sents);
            panel.add(view);
            panel.add(received);
            panel.add(from);
            panel.add(view2);
            panel.add(mainPage);

            view.addActionListener((ActionEvent e) -> {
                this.setPanel(getNotePage(sents.getSelectedItem().toString()
                        .substring(4)));
            });

            view2.addActionListener((ActionEvent e) -> {
                this.setPanel(getNotePage(from.getSelectedItem().toString().substring(4)));
            });

            mainPage.addActionListener((ActionEvent e) -> {
                this.setPanel(getMainPage());
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;

    }

    private JPanel getNotePage(String id) {
        String realID = "";
        for(char c : id.toCharArray()) {
            if (c == ' ') {
                break;
            } else {
                realID += c;
            }
        }
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");
        JLabel noteID = new JLabel("ID: " + realID);
        JLabel to;
        JLabel from;
        JLabel body;
        try {
            String sql1 = "SELECT student_to_id, student_from_id, note_text " +
                    "FROM notes WHERE note_id = ?";
            PreparedStatement prep1 = controller.conn.prepareStatement(sql1);
            prep1.setString(1, id);
            ResultSet rs1 = prep1.executeQuery();
            rs1.next();
            to = new JLabel("To: " + rs1.getString(1));
            from = new JLabel("From: " + rs1.getString(2));
            body = new JLabel("Message: " + rs1.getString(3));

            panel.add(noteID);
            panel.add(to);
            panel.add(from);
            panel.add(body);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });
        return panel;
    }

    private JPanel sendNotesPage() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        try {
            JLabel recip = new JLabel("Recipient: ");
            JComboBox to;
            JLabel body = new JLabel("Message: ");
            JTextField message = new JTextField(15);
            JButton send = new JButton("Send Message");
            JButton mainPage = new JButton("Home");
            String sql = "SELECT student_id FROM students WHERE NOT " +
                    "student_id = '" + this.guiUser + "'";
            Statement stmt = controller.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<String> names = new ArrayList<String>();
            while(rs.next()) {
                names.add(rs.getString(1).toString());
            }
            to = new JComboBox(names.toArray());
            panel.add(recip);
            panel.add(to);
            panel.add(body);
            panel.add(message);
            panel.add(send);
            panel.add(mainPage);

            send.addActionListener((ActionEvent e) -> {
                String sqlStatement = "INSERT INTO notes (student_from_id, " +
                        "student_to_id, note_text) values (?, ?, ?)";
                try {
                    PreparedStatement prep = controller.conn.prepareStatement(sqlStatement);
                    prep.setString(1, this.guiUser);
                    prep.setString(2, to.getSelectedItem().toString());
                    prep.setString(3, message.getText());
                    prep.execute();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                this.setPanel(getMainPage());
            });

            mainPage.addActionListener((ActionEvent e) -> {
                this.setPanel(getMainPage());
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return panel;
    }

    private JPanel individualGroup() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JButton mainPage = new JButton("Home");
        JLabel groupName = new JLabel("");
        JLabel groupID = new JLabel("ID: " + this.currentGroupID);
        JLabel groupStatement = new JLabel("");
        JLabel members = new JLabel("Members: ");
        ArrayList<String> kickable = new ArrayList<>();
        JLabel admins = new JLabel("Admins: ");

        JLabel threadsLabel = new JLabel("Click Threads to view all threads for selected group");
        JButton threads = new JButton("Threads");

        boolean admin = false;
        try {
            String sql1 = "SELECT group_name FROM groups WHERE group_id = ?";
            PreparedStatement prep1 = controller.conn.prepareStatement(sql1);
            prep1.setString(1, this.currentGroupID);
            ResultSet rs1 = prep1.executeQuery();
            rs1.next();
            groupName = new JLabel("Name: " + rs1.getString(1));

            String sql2 = "SELECT purpose_statement FROM groups WHERE " +
                    "group_id = ?";
            PreparedStatement prep2 = controller.conn.prepareStatement(sql2);
            prep2.setString(1, this.currentGroupID);
            ResultSet rs2 = prep2.executeQuery();
            rs2.next();
            groupStatement = new JLabel("Purpose Statement: " + rs2.getString
                    (1));

            panel.add(groupName);
            panel.add(groupID);
            panel.add(groupStatement);
            panel.add(threadsLabel);
            panel.add(threads);

            threads.addActionListener((ActionEvent e) -> {
                this.setPanel(threadsListPage());
            });

            String sql3 = "CALL get_members(" + this.currentGroupID + ")";
            PreparedStatement prep3 = controller.conn.prepareStatement(sql3);
            ResultSet rs3 = prep3.executeQuery();
            while(rs3.next()) {
                String s = rs3.getString(1);
                kickable.add(s);
            }
            panel.add(admins);
            String sql4 = "CALL get_admins(" + this.currentGroupID + ")";
            PreparedStatement prep4 = controller.conn.prepareStatement(sql4);
            ResultSet rs4 = prep4.executeQuery();
            while(rs4.next()) {
                String s = rs4.getString(1);
                if (s.equals(guiUser)) {
                    admin = true;
                }
                for (int i = 0; i < kickable.size(); i++) {
                    if(kickable.get(i).equals(s)) {
                        kickable.remove(i);
                        i--;
                    }
                }
                panel.add(new JLabel(s));
            }
            panel.add(members);


            for(String s : kickable) {
                panel.add(new JLabel(s));
            }
            if (admin) {
                JComboBox kicks = new JComboBox(kickable.toArray());
                JButton kick = new JButton("Kick member");
                JButton delete = new JButton("Close Group");
                panel.add(kicks);
                panel.add(kick);
                panel.add(delete);

                kick.addActionListener((ActionEvent e) -> {
                    String sql5 = "DELETE FROM members WHERE group_id = ? AND" +
                            " " +
                            "student_id = ?;";
                    String sql6 = "INSERT INTO ban_list VALUES (?, ?)";
                    try {
                        PreparedStatement prep = controller.conn
                                    .prepareStatement(sql5);
                        prep.setString(1, this.currentGroupID);
                        prep.setString(2, kicks.getSelectedItem().toString());
                        prep.execute();
                        prep = controller.conn.prepareStatement(sql6);
                        prep.setString(2, this.currentGroupID);
                        prep.setString(1, kicks.getSelectedItem().toString());
                        prep.execute();
                        this.setPanel(individualGroup());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });

                delete.addActionListener((ActionEvent e) -> {
                    String sql7 = "DELETE FROM groups WHERE group_id = ?";
                    try {
                        PreparedStatement prep = controller.conn
                                .prepareStatement(sql7);
                        prep.setString(1, this.currentGroupID);
                        prep.execute();
                        this.setPanel(getMainPage());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        panel.add(mainPage);

        mainPage.addActionListener((ActionEvent e) -> {
            this.setPanel(getMainPage());
        });
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
    public void setPanel(JPanel panel) {
        this.remove(current);
        this.current = panel;
        this.add(current);
        this.refresh();
    }

    private void refresh() {
        this.revalidate();
        this.repaint();
        this.pack();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 450);
    }
}
