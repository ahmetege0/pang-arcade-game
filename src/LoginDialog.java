import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin, btnCancel;
    private boolean succeeded;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(300, 170);
        setLocationRelativeTo(parent);

        add(new JLabel("Username:"));
        tfUsername = new JTextField();
        add(tfUsername);

        add(new JLabel("Password:"));
        pfPassword = new JPasswordField();
        add(pfPassword);

        btnLogin = new JButton("Login");
        btnCancel = new JButton("Cancel");
        add(btnLogin);
        add(btnCancel);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText().trim();
                String password = new String(pfPassword.getPassword()).trim();

                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        "Welcome, " + username + "!",
                        "Succeed!",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        "Invalid user name or password",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    tfUsername.setText("");
                    pfPassword.setText("");
                    succeeded = false;
                }
            }
        });

        btnCancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private boolean authenticate(String user, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String u = parts[0].trim();
                    String p = parts[1].trim();
                    if (u.equals(user) && p.equals(pass)) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "reading document error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        return false;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }
}
