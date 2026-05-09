import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Board extends JPanel {
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 16;
    private Timer timer;
    private Timer bgTimer;
    private boolean isTitleScreen = true;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLineRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Shape.Tetrominoe[] board;
    private GameState state = GameState.TITLE;
    // idk 
    private int fallDelay = 18; 
    private int fallTick = 0; 
    private boolean isMenuSelecting = false;  
    private long menuSelectTime = 0;   
    private int highScore = 0;
    private int recentScore = 0;

    //clear line anime idk
    private boolean isLol = false;
    private int lolLine = -1;
    private long lolTime = 0;
    
    // test music lol
    private SoundManager soundManager = new SoundManager();

    // gave over thing 
    private boolean isGameOverAnimated = false;
    private boolean isGameOverSelecting = false;
    private long gameoverSelectTime = 0;
    private float goX = 400;
    private float goY = 150;
    private int animaStep = 0;
    private long animaTimer = 0;
    private float riX = 300;
    private float leX = -300;
    private float r2X = 300;
    private float l2X = -300;
    private float uY = 600;
    private boolean imtired = false;

    // fancy ui mode lmao
    private int goMenuI = 0;
    ImageIcon bgGif, gameoverGif, border;   
    Image bg2, pressStart, sc, high, recent, menu, restart;        
    ImageIcon logoGif, sr, rc;
    float bg2X = 0f;
    int menuI = 0;
    int menuX = -150;    
    int targetM = 40;  
    boolean menuSliding = true;
    ImageIcon[] sprites = new ImageIcon[8];
    ImageIcon[] selectors = new ImageIcon[5];
    BufferedImage sheet, lsheet;
    BufferedImage[] digits = new BufferedImage[10];
    BufferedImage[] font = new BufferedImage[26];
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int digitW = 8;
    int digitH = 15;
    int charW = 8;
    int charH = 15;
    boolean isStarting = false;
    long startTime = 0;
    
    private static final String IMG_PATH = "Assets/";
    
    private enum GameState {
        TITLE,
        MENU,
        RECORDS,
        PLAYING,
        GAME_OVER
    }

    public Board(Tetris parent) {
        initBoard(parent);
    }

    private void initBoard(Tetris parent) {
        setFocusable(true);
        loadScores();
        statusbar = parent.getStatusBar();
        addKeyListener(new TAdapter());

        bgGif = new ImageIcon(IMG_PATH + "Background 1.gif");
        bg2 = new ImageIcon(IMG_PATH + "Background 2.png").getImage();
        sc = new ImageIcon(IMG_PATH + "Score.png").getImage();
        pressStart = new ImageIcon(IMG_PATH + "Custom 1.png").getImage();
        logoGif = new ImageIcon(IMG_PATH + "Logo.gif");
        gameoverGif = new ImageIcon(IMG_PATH + "Game Over.gif");
        high = new ImageIcon(IMG_PATH + "Highest.png").getImage();
        recent = new ImageIcon(IMG_PATH + "Recent.png").getImage();
        border = new ImageIcon(IMG_PATH + "Border.gif");
        selectors[0] = new ImageIcon(IMG_PATH + "Start.gif");
        selectors[1] = new ImageIcon(IMG_PATH + "Records.gif");
        selectors[2] = new ImageIcon(IMG_PATH + "Menu.gif");
        selectors[3] = new ImageIcon(IMG_PATH + "Restart.gif");
        
        try {
            sprites[1] = new ImageIcon(IMG_PATH + "Cya.gif");
            sprites[2] = new ImageIcon(IMG_PATH + "Blu.gif");
            sprites[3] = new ImageIcon(IMG_PATH + "Ora.gif");
            sprites[4] = new ImageIcon(IMG_PATH + "Yel.gif");
            sprites[5] = new ImageIcon(IMG_PATH + "Gre.gif");
            sprites[6] = new ImageIcon(IMG_PATH + "Pur.gif");
            sprites[7] = new ImageIcon(IMG_PATH + "Red.gif");
        } catch (Exception e) {}
        
        try {
            sheet = ImageIO.read(new File(IMG_PATH + "Number.png"));
        } catch (Exception e) {}

        try {
            lsheet = ImageIO.read(new File(IMG_PATH + "Font.png"));
        } catch (Exception e) {}
        
        for (int i = 0; i < 10; i++) {
            digits[i] = sheet.getSubimage(i * digitW, 0, digitW, digitH);
        }
        for (int j = 0; j < 26; j++) {
            font[j] = lsheet.getSubimage(j * charW, 0, charW, charH);
        }
        
        bgTimer = new Timer(16, e -> {
            bg2X -= 0.1f;

            if (bg2X <= -bg2.getWidth(null)) {
                bg2X += bg2.getWidth(null);
            }

            if (System.currentTimeMillis() - lolTime > 150) {
                isLol = false;
                lolLine = -1;
            }

            if (menuSliding) {
                int remaining = targetM - menuX;
                if (remaining <= 1) {
                    menuX = targetM;
                    menuSliding = false;
                } else {
                    menuX += Math.max(1, remaining / 5);
                    if (menuX >= targetM) {
                        menuX = targetM;
                        menuSliding = false;
                    }
                }
            }
            // i love wasting my time on this
            if (isGameOverAnimated) {
                if (animaStep == 0) {
                    goX -= 10f;
                    if (goX <= 0) {
                        goX = 0;
                        animaStep = 1;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 1) {
                    if (System.currentTimeMillis() - animaTimer > 500) {
                        animaStep = 2;
                    }
                } else if (animaStep == 2) {
                    goY -= 2f;
                    if (goY <= 100) {
                        goY = 100;
                        animaStep = 3;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 3) {
                    if (System.currentTimeMillis() - animaTimer > 100) {
                        animaStep = 4;
                    }
                } else if (animaStep == 4) {
                    leX += 20f;
                    if (leX >= 30) {
                        leX = 30;
                        animaStep = 5;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 5) {
                    if (System.currentTimeMillis() - animaTimer > 180) {
                        animaStep = 6;
                    }
                } else if (animaStep == 6) {
                    riX -= 20f;
                    if (riX <= 120) {
                        riX = 120;
                        animaStep = 7;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 7) {
                    if (System.currentTimeMillis() - animaTimer > 180) {
                        animaStep = 8;
                    }
                } else if (animaStep == 8) {
                    l2X += 20f;
                    if (l2X >= 30) {
                        l2X = 30;
                        animaStep = 9;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 9) {
                    if (System.currentTimeMillis() - animaTimer > 180) {
                        animaStep = 10;
                    }
                } else if (animaStep == 10) {
                    r2X -= 20f;
                    if (r2X <= 120) {
                        r2X = 120;
                        animaStep = 11;
                        animaTimer = System.currentTimeMillis();
                    }
                } else if (animaStep == 11) {
                    if (System.currentTimeMillis() - animaTimer > 400) {
                        animaStep = 12;
                    }
                } else if (animaStep == 12) {
                    uY -= 10f;
                    if (uY <= 300) { 
                        uY = 300;
                        isGameOverAnimated = false;
                    }
                }
            }
        });
        bgTimer.start();
    }

    private void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("HighScore"))) {
            highScore = ois.readInt();
        } catch (Exception e) {
            highScore = 0;
        }
        try (ObjectInputStream bob = new ObjectInputStream(new FileInputStream("RecentScore"))) {
            recentScore = bob.readInt();
        } catch (Exception e) {
            recentScore = 0;
        }
    }

    private void GameOverAnime() {
        if (state != GameState.PLAYING) return;
        isGameOverAnimated = true;
        goX = 300;
        goY = 150;
        leX = -300;
        riX = 300;
        l2X = -300;
        r2X = 300;
        uY = 600;
        animaStep = 0;
        soundManager.playGameOverMusic();
    }
    
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P) {
                pause();
                return;
            }
            if (isPaused) return;

            if (state == GameState.GAME_OVER) {
                if (isGameOverAnimated) return;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    goMenuI = (goMenuI + 1) % 2;
                    soundManager.playBsound();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    goMenuI = (goMenuI - 1 + 2) % 2;
                    soundManager.playBsound();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !isGameOverSelecting) {
                    isGameOverSelecting = true;
                    soundManager.playMenuSelect();
                    gameoverSelectTime = System.currentTimeMillis();
                }
                repaint();
                return;
            }
            if (state == GameState.RECORDS) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    state = GameState.MENU;
                    repaint();
                }
                return;
            }
            if (isTitleScreen) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !isStarting) {
                    isStarting = true;
                    soundManager.playMenuSelect();
                    startTime = System.currentTimeMillis();
                }
                return;
            }
            if (state == GameState.MENU) {
                if (menuSliding) return;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    menuI = (menuI + 1) % 2;
                    soundManager.playBsound();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    menuI = (menuI - 1 + 2) % 2;
                    soundManager.playBsound();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !isMenuSelecting) {
                    isMenuSelecting = true;
                    soundManager.playMenuSelect();
                    menuSelectTime = System.currentTimeMillis();
                }
                repaint();
                return;
            }
            if (curPiece.getShape() == Shape.Tetrominoe.NoShape) return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0 && tryMove(curPiece, curX, newY - 1)) newY--;
        pieceDropped();
    }

    private void pause() {
        isPaused = !isPaused;
        statusbar.setText(isPaused ? "paused" : "Score: " + numLineRemoved + "  Best: " + highScore);
        repaint();
    }

    private final double SCALE = 0.9;

    private int squareWidth() {
        return (int)((200 / BOARD_WIDTH) * SCALE);
    }

    private int squareHeight() {
        return (int)((400 / BOARD_HEIGHT) * SCALE);
    }

    private Shape.Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_WIDTH * BOARD_HEIGHT; i++) {
            board[i] = Shape.Tetrominoe.NoShape;
        }
    }

    private void newPiece() {
        curPiece = new Shape();
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            if (state == GameState.PLAYING) {
                curPiece.setShape(Shape.Tetrominoe.NoShape);
                timer.stop();
                
                if (numLineRemoved > highScore) {
                highScore = numLineRemoved;
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("HighScore"))) {
                    oos.writeInt(highScore);
                } catch (Exception e) {}
            }
            try (ObjectOutputStream ios = new ObjectOutputStream(new FileOutputStream("RecentScore"))) {
                ios.writeInt(numLineRemoved);
                recentScore = numLineRemoved;
            } catch (Exception e) {}

            }
            GameOverAnime();
            state = GameState.GAME_OVER;
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.getX(i);
            int y = newY - newPiece.getY(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
                return false;
            if (shapeAt(x, y) != Shape.Tetrominoe.NoShape)
                return false;
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.getX(i);
            int y = curY - curPiece.getY(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        removeFullLines();
        if (!isFallingFinished) newPiece();
    }

    private void removeFullLines() {
        int numFullLines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Shape.Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                numFullLines++;
                lolLine = i;
                isLol = true;
                lolTime = System.currentTimeMillis();
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
                i++;
            }
        }
        if (numFullLines > 0) {
            numLineRemoved += numFullLines * 100;
            if (numLineRemoved > highScore) {
                highScore = numLineRemoved;
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("HighScore"))) {
                    oos.writeInt(highScore);
                } catch (Exception e) {}
            }
            try (ObjectOutputStream ios = new ObjectOutputStream(new FileOutputStream("RecentScore"))) {
                ios.writeInt(numLineRemoved);
                recentScore = numLineRemoved;
            } catch (Exception e) {}
            statusbar.setText("Score: " + numLineRemoved + "  Best: " + highScore);
            isFallingFinished = true;
            curPiece.setShape(Shape.Tetrominoe.NoShape);
        }
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) pieceDropped();
    }

    private class GameCycle implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

    private void update() {
        if (state != GameState.PLAYING) return;
        if (isPaused) return;
        fallTick++;
        if (fallTick >= fallDelay) {
            if (isFallingFinished) {
                isFallingFinished = false;
                newPiece();
            } else {
                oneLineDown();
            }
            fallTick = 0;
        }
    }
    
    void drawNum(Graphics g, int number, int x, int y) {
        String son = String.valueOf(number);
        for (int i = 0; i < son.length(); i++) {
            int dig = son.charAt(i) - '0';
            g.drawImage(digits[dig], x + i * (digitW + 2), y, null);
        }
    }
    
    void drawText(Graphics g, String text, int x, int y) {
        text = text.toUpperCase();
        int offsetX = x;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                offsetX += charW; 
                continue;
            }
            int index = c - 'A';
            if (index >= 0 && index < 26) {
                g.drawImage(font[index], offsetX, y, null);
                offsetX += charW; 
            }
        }
    }

    void start() {
        if (timer != null) timer.stop();
        soundManager.playGamePlayingMusic();
        loadScores();
        state = GameState.PLAYING;
        fallTick = 0;
        numLineRemoved = 0;
        statusbar.setText("Score: 0  Best: " + highScore);
        curPiece = new Shape();
        board = new Shape.Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        newPiece();
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(2, 2);
        doDrawing(g2);
    }

    private void doDrawing(Graphics g) {
        bgGif.paintIcon(this, g, 0, 0);
        g.drawImage(bg2, (int)bg2X, 0, null);
        g.drawImage(bg2, (int)bg2X + bg2.getWidth(null), 0, null);

        if (isStarting) {
            if (System.currentTimeMillis() - startTime > 1000) {
                state = GameState.MENU;
                isStarting = false;
                menuX = -150;
                menuSliding = true;
                menuI = 0;
                isTitleScreen = false;
                if (timer != null) timer.stop();
                soundManager.playGameMusic();
            }
        }

        if (isTitleScreen) {
            if (!imtired) {
                soundManager.playTitleMusic();
                imtired = true;
            }
            int x = 29;
            int y = 90;
            long now = System.currentTimeMillis();
            int speed = isStarting ? 40 : 500;
            logoGif.paintIcon(this, g, x, y);
            if ((now / speed) % 2 == 0) {
                g.drawImage(pressStart, x + 28, y + 200, null);
            }
            return;
        }
        
        if (state == GameState.MENU) {
            int x = 29;
            int y = 90;
            logoGif.paintIcon(this, g, x, y);
            int menuY = y + 180;
            boolean byebye = false;
            if (isMenuSelecting) {
                long now = System.currentTimeMillis();
                if (now - menuSelectTime > 1000) {
                    if (menuI == 0) start();
                    else if (menuI == 1) state = GameState.RECORDS;
                    isMenuSelecting = false;
                } else if ((now / 45) % 2 == 0) {
                    byebye = true;
                }
            }
            if (menuI == 0 && byebye) {}
            else if (menuI == 0) selectors[0].paintIcon(this, g, menuX + 12, menuY);
            else drawText(g, "START", menuX + 40, menuY);
            if (menuI == 1 && byebye) {}
            else if (menuI == 1) selectors[1].paintIcon(this, g, menuX + 26, menuY + 30);
            else drawText(g, "RECORDS", menuX + 32, menuY + 30);
            return;
        }

        if (state == GameState.RECORDS) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(18, 140, 170, 89);
            border.paintIcon(this, g, 0, 130);
            g.drawImage(high, 30, 190, null);
            drawNum(g, highScore, 120, 195);
            g.drawImage(recent, 30, 160, null);
            drawNum(g, recentScore, 120, 160);
            drawText(g, "PRESS ESC TO RETURN", 23, 250);
            return;
        }

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, getWidth(), getHeight());

        int boardX = 9;
        int boardY = 0;
        int boardPxWidth = BOARD_WIDTH * squareWidth();
        int boardPxHeight = BOARD_HEIGHT * squareHeight();

        g.setColor(Color.WHITE);
        g.drawRect(boardX, boardY, boardPxWidth, boardPxHeight);
        g.drawImage(sc, boardX, boardY + boardPxHeight + 3, null);
        drawNum(g, numLineRemoved, boardX + 60, boardY + boardPxHeight + 3);

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Shape.Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Shape.Tetrominoe.NoShape) {
                    drawSquare(g, boardX + j * squareWidth(), boardY + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece != null && curPiece.getShape() != Shape.Tetrominoe.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.getX(i);
                int y = curY - curPiece.getY(i);
                drawSquare(g, boardX + x * squareWidth(), boardY + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
            }
        }

        if (isLol && lolLine >= 0) {
            int bx = 9;
            int yo = (BOARD_HEIGHT - lolLine - 1) * squareHeight();
            g.setColor(new Color(200, 200, 255, 100));
            g.fillRect(bx, yo, BOARD_WIDTH * squareWidth(), squareHeight());
        }

        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());
            drawText(g, "PAUSE", 80, 180);
        }

        if (state == GameState.GAME_OVER) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            gameoverGif.paintIcon(this, g, (int)goX, (int)goY);
            g.drawImage(sc, (int)leX, 160, null);
            drawNum(g, numLineRemoved, (int)riX, 160);
            g.drawImage(high, (int)l2X, 185, null);
            drawNum(g, highScore, (int)r2X, 190);

            long now = System.currentTimeMillis();
            boolean boo = false;
            if (isGameOverSelecting) {
                if (now - gameoverSelectTime > 1000) {
                    if (goMenuI == 0) {
                        state = GameState.MENU;
                        soundManager.playGameMusic();
                    }
                    else if (goMenuI == 1) start();
                    isGameOverSelecting = false;
                } else if ((now / 45) % 2 == 0) {
                    boo = true;
                }
            }
            if (goMenuI == 0 && boo) {}
            else if (goMenuI == 0) selectors[2].paintIcon(this, g, 22, (int)uY);
            else drawText(g, "MENU", 31, (int)uY);
            if (goMenuI == 1 && boo) {}
            else if (goMenuI == 1) selectors[3].paintIcon(this, g, 109, (int)uY);
            else drawText(g, "RESTART", 115, (int)uY);
            return;
        }
    }

    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoe shape) {
        int index = shape.ordinal();
        if (index >= 1 && index <= 7 && sprites[index] != null) {
            sprites[index].paintIcon(this, g, x, y);
        } else {
            Color[] colors = {
                new Color(0, 0, 0),
                new Color(0, 204, 204),
                new Color(102, 102, 204),
                new Color(204, 170, 102),
                new Color(204, 204, 102),
                new Color(102, 204, 102),
                new Color(204, 102, 204),
                new Color(204, 102, 102)
            };
            Color color = colors[index];
            g.setColor(color);
            g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
            g.setColor(color.brighter());
            g.drawLine(x, y + squareHeight() - 1, x, y);
            g.drawLine(x, y, x + squareWidth() - 1, y);
            g.setColor(color.darker());
            g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
            g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
        }
    }
}