/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BrickBreaker;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Bronj
 */
public class Entity {
    private int x, y, height, width;
    
    //CONSTRUCTOR
    public Entity(int height, int width){
        this.height = height;
        this.width = width;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    //moves object on screen
    public void move(int dx, int dy){
        x += dx;
        y += dy;
    }

    //gets boundaries of entity
    public Rectangle getBound(){
        return new Rectangle(x, y, width, height);
    }
    
    //Checks for collision of entities
    public boolean isCollision(Entity o){
        if (o == this) return false;
        return getBound().intersects(o.getBound());
    }
    
    //creates graphic
    public void render(Graphics2D g2d){
        g2d.fillRect(x + 1, y + 1, width, height);
    }
}
