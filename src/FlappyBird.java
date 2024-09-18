import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image coinImg;
    Image aidenImg;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;


    //bird class
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;    //boolean only for the Pipe class

        Pipe(Image img) {
            this.img = img;
        }
    }

    //Coins
    int coinX = boardWidth / 2;
    int coinY = 0;
    int coinWidth = boardWidth / 4;
    int coinHeight = boardHeight / 5;

    class Coin {
        int x = coinX;
        int y = coinY;
        int width = coinWidth;
        int height = coinHeight;

        Image img;
        boolean passed = false;

        Coin(Image img) {
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = -0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    ArrayList<Coin> coins;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    Timer placeCoinTimer;
    boolean gameOver = false;
    double score = 0;
    int jumps = 0;
    int coinsCollect = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        coinImg = new ImageIcon(getClass().getResource("./coin.png")).getImage();
        aidenImg = new ImageIcon(getClass().getResource("./aiden.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        coins = new ArrayList<Coin>();
        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //place coin timer
        placeCoinTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCoin();
            }
        });
        placeCoinTimer.start();

        //gametimer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> pipeHeight/4 -> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;
        if (score >= 30) {
            openingSpace = boardHeight / 5;

        }
        if (score >= 50) {
            openingSpace = boardHeight / 7;
        }
        if (score >= 70) {
            openingSpace = boardHeight / 9;
        }
        if (score >= 90) {
            openingSpace = boardHeight / 10;
        }

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void placeCoin() {
        int randomCoinY = (int) (coinY - pipeHeight / 4);
        int placeCoin = boardHeight / 3;

        Coin coin = new Coin(coinImg);
        coin.y = randomCoinY;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, birdWidth, birdHeight, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //coins
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            g.drawImage(coin.img, coin.x, coin.y, coin.width, coin.height, null);
        }

        //score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {

            g.setFont(new Font("Arial", Font.PLAIN, 40));
            g.drawString("Game Over!", (int) (boardWidth / 5.5), 300);

            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);

        } else {
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);
        }

        //jumps
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver != false) {
            g.drawString("Total Jumps: " + String.valueOf((int) jumps), 7, 75);
        } else {
            g.drawString("Jumps: " + String.valueOf((int) jumps), 7, 75);
        }

        //Coins
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver != false) {
            g.drawString("Coins Collected: " + String.valueOf((int) coinsCollect), 7, 115);
        } else {
            g.drawString("Coins: " + String.valueOf((int) coinsCollect), 7, 115);
        }
    }


    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; //0.5 because there are two pipes, Top pipe and Bottome pipe, so combined the score increases by 1
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        //coins
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            coin.x += velocityX;

            if (!coin.passed && bird.x > coin.x + coin.width) {
                coin.passed = true;
                score += 1;
            }

            if (collision(bird, coin)) {
                coinsCollect++;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&    //a's top left corner doesn't reach b's top left corner
                a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    // a's bottom left corner passes b's top left corner
    }

    public boolean collision(Bird a, Coin c) {
        return a.x < c.x + c.width &&
                a.x + a.width > c.x &&
                a.y < c.y + c.height &&
                a.y + a.height > c.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            placeCoinTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            jumps++;
            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
                placeCoinTimer.start();
                jumps = 0;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_C) {     //hackz
            if (score < 1) {
                score = 0;
            } else {
                score += 9;
                jumps += 33;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}