import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnRegister, btnCancel;

    public RegisterDialog(JFrame parent) {
        super(parent, "Register", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(300, 170);
        setLocationRelativeTo(parent);

        add(new JLabel("Username:"));
        tfUsername = new JTextField();
        add(tfUsername);

        add(new JLabel("Password:"));
        pfPassword = new JPasswordField();
        add(pfPassword);

        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");
        add(btnRegister);
        add(btnCancel);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText().trim();
                String password = new String(pfPassword.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        RegisterDialog.this,
                        "Lütfen hem kullanıcı adı hem şifre gir.",
                        "Eksik Bilgi",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
                    bw.write(username + "," + password);
                    bw.newLine();
                    JOptionPane.showMessageDialog(
                        RegisterDialog.this,
                        "Kayıt başarılı! Şimdi giriş yapabilirsin.",
                        "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                        RegisterDialog.this,
                        "Dosyaya yazma hatası: " + ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        btnCancel.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
