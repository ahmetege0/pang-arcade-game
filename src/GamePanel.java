import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int PANEL_WIDTH = 768;
    public static final int PANEL_HEIGHT = 512;

    private Thread gameThread;
    private volatile boolean running = false;

    private int levelFrameTimer = 90 * 40;
    private Difficulty difficulty;
    private int level;
    private Player player;
    private List<Bubble> bubbles = Collections.synchronizedList(new ArrayList<>());
    private List<Bullet> bullets = Collections.synchronizedList(new ArrayList<>());
    private final int MAX_BULLETS = 3;
    private int score = 0;
    private int levelScore = 0;
    private int lives;
    private boolean gameOver = false;
    private String loggedInUser;
    private int invincibleTimer = 0;
    private static final int INVINCIBLE_DURATION = 150;
    private int finishCountdown = 0;
    private static final int FINISH_COUNTDOWN = 180;
    private boolean waitingForEnter = true;
    private Image livesImage;
    private Image invisableImage;
    private Clip bgMusic;
    private Clip popSound;
    private int countdownTimer = 180;
    private static final int COUNTDOWN_START = 180;
    private GameState currentState = GameState.BEFORE_LEVEL;
    private int currentLevelIndex = 0;

    private final String[] levelNames = {
            "istanbul",
            "ankara",
            "antalya",
            "adana",
            "gobeklitepe",
            "agriDagi"
    };

    private final String[] levelNames2 = {
            "Istanbul",
            "Ankara",
            "Antalya",
            "Adana",
            "Göbeklitepe",
            "Ağrı Dağı"
    };

    private enum GameState {
        BEFORE_LEVEL,
        PLAYING,
        GAME_OVER
    }

    private boolean showingEndDialog = false;

    private Image[] roadmapImgs;
    private Image[] levelBackgrounds;

    public GamePanel(Difficulty difficulty, int initialLevel, String username) {

        loadPopSound();
        playBackgroundMusic();

        ImageIcon livesIcon = new ImageIcon("assets/lives.png");
        livesImage = livesIcon.getImage();

        ImageIcon invIcon = new ImageIcon("assets/invisable.png");
        invisableImage = invIcon.getImage();

        this.loggedInUser = username;
        this.difficulty = difficulty;
        this.level = initialLevel;
        this.currentLevelIndex = 0;

        switch (difficulty) {
            case EASY:
                lives = 5;
                break;
            case MEDIUM:
                lives = 3;
                break;
            case HARD:
                lives = 2;
                break;
            default:
                lives = 3;
                break;
        }

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);

        int playerStartX = (PANEL_WIDTH - 64) / 2;
        int playerStartY = 384 - 80;
        player = new Player(playerStartX, playerStartY);

        roadmapImgs = new Image[levelNames.length];
        levelBackgrounds = new Image[levelNames.length];

        String baseRoadMapPath = "assets/levels/";
        String baseBgPath = "assets/";

        for (int i = 0; i < levelNames.length; i++) {
            String rmPath = baseRoadMapPath + levelNames[i] + ".png";
            roadmapImgs[i] = new ImageIcon(rmPath).getImage();

            String bgPath = baseBgPath + levelNames[i] + ".png";
            levelBackgrounds[i] = new ImageIcon(bgPath).getImage();
        }

        startGameLoop();

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
    }

    private void startGameLoop() {
        running = true;
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    actionPerformed(null);
                }
            }
        });
        gameThread.start();
    }

    private void spawnBalloonsForLevel() {
        bubbles.clear();
        bullets.clear();

        levelScore = 0;
        levelFrameTimer = 90 * 40; // 3600 saniye

        double easyVelMul = 1.7;
        double mediumVelMul = 2;
        double hardVelMul = 2.3;

        int y = 50;
        int x = 250;

        switch (currentLevelIndex) {
            case 0:
                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "large", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "small", mediumVelMul));
                } else {
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 250, y, "veryLarge", hardVelMul));
                }
                break;
            case 1:

                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "veryLarge", easyVelMul));
                    bubbles.add(new Bubble(x + 200, y, "medium", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", mediumVelMul));
                } else { // HARD
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", hardVelMul));
                    bubbles.add(new Bubble(x + 400, y, "large", hardVelMul));
                }
                break;
            case 2:

                easyVelMul = 1.8;
                mediumVelMul = 2.1;
                hardVelMul = 2.4;

                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "veryLarge", easyVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", easyVelMul));
                    bubbles.add(new Bubble(x + 400, y, "small", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", mediumVelMul));
                    bubbles.add(new Bubble(x + 400, y, "medium", mediumVelMul));
                } else { // HARD
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 400, y, "medium", hardVelMul));
                }
                break;
            case 3:

                easyVelMul = 1.9;
                mediumVelMul = 2.2;
                hardVelMul = 2.5;

                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "veryLarge", easyVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", easyVelMul));
                    bubbles.add(new Bubble(x + 400, y, "small", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", mediumVelMul));
                    bubbles.add(new Bubble(x + 400, y, "medium", mediumVelMul));
                } else { // HARD
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", hardVelMul));
                    bubbles.add(new Bubble(x + 400, y, "large", hardVelMul));
                    bubbles.add(new Bubble(x + 600, y, "medium", hardVelMul));
                }
                break;
            case 4:

                easyVelMul = 2;
                mediumVelMul = 2.3;
                hardVelMul = 2.4;

                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "veryLarge", easyVelMul));
                    bubbles.add(new Bubble(x + 200, y, "large", easyVelMul));
                    bubbles.add(new Bubble(x + 400, y, "medium", easyVelMul));
                    bubbles.add(new Bubble(x + 600, y, "small", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 400, y, "medium", mediumVelMul));
                    bubbles.add(new Bubble(x + 600, y, "small", mediumVelMul));
                } else { // HARD
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 400, y, "large", hardVelMul));
                    bubbles.add(new Bubble(x + 600, y, "large", hardVelMul));
                }
                break;
            case 5:

                easyVelMul = 2.1;
                mediumVelMul = 2.4;
                hardVelMul = 2.6;

                if (difficulty == Difficulty.EASY) {
                    bubbles.add(new Bubble(x, y, "veryLarge", easyVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", easyVelMul));
                } else if (difficulty == Difficulty.MEDIUM) {
                    bubbles.add(new Bubble(x, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", mediumVelMul));
                    bubbles.add(new Bubble(x + 400, y, "large", mediumVelMul));
                } else { // HARD
                    bubbles.add(new Bubble(x, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 200, y, "veryLarge", hardVelMul));
                    bubbles.add(new Bubble(x + 400, y, "large", hardVelMul));
                    bubbles.add(new Bubble(x + 600, y, "medium", hardVelMul));
                    bubbles.add(new Bubble(x + 800, y, "small", hardVelMul));
                }
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (levelFrameTimer > 0) {
            levelFrameTimer--;
        }

        if (levelFrameTimer <= 0) {

            gameOver = true;
            finishCountdown = FINISH_COUNTDOWN;
            showingEndDialog = false;
            running = false;
            stopBackgroundMusic();
            repaint();
            return;
        }

        if (currentState == GameState.BEFORE_LEVEL) {

            if (waitingForEnter) {
                repaint();
                return;
            }

            if (countdownTimer == COUNTDOWN_START) {
                refillLivesForNewLevel();
            }

            countdownTimer--;
            if (countdownTimer <= 0) {
                currentState = GameState.PLAYING;

                spawnBalloonsForLevel();
            }

            repaint();
            return;
        }

        if (gameOver) {
            finishCountdown--;
            if (finishCountdown <= 0) {
                if (!showingEndDialog) {
                    showingEndDialog = true;
                    HistoryManager.appendToHistory(loggedInUser, currentLevelIndex, score);
                    SwingUtilities.invokeLater(this::showEndGameDialog);
                }
                running = false;
            }

            repaint();
            return;
        }

        if (invincibleTimer > 0) {
            invincibleTimer--;
        }

        for (Bubble b : bubbles) {
            b.move();
        }

        for (Bubble bubble : bubbles) {
            if (bubble.getBounds().intersects(player.getBounds())) {
                if (invincibleTimer == 0) {
                    lives--;
                    invincibleTimer = INVINCIBLE_DURATION;
                    if (lives <= 0) {
                        gameOver = true;
                        finishCountdown = FINISH_COUNTDOWN;
                    }
                }

                int groundY = 304;
                int newY = groundY - bubble.getHeight();
                bubble.setY(newY);

                double currentDy = bubble.getDy();
                bubble.setDy(-Math.abs(currentDy > 0 ? currentDy : bubble.getDy()));

                bubble.setDx(-bubble.getDx());
                break;
            }
        }

        int points = 0;

        List<Bubble> bubblesToRemove = new ArrayList<>();
        List<Bubble> bubblesToAdd = new ArrayList<>();
        List<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.isOffScreen()) {
                bulletsToRemove.add(bullet);
            }
        }

        for (Bullet bullet : bullets) {
            for (Bubble bubble : bubbles) {
                if (bullet.getBounds().intersects(bubble.getBounds())) {
                    switch (bubble.getType()) {
                        case "veryLarge":
                            points += 50;
                            break;
                        case "large":
                            points += 100;
                            break;
                        case "medium":
                            points += 150;
                            break;
                        case "small":
                            points += 200;
                            break;
                    }
                    levelScore += points;
                    score += points;

                    bubblesToRemove.add(bubble);
                    bulletsToRemove.add(bullet);

                    List<Bubble> newPieces = bubble.split();
                    bubblesToAdd.addAll(newPieces);
                    popSound.setFramePosition(3);
                    popSound.start();
                    break;
                }
            }
        }

        if (!bubblesToRemove.isEmpty()) {
            bubbles.removeAll(bubblesToRemove);
            bubblesToRemove.clear();
        }
        if (!bulletsToRemove.isEmpty()) {
            bullets.removeAll(bulletsToRemove);
            bulletsToRemove.clear();
        }
        if (!bubblesToAdd.isEmpty()) {
            bubbles.addAll(bubblesToAdd);
            bubblesToAdd.clear();
        }

        if (bubbles.isEmpty() && !gameOver) {
            HistoryManager.appendToHistory(loggedInUser, currentLevelIndex, score);

            currentLevelIndex++;
            if (currentLevelIndex < 6) {
                countdownTimer = COUNTDOWN_START;
                waitingForEnter = true;
                currentState = GameState.BEFORE_LEVEL;
            } else {
                gameOver = true;
                finishCountdown = FINISH_COUNTDOWN;
            }
        }

        player.move();
        repaint();
    }

    private void showEndGameDialog() {
        boolean allLevelsCompleted = (currentLevelIndex >= levelNames.length);

        if (allLevelsCompleted) {
            Object[] options = { "Stay, I wanna play more", "Nahh, quit (¬_¬)" };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Congratulations! You finished all the journey!\n"
                            + "Do you wanna stay or leave?",
                    "You won!",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (choice == 0) {
                resetEntireGame();
            } else {
                System.exit(0);
            }
        } else {
            Object[] options = { "Replay this level", "Restart game" };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Aren't you talented enough?\n"
                            + "Don't Worry, everyone deserves second chance!\n"
                            + "(note: when you replay level, you have -2000 score debuff)",
                    "It seems you lose",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                resetCurrentLevel();
            } else {
                resetEntireGame();
            }
        }
    }

    private void resetCurrentLevel() {
        score -= levelScore;
        score -= 2000;
        levelScore = 0;

        switch (difficulty) {
            case EASY:
                lives = 5;
                break;
            case MEDIUM:
                lives = 3;
                break;
            case HARD:
                lives = 2;
                break;
        }

        gameOver = false;
        showingEndDialog = false;
        waitingForEnter = true;
        currentState = GameState.BEFORE_LEVEL;
        countdownTimer = COUNTDOWN_START;

        int playerStartX = (PANEL_WIDTH - 64) / 2;
        int playerStartY = 384 - 80;
        player.setX(playerStartX);
        player.setY(playerStartY);

        bubbles.clear();
        bullets.clear();

        startGameLoop();
    }

    private void resetEntireGame() {
        if (currentLevelIndex < 6) {
            score = 0;
        }
        currentLevelIndex = 0;
        levelScore = 0;

        switch (difficulty) {
            case EASY:
                lives = 5;
                break;
            case MEDIUM:
                lives = 3;
                break;
            case HARD:
                lives = 2;
                break;
        }

        gameOver = false;
        showingEndDialog = false;
        waitingForEnter = true;
        currentState = GameState.BEFORE_LEVEL;
        countdownTimer = COUNTDOWN_START;

        int playerStartX = (PANEL_WIDTH - 64) / 2;
        int playerStartY = 384 - 80;
        player.setX(playerStartX);
        player.setY(playerStartY);

        bubbles.clear();
        bullets.clear();

        startGameLoop();
    }

    private void refillLivesForNewLevel() {
        switch (difficulty) {
            case EASY:
                while (lives < 5) {
                    lives++;
                }
                break;
            case MEDIUM:
                while (lives < 4) {
                    lives++;
                }
                break;
            case HARD:
                while (lives < 3) {
                    lives++;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentState == GameState.BEFORE_LEVEL) {
            Image bgImage = levelBackgrounds[currentLevelIndex];
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
            } else {
                g.setColor(Color.CYAN);
                g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            }

            Image roadmap = roadmapImgs[currentLevelIndex];
            if (roadmap != null) {
                g.drawImage(roadmap, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospace", Font.BOLD, 30));
            String title = "Level " + (currentLevelIndex + 1) + ": " + levelNames2[currentLevelIndex];
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(title);
            int textX = (getWidth() - textWidth) / 2;
            int textY = 75;
            g.drawString(title, textX, textY);

            if (waitingForEnter) {
                g.setFont(new Font("Monospace", Font.BOLD, 40));
                String prompt = "Press ENTER to Play";
                FontMetrics fm3 = g.getFontMetrics();
                int promptWidth = fm3.stringWidth(prompt);
                int promptX = (getWidth() - promptWidth) / 2;
                int promptY = 450;
                g.drawString(prompt, promptX, promptY);

                g.setFont(new Font("Monospace", Font.BOLD, 10));
                String prompt1 = "turn on/off music: M --- "
                        + "turn on/off pop sound: P";
                FontMetrics fm31 = g.getFontMetrics();
                int promptWidth1 = fm31.stringWidth(prompt1);
                int promptX1 = (getWidth() - promptWidth1) / 2;
                int promptY1 = 500;
                g.drawString(prompt1, promptX1, promptY1);

                return;
            }

            int secondsLeft = (countdownTimer / 60) + 1;
            String countText = "" + secondsLeft;
            g.setFont(new Font("Monospace", Font.ROMAN_BASELINE, 50));
            FontMetrics fm2 = g.getFontMetrics();
            int countWidth = fm2.stringWidth(countText);
            int countX = (getWidth() - countWidth) / 2;
            int countY = getHeight() / 2;
            g.drawString(countText, countX, countY);

            return;
        }

        ImageIcon icon = new ImageIcon("assets/levels/gameOverWin.png");
        Image gameOverImage = icon.getImage();

        Image bgImage = null;
        if (currentLevelIndex < 6)
            bgImage = levelBackgrounds[currentLevelIndex];
        else {
            bgImage = gameOverImage;
        }

        Font pixelLike = new Font("Monospaced", Font.ROMAN_BASELINE, 40);
        g.setFont(pixelLike);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }

        if (currentLevelIndex < 6) {
            if (invincibleTimer > 0) {
                g.drawImage(invisableImage, player.getX(), player.getY(), player.getWidth(), player.getHeight(), this);
            } else {
                g.drawImage(player.getImage(), player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                        this);
            }

            for (Bubble b : bubbles) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), this);
            }

            for (Bullet bullet : bullets) {
                if (0 < bullet.getY()) {
                    g.drawImage(bullet.getImage(), bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(),
                            this);
                }
            }

            for (int i = 0; i < lives; i++) {
                if (lives > 3) {
                    g.drawImage(livesImage, 508 + i * 34, 420, 34, 34, this);
                } else {
                    g.drawImage(livesImage, 508 + i * 56, 410, 56, 56, this);
                }

            }
            g.setColor(Color.WHITE);
            g.drawString("" + (currentLevelIndex + 1), 200, 435);
            g.drawString("" + score, 200, 485);

            int remainingFrames = Math.max(levelFrameTimer, 0);
            int remainingSeconds = remainingFrames / 40; 

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospace", Font.BOLD, 24));
            g.drawString("Time: " + remainingSeconds, 630, 50);

            g.setColor(Color.WHITE);

            if (currentState == GameState.PLAYING) {
                Font font = new Font("Monospaced", Font.BOLD, 15);
                g.setFont(font);
                if (difficulty == Difficulty.EASY) {
                    g.drawString("EASY", 573, 400);
                } else if (difficulty == Difficulty.MEDIUM) {
                    g.drawString("MEDIUM", 565, 400);
                } else if (difficulty == Difficulty.HARD) {
                    g.drawString("HARD", 573, 400);
                }
            }

        }

        ImageIcon loseIcon = new ImageIcon("assets/levels/gameOverLose.png");
        Image loseImage = loseIcon.getImage();

        if (gameOver && currentLevelIndex < 6) {
            if (finishCountdown <= 0)
                g.drawImage(loseImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (currentState == GameState.BEFORE_LEVEL && event.getKeyCode() == KeyEvent.VK_ENTER) {
            waitingForEnter = false;
            return;
        }

        if (!gameOver && currentState == GameState.PLAYING) {
            int code = event.getKeyCode();

            player.keyPressed(code);

            if (code == KeyEvent.VK_RIGHT) {
                player.setState(Player.State.RUN_RIGHT);
            }

            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                player.shoot();
                if (bullets.size() < MAX_BULLETS) {
                    int bulletX = player.getX() + player.getWidth() / 2 - 2;
                    int bulletY = player.getY();
                    bullets.add(new Bullet(bulletX, bulletY));
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent event) {

        int code = event.getKeyCode();

        if (event.getKeyCode() == KeyEvent.VK_M) {
            if (bgMusic.isActive()) {
                stopBackgroundMusic();
            } else {
                playBackgroundMusic();
                bgMusic.start();
            }
        }

        if (event.getKeyCode() == KeyEvent.VK_P) {
            if (popSound.isRunning()) {
                stopPopSound();
            } else {
                loadPopSound();
                popSound.start();
            }
        }

        player.keyReleased(code);

        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            player.setState(Player.State.DEFAULT);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void playBackgroundMusic() {
        try {
            File musicPath = new File("assets/music.wav");
            if (!musicPath.exists()) {
                System.err.println("Müzik dosyası bulunamadı: " + musicPath.getAbsolutePath());
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicPath);
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioIn);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    private void loadPopSound() {
        try {
            File popPath = new File("assets/pop.wav");
            if (!popPath.exists()) {
                System.err.println("Pop efekti dosyası bulunamadı: " + popPath.getAbsolutePath());
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(popPath);
            popSound = AudioSystem.getClip();
            popSound.open(ais);
        } catch (UnsupportedAudioFileException uafe) {
            System.err.println("Pop efekti formatı desteklenmiyor: " + uafe.getMessage());
        } catch (IOException ioe) {
            System.err.println("Pop efekti dosyası okunamadı: " + ioe.getMessage());
        } catch (LineUnavailableException lue) {
            System.err.println("Pop efekti ses hattı kullanılamıyor: " + lue.getMessage());
        }
    }

    private void stopPopSound() {
        if (popSound != null && popSound.isRunning()) {
            popSound.stop();
            popSound.close();
        }
    }

}
