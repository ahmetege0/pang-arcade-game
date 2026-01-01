import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private JMenu gameMenu, optionsMenu, helpMenu;
    private JMenuItem registerItem, newGameItem, quitItem;
    private JMenuItem historyItem, highScoreItem;
    private JMenuItem easyItem, mediumItem, hardItem;
    private JMenuItem aboutItem;
    private static Difficulty currentDifficulty = Difficulty.MEDIUM;

    private JLabel backgroundLabel;
    private String loggedInUser;

    public MainFrame() {
        setTitle("Pang Arcade Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout());
        setContentPane(content);

        ImageIcon bgIcon = new ImageIcon("pangWelcome.png");
        Image img = bgIcon.getImage().getScaledInstance(768, 512, Image.SCALE_SMOOTH);
        
        backgroundLabel = new JLabel(new ImageIcon(img));
        backgroundLabel.setLayout(new BorderLayout());
        content.add(backgroundLabel, BorderLayout.CENTER);

        initMenuBar();
        setJMenuBar(menuBar);

        setSize(780, 562);   
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();

        gameMenu = new JMenu("Game");
        registerItem = new JMenuItem("Register");
        newGameItem = new JMenuItem("New Game");
        quitItem = new JMenuItem("Quit");

        gameMenu.add(registerItem);
        gameMenu.addSeparator();
        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(quitItem);

        registerItem.addActionListener(e -> {
            RegisterDialog rd = new RegisterDialog(MainFrame.this);
            rd.setVisible(true);
        });

        newGameItem.addActionListener(e -> {
            LoginDialog ld = new LoginDialog(MainFrame.this);
            ld.setVisible(true);
            if (ld.isSucceeded()) {
            	loggedInUser = ld.getUsername();
                switchToGamePanel();
            }
        });

        quitItem.addActionListener(e -> System.exit(0));

        optionsMenu = new JMenu("Options");
        historyItem = new JMenuItem("History");
        highScoreItem = new JMenuItem("High Score");
        
        JMenu difficultyMenu = new JMenu("Difficulty");
        easyItem = new JMenuItem("Easy");
        mediumItem = new JMenuItem("Medium");
        hardItem = new JMenuItem("Hard");
        
        difficultyMenu.add(easyItem);
        difficultyMenu.add(mediumItem);
        difficultyMenu.add(hardItem);

        optionsMenu.add(historyItem);
        optionsMenu.add(highScoreItem);
        optionsMenu.addSeparator();
        optionsMenu.add(difficultyMenu);
        
        easyItem.addActionListener(e -> {
            currentDifficulty = Difficulty.EASY;
            JOptionPane.showMessageDialog(
                this,
                "Difficulty set to EASY",
                "Difficulty",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        mediumItem.addActionListener(e -> {
            currentDifficulty = Difficulty.MEDIUM;
            JOptionPane.showMessageDialog(
                this,
                "Difficulty set to MEDIUM",
                "Difficulty",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        hardItem.addActionListener(e -> {
            currentDifficulty = Difficulty.HARD;
            JOptionPane.showMessageDialog(
                this,
                "Difficulty set to HARD",
                "Difficulty",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        historyItem.addActionListener(e -> {
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(
                    MainFrame.this,
                    "You should be logged in (New Game)",
                    "History",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            showHistoryDialog();
        });
        
        highScoreItem.addActionListener(e -> {
            showScoreboardDialog();
        });

        helpMenu = new JMenu("Help");
        aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                MainFrame.this,
                "Pang Arcade Game\nDeveloper: Ahmet Ege\nEmail: aege0601@gmail.com\n"
                + "Music: Yusuf Talha Toğrul",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        menuBar.add(gameMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
    }

    private void switchToGamePanel() {
        getContentPane().removeAll();

        int startingLevel = 1;
        GamePanel gamePanel = new GamePanel(currentDifficulty,startingLevel,loggedInUser);
        getContentPane().add(gamePanel, BorderLayout.CENTER);

        setJMenuBar(menuBar);

        gamePanel.requestFocusInWindow();

        revalidate();
        repaint();
    }
    
    private void showHistoryDialog() {
        ArrayList<String> lines = (ArrayList<String>) HistoryManager.readHistory(loggedInUser);
        if (lines.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "There is no playing history yet!",
                "History",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        for (String line : lines) {
            ta.append(line + "\n");
        }
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setPreferredSize(new Dimension(450, 300));

        JOptionPane.showMessageDialog(
            this,
            scroll,
            loggedInUser + " - Playing History",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showScoreboardDialog() {
        ArrayList<String> topList = (ArrayList<String>) HistoryManager.getTopMaxScores(10);
        if (topList.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "There is no any score record yet!",
                "Scoreboard",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.append("---- TOP 10 SCOREBOARD ----\n\n");
        for (String entry : topList) {
            textArea.append(entry + "\n");  
        }
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(300, 250));

        JOptionPane.showMessageDialog(
            this,
            scroll,
            "Scoreboard",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
    

