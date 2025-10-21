import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlashPop extends JFrame {

    private static Map<String, User> users = new HashMap<>();
    private static User currentUser = null;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private JTextArea notesAreaGlobal;
    private DefaultListModel<String> flashListModel;
    private java.util.List<Flashcard> quizQueue = new ArrayList<>();
    private int quizIndex = 0;

    public FlashPop() {
        setTitle("FlashPop");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDemoUser();
        initAuthUI();
    }

    private void initDemoUser() {
        users.put("demo", new User("demo","demo123","Demo User"));
    }

    // AUTH
    private void initAuthUI() {
        JPanel authPanel = new JPanel(new GridBagLayout());
        authPanel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField nameField = new JTextField(20);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        int row=0;
        c.gridx=0;c.gridy=row; authPanel.add(new JLabel("Username:"),c);
        c.gridx=1; authPanel.add(usernameField,c); row++;
        c.gridx=0;c.gridy=row; authPanel.add(new JLabel("Password:"),c);
        c.gridx=1; authPanel.add(passwordField,c); row++;
        c.gridx=0;c.gridy=row; authPanel.add(loginBtn,c);
        c.gridx=1; authPanel.add(signupBtn,c);

        loginBtn.addActionListener(e->{
            String u=usernameField.getText().trim();
            String p=new String(passwordField.getPassword());
            if(authenticate(u,p)) { showDashboard(); }
            else {
                int option=JOptionPane.showConfirmDialog(this,
                        "User not found. Sign up?","User Not Found",JOptionPane.YES_NO_OPTION);
                if(option==JOptionPane.YES_OPTION){
                    showSignupDialog(nameField,usernameField,passwordField);
                }
            }
        });

        signupBtn.addActionListener(e->showSignupDialog(nameField,usernameField,passwordField));

        mainPanel.add(authPanel,"auth");
        add(mainPanel);
        cardLayout.show(mainPanel,"auth");
    }

    private void showSignupDialog(JTextField nameField, JTextField usernameField, JPasswordField passwordField){
        JPanel signupPanel = new JPanel(new GridLayout(3,2,10,10));
        signupPanel.add(new JLabel("Name:")); signupPanel.add(nameField);
        signupPanel.add(new JLabel("Username:")); signupPanel.add(usernameField);
        signupPanel.add(new JLabel("Password:")); signupPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, signupPanel,"Sign Up",JOptionPane.OK_CANCEL_OPTION);
        if(result==JOptionPane.OK_OPTION){
            String name=nameField.getText().trim();
            String user=usernameField.getText().trim();
            String pass=new String(passwordField.getPassword());
            if(name.isEmpty() || user.isEmpty() || pass.isEmpty()){
                JOptionPane.showMessageDialog(this,"All fields required","Sign Up",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(users.containsKey(user)){
                JOptionPane.showMessageDialog(this,"Username exists","Sign Up",JOptionPane.ERROR_MESSAGE);
                return;
            }
            User newUser=new User(user,pass,name);
            users.put(user,newUser);
            JOptionPane.showMessageDialog(this,"Sign up successful! Login now.","Sign Up",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean authenticate(String username,String password){
        User u=users.get(username);
        if(u!=null && u.password.equals(password)){ currentUser=u; return true; }
        return false;
    }

    // DASHBOARD
    private void showDashboard(){
        if(currentUser==null) return;
        mainPanel.add(createDashboardPanel(),"dashboard");
        cardLayout.show(mainPanel,"dashboard");
    }

    private JPanel createDashboardPanel(){
        JPanel panel=new JPanel(new BorderLayout(8,8));
        panel.add(createTopBar(), BorderLayout.NORTH);

        JPanel center=new JPanel(new GridLayout(2,2,12,12));
        center.add(makeBlock("Taking Notes","Create / save notes",new Color(224,242,254), e->
                { mainPanel.add(createNotesPanel(),"notes"); cardLayout.show(mainPanel,"notes"); }));
        center.add(makeBlock("Generate Flashcards","Manually add flashcards",new Color(220,252,231), e->
                { mainPanel.add(createFlashcardsPanel(),"flashcards"); cardLayout.show(mainPanel,"flashcards"); }));
        center.add(makeBlock("Take Quiz","Yes / No recap quiz",new Color(255,229,229), e->
                { mainPanel.add(createQuizPanel(),"quiz"); cardLayout.show(mainPanel,"quiz"); }));
        center.add(makeBlock("View Progress","See progress & accuracy",new Color(255,249,196), e->
                { mainPanel.add(createProgressPanel(),"progress"); cardLayout.show(mainPanel,"progress"); }));

        panel.add(center,BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeBlock(String title,String desc,Color bg,ActionListener action){
        JPanel card=new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(12,12,12,12)));
        JLabel t=new JLabel(title); t.setFont(t.getFont().deriveFont(Font.BOLD,16f));
        JTextArea d=new JTextArea(desc); d.setEditable(false); d.setOpaque(false); d.setLineWrap(true);
        d.setWrapStyleWord(true);
        JButton btn=new JButton("Open"); btn.addActionListener(action);
        card.add(t,BorderLayout.NORTH); card.add(d,BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTopBar(){
        JPanel top=new JPanel(new BorderLayout());
        JLabel welcome=new JLabel("Welcome, " + (currentUser!=null ? currentUser.name : ""));
        welcome.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        top.add(welcome, BorderLayout.WEST);

        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn=new JButton("Logout");
        logoutBtn.addActionListener(e->{
            currentUser=null;
            notesAreaGlobal = null;
            flashListModel = null;
            cardLayout.show(mainPanel,"auth");
        });
        right.add(logoutBtn);
        top.add(right, BorderLayout.EAST);
        top.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
        return top;
    }

    // NOTES PANEL
    private JPanel createNotesPanel(){
        JPanel panel=new JPanel(new BorderLayout(8,8));

        notesAreaGlobal = new JTextArea();
        notesAreaGlobal.setLineWrap(true);
        notesAreaGlobal.setWrapStyleWord(true);
        notesAreaGlobal.setFont(notesAreaGlobal.getFont().deriveFont(14f));
        panel.add(new JScrollPane(notesAreaGlobal), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save Notes");
        JButton back = new JButton("Back");

        saveBtn.addActionListener(e -> {
            String text = notesAreaGlobal.getText().trim();
            if(!text.isEmpty()) {
                currentUser.notes.add(new Note(text));
                JOptionPane.showMessageDialog(this,"Notes saved!");
            } else { JOptionPane.showMessageDialog(this,"Nothing to save."); }
        });

        back.addActionListener(e->cardLayout.show(mainPanel,"dashboard"));

        btnPanel.add(saveBtn);
        btnPanel.add(back);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // FLASHCARDS PANEL
    private JPanel createFlashcardsPanel(){
        JPanel panel = new JPanel(new BorderLayout(8,8));
        flashListModel = new DefaultListModel<>();
        for(Flashcard f: currentUser.flashcards) flashListModel.addElement(f.question);
        JList<String> list = new JList<>(flashListModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Flashcard");
        JButton back = new JButton("Back");

        addBtn.addActionListener(e->{
            JTextField qField = new JTextField();
            JTextField aField = new JTextField();
            Object[] objs = {"Question:", qField, "Answer:", aField};
            int res = JOptionPane.showConfirmDialog(this, objs, "Add Flashcard", JOptionPane.OK_CANCEL_OPTION);
            if(res == JOptionPane.OK_OPTION){
                String q = qField.getText().trim();
                String a = aField.getText().trim();
                if(!q.isEmpty() && !a.isEmpty()){
                    Flashcard f = new Flashcard(q,a);
                    currentUser.flashcards.add(f);
                    flashListModel.addElement(q);
                }
            }
        });

        back.addActionListener(e->cardLayout.show(mainPanel,"dashboard"));
        btnPanel.add(addBtn);
        btnPanel.add(back);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // QUIZ PANEL
    private JPanel createQuizPanel(){
        JPanel panel = new JPanel(new BorderLayout(8,8));
        quizQueue = new ArrayList<>(currentUser.flashcards);
        Collections.shuffle(quizQueue);
        quizIndex = 0;

        JLabel questionLabel = new JLabel();
        JButton knowBtn = new JButton("I know");
        JButton recapBtn = new JButton("Recap");

        ActionListener nextQ = e->{
            if(quizIndex >= quizQueue.size()){
                JOptionPane.showMessageDialog(this,"Quiz finished!");
                cardLayout.show(mainPanel,"dashboard");
                return;
            }
            Flashcard f = quizQueue.get(quizIndex);
            questionLabel.setText(f.question);
        };

        knowBtn.addActionListener(e->{
            quizIndex++;
            nextQ.actionPerformed(null);
        });

        recapBtn.addActionListener(e->{
            Flashcard f = quizQueue.get(quizIndex);
            JOptionPane.showMessageDialog(this,"Answer: " + f.answer);
            quizIndex++;
            nextQ.actionPerformed(null);
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(knowBtn); btnPanel.add(recapBtn);

        panel.add(questionLabel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        nextQ.actionPerformed(null);
        return panel;
    }

    // PROGRESS PANEL
    private JPanel createProgressPanel(){
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Total Notes: ").append(currentUser.notes.size()).append("\n");
        sb.append("Total Flashcards: ").append(currentUser.flashcards.size()).append("\n");
        ta.setText(sb.toString());
        panel.add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e->cardLayout.show(mainPanel,"dashboard"));
        panel.add(back, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new FlashPop().setVisible(true));
    }

    static class User{
        String username,password,name;
        java.util.List<Note> notes = new ArrayList<>();
        java.util.List<Flashcard> flashcards = new ArrayList<>();
        User(String u,String p,String n){ username=u; password=p; name=n; }
    }

    static class Note { String text; Note(String t){ text=t; } }
    static class Flashcard { String question,answer; Flashcard(String q,String a){ question=q; answer=a; } }
}
