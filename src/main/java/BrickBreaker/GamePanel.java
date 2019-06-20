/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrickBreaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Bronj
 */
    @SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    
    //Rendering variables
    private Graphics2D g2d;
    private BufferedImage image;
    
    //Game logistical Variables
    private Thread thread;
    private boolean running;
    private long targetTime;
    
    //Game Stuff
    private final int SIZE = 10;
    private int score;
    private int highscore;
    private int level;
    private boolean gameover;    
    
    //Objects on game stuff
    private Entity paddle, ball, block;
    private ArrayList<Entity> brick;
    private int numBricks;
    private int count;
    
    //Movement stuff 
    private int dx, dy, dx2, dy2;
    
    //Input from keyboard
    private boolean up, down, left, right, start;
    
    public GamePanel() {
        this.brick = new ArrayList<Entity>();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
    }   
    
    public void run(){
        if (running) return;
        init();
        
        long startTime;
        long elapsed;
        long wait;
        
        while (running){
            startTime = System.nanoTime();
            update();
            requestRender();
            
            elapsed = System.nanoTime() - startTime;
            wait = targetTime - elapsed / 1000000;
            
            if (wait > 0){
                try{
                    Thread.sleep(wait);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        //Only serves to stop GamePanel from being abstract. Must be overriden
    }

    @Override
    public void keyPressed(KeyEvent e){
        int k = e.getKeyCode();
        
        switch (k){
            case KeyEvent.VK_LEFT: 
                left = true;
                break;
            case KeyEvent.VK_RIGHT: 
                right = true;
                break;
            case KeyEvent.VK_ENTER: 
                start = true;
                break;
            default:
                System.out.println("Key Not Recognized");
            
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        int k = e.getKeyCode();
        
        switch (k){
            case KeyEvent.VK_LEFT: 
                left= false;
                break;
            case KeyEvent.VK_RIGHT: 
                right= false;
                break;
            case KeyEvent.VK_ENTER: 
                start = true;
                break;
            default:
                System.out.println("Key Not Recognized");
            
        }
    }
    
    //Initializes everything
    private void init(){
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();        
        
        running = true;
        count = 0;
        level = 0;
        setUpLevel();
    }

    //Sets pace of the game
    private void setFPS(int fps){
        targetTime = 1000 / fps;
    }
      
    //Gets everything in place to initialize start of game
    public void setUpLevel(){
        paddle = new Entity(20, 75);//height, width
        ball = new Entity(20, 20);//height, width
        
        //positioning ball and paddle
        paddle.setPosition(WIDTH/2, 350);
        ball.setPosition(WIDTH/2, HEIGHT/2);
        level++;
        
        //get width of box and fix accordingly
        scaleBricks(level);
        
        score = 0;
        gameover = false;
        dx = dy = 0;
        dx2 = dy2 = 0;
        setFPS(2 * 10);
    }
    
    private void scaleBricks(int level){
        numBricks = 6;
        
        for (int i = 0; i < numBricks; i++){
            block = new Entity(20, 97);
            brick.add(block);                  
        }
        
        setBricks();
    }
    
    //Updates to keep action on screen moving
    private void update(){
        if (gameover){
            if(start){
                setUpLevel();
            }
            return;
        }
        
        if (left){
            dy = 0;
            dx = -SIZE;          
            
            paddle.move(dx, dy);
        }
        if (right){
            dy = 0;
            dx = SIZE;
                    
            paddle.move(dx, dy);
        }        
                
        if (start){
            if (count > 0){
                if (ball.isCollision(paddle)){
                    ball.move(dx2, dy2--);
                    System.out.println("ouch paddle");
                }
                if (ball.isCollision(block)){
                    ball.move(dx2, dy2--);
                    System.out.println("ouch block");
                }
            }else{
                if (ball.isCollision(paddle)){
                    System.out.println("ouch paddle 1");
                    count++;
                }
                if (ball.isCollision(block)){
                    System.out.println("ouch block 1");
                   count++;
                }
               ball.move(dx2, dy2++); 
               System.out.println(count);
            }
            
        }
        
        if (paddle.getX() < 0) paddle.setX(WIDTH);        
        if (paddle.getX() > WIDTH) paddle.setX(0);
    }
    
    //Making Brick map
    public void setBricks(){
        int x = -100;
        int y = 0;
        
        x = x - (x % SIZE);
        y = y - (y % SIZE);
        
        //layer one
        for (int i = 0; i < numBricks; i++){
            x += 100;
            brick.get(i).setPosition(x, y);
        }
        //layer two
        
        //layer three
    }
    
    //prepares render
    private void requestRender(){
        render(g2d);
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }
    
    //Creates render of objects etc
    private void render(Graphics2D g2d){
        g2d.clearRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.BLUE);
        
        for (Entity e: brick){
            e.render(g2d);
        }
        
        g2d.setColor(Color.RED);
        ball.render(g2d);
        if (gameover){
            g2d.drawString("GAME OVER!", 160,200);
        }
        
        g2d.setColor(Color.WHITE);
        paddle.render(g2d);
        g2d.drawString("Score: "+ score + "   Level: "+ level, 10,20);
        g2d.drawString("Highscore: "+ highscore, 10, 40);
        
        if (dx == 0 && dy == 0){
            g2d.drawString("Ready!", 180,200);
        }
        if (gameover){
            g2d.drawString("Press ENTER to START!", 130,220);
        }
    }  
    
    //Starts game
    public void addNotify(){
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }
}
