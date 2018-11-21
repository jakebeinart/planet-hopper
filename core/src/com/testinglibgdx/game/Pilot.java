package com.testinglibgdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.awt.Polygon;

public class Pilot {
    private Sprite pilotImage = new Sprite(new Texture("pilot.png"));
    private Polygon hitbox = null;
    private int angle;
    private int centerX;
    private int centerY;
    private Vector2 trajectory = null;
    private int velocity;


    Planet orbitingPlanet;

    Pilot(Planet orbitingPlanet){
        this.centerX = (int)(orbitingPlanet.getGravityInfo().x + Math.cos(Math.toRadians(angle))*orbitingPlanet.getGravityInfo().radius);
        this.centerY = (int)(orbitingPlanet.getGravityInfo().y + Math.sin(Math.toRadians(angle))*orbitingPlanet.getGravityInfo().radius);
        pilotImage.setSize(50,50);
        pilotImage.setOrigin(25,25);
        pilotImage.setPosition(centerX-25, centerY-25);

        this.velocity = 8;

        this.orbitingPlanet = orbitingPlanet;
    }

    public Planet getOrbitingPlanet() {
        return orbitingPlanet;
    }

    public void setAngle(int angle) {
        pilotImage.setRotation((float)angle);
        this.angle = angle;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getAngle() {
        return angle;
    }

    public void setX(int x){
        pilotImage.setPosition(x-25, centerY);
        centerX = x-25;
    }

    public void setY(int y){
        pilotImage.setPosition(centerX, y-25);
        centerY = y-25;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRealX(){
        return centerX+25;
    }

    public int getRealY(){
        return centerY+25;
    }

    public Sprite getPilotImage() {
        return pilotImage;
    }
}
