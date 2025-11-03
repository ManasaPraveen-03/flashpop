package main.java;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class HospitalManagementSystem extends JFrame {

    private final ArrayList<Patient> patients = new ArrayList<>();
    private final ArrayList<Doctor> doctors = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel patientModel, doctorModel;
    private JTable patientTable, doctorTable;
    private JTextField txtName, txtAge, txtDisease, txtRoom, txtDays, txtDocName, txtSpec;
    private JCheckBox chkAdmitted, chkAvailable;
    private JLabel lblBill;
    private JComboBox<String> cmbDoctors;

    public HospitalManagementSystem() {
        setTitle("🏥 Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createWelcomePanel(), "welcome");
        mainPanel.add(createDashboardPanel(), "dashboard");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
        setVisible(true);
    }
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(210, 230, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel lblTitle = new JLabel("🏥 Hospital Management System");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 28));

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 16));
        lblPass.setFont(new Font("Arial", Font.BOLD, 16));

        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.addActionListener(e -> {
            if (txtUser.getText().equals("admin") && new String(txtPass.getPassword()).equals("1234")) {
                cardLayout.show(mainPanel, "welcome");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.gridy++;
        panel.add(lblUser, gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy++;
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        return panel;
    }
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 250, 255));

        JLabel lbl = new JLabel("✨ Welcome to Hospital Management System ✨", SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 28));
        lbl.setForeground(new Color(25, 25, 112));

        JButton btnProceed = new JButton("Go to Dashboard");
        btnProceed.setFont(new Font("Arial", Font.BOLD, 18));
        btnProceed.setBackground(new Color(70, 130, 180));
        btnProceed.setForeground(Color.WHITE);
        btnProceed.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));

        panel.add(lbl, BorderLayout.CENTER);
        panel.add(btnProceed, BorderLayout.SOUTH);
        return panel;
    }
    private JPanel createDashboardPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", createPatientPanel());
        tabs.addTab("Doctors", createDoctorPanel());

        JPanel panel = new JPanel(new BorderLayout());
        JLabel topLabel = new JLabel("🏥 Hospital Dashboard", SwingConstants.CENTER);
        topLabel.setFont(new Font("Serif", Font.BOLD, 26));
        topLabel.setOpaque(true);
        topLabel.setBackground(new Color(70, 130, 180));
        topLabel.setForeground(Color.WHITE);

        panel.add(topLabel, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(250, 250, 255));

        patientModel = new DefaultTableModel(
                new Object[]{"Name", "Age", "Disease", "Admitted", "Room", "Days", "Bill", "Doctor"}, 0);
        patientTable = new JTable(patientModel);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBackground(new Color(230, 240, 255));

        txtName = new JTextField();
        txtAge = new JTextField();
        txtDisease = new JTextField();
        chkAdmitted = new JCheckBox("Admitted?");
        txtRoom = new JTextField();
        txtDays = new JTextField();
        lblBill = new JLabel("Bill: ₹0");
        cmbDoctors = new JComboBox<>();

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Age:")); form.add(txtAge);
        form.add(new JLabel("Disease:")); form.add(txtDisease);
        form.add(new JLabel("Admitted:")); form.add(chkAdmitted);
        form.add(new JLabel("Room No:")); form.add(txtRoom);
        form.add(new JLabel("Days Stayed:")); form.add(txtDays);
        form.add(new JLabel("Assign Doctor:")); form.add(cmbDoctors);
        form.add(new JLabel("Total Bill:")); form.add(lblBill);

        JButton btnAdd = new JButton("Add Patient");
        JButton btnRemove = new JButton("Remove Selected");
        JButton btnClear = new JButton("Clear");
        JButton btnAssign = new JButton("Assign Doctor");
        styleButton(btnAdd);
        styleButton(btnRemove);
        styleButton(btnClear);
        styleButton(btnAssign);

        btnAdd.addActionListener(e -> addPatient());
        btnRemove.addActionListener(e -> removePatient());
        btnClear.addActionListener(e -> clearPatientForm());
        btnAssign.addActionListener(e -> assignDoctorToPatient());

        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnAssign);
        buttons.add(btnRemove);
        buttons.add(btnClear);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }
    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(250, 250, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        doctorModel = new DefaultTableModel(new Object[]{"Name", "Specialization", "Available"}, 0);
        doctorTable = new JTable(doctorModel);

        txtDocName = new JTextField();
        txtSpec = new JTextField();
        chkAvailable = new JCheckBox("Available");

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBackground(new Color(230, 240, 255));
        form.add(new JLabel("Doctor Name:")); form.add(txtDocName);
        form.add(new JLabel("Specialization:")); form.add(txtSpec);
        form.add(new JLabel("Availability:")); form.add(chkAvailable);

        JButton btnAdd = new JButton("Add Doctor");
        JButton btnRemove = new JButton("Remove Selected");
        styleButton(btnAdd);
        styleButton(btnRemove);

        btnAdd.addActionListener(e -> addDoctor());
        btnRemove.addActionListener(e -> removeDoctor());

        JPanel btns = new JPanel();
        btns.add(btnAdd);
        btns.add(btnRemove);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }
    private void addPatient() {
        try {
            String name = txtName.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());
            String disease = txtDisease.getText().trim();
            boolean admitted = chkAdmitted.isSelected();
            String room = txtRoom.getText().trim();
            int days = txtDays.getText().isEmpty() ? 0 : Integer.parseInt(txtDays.getText().trim());
            String doctor = (String) cmbDoctors.getSelectedItem();

            double bill = admitted ? (days * 1500 + 500) : 300;
            lblBill.setText("Bill: ₹" + bill);

            patients.add(new Patient(name, age, disease, admitted, room, days, bill, doctor));
            patientModel.addRow(new Object[]{name, age, disease, admitted ? "Yes" : "No", room, days, "₹" + bill, doctor});
            clearPatientForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removePatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            patients.remove(row);
            patientModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Select a patient first!");
        }
    }

    private void clearPatientForm() {
        txtName.setText("");
        txtAge.setText("");
        txtDisease.setText("");
        chkAdmitted.setSelected(false);
        txtRoom.setText("");
        txtDays.setText("");
        lblBill.setText("Bill: ₹0");
        cmbDoctors.setSelectedIndex(-1);
    }

    private void addDoctor() {
        String name = txtDocName.getText().trim();
        String spec = txtSpec.getText().trim();
        boolean available = chkAvailable.isSelected();

        if (name.isEmpty() || spec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        doctors.add(new Doctor(name, spec, available));
        doctorModel.addRow(new Object[]{name, spec, available ? "Yes" : "No"});
        cmbDoctors.addItem(name + " (" + spec + ")");
        txtDocName.setText("");
        txtSpec.setText("");
        chkAvailable.setSelected(false);
    }

    private void removeDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row >= 0) {
            String docName = (String) doctorModel.getValueAt(row, 0);
            cmbDoctors.removeItem(docName);
            doctors.remove(row);
            doctorModel.removeRow(row);
        }
    }

    private void assignDoctorToPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a patient first!");
            return;
        }

        String selectedDoctor = (String) cmbDoctors.getSelectedItem();
        if (selectedDoctor == null) {
            JOptionPane.showMessageDialog(this, "No available doctor selected!");
            return;
        }

        patientModel.setValueAt(selectedDoctor, selectedRow, 7);
        JOptionPane.showMessageDialog(this, "Doctor assigned successfully!");
    }

    private void styleButton(JButton b) {
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
    }
    static class Patient {
        String name, disease, room, doctor;
        int age, days;
        boolean admitted;
        double bill;
        Patient(String n, int a, String d, boolean ad, String r, int day, double b, String doc) {
            name = n; age = a; disease = d; admitted = ad; room = r; days = day; bill = b; doctor = doc;
        }
    }

    static class Doctor {
        String name, specialization;
        boolean available;
        Doctor(String n, String s, boolean a) { name = n; specialization = s; available = a; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalManagementSystem::new);
    }
}
