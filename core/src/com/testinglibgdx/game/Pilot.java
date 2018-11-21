package com.testinglibgdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.awt.Polygon;

import static com.testinglibgdx.game.Constants.PILOT_SIZE;

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

        this.pilotImage.setSize(PILOT_SIZE,PILOT_SIZE);
        this.pilotImage.setOrigin(PILOT_SIZE/2,PILOT_SIZE/2);
        this.pilotImage.setRotation(90);
        this.angle = 90;
        this.pilotImage.setPosition(centerX-(PILOT_SIZE/2), centerY-(PILOT_SIZE/2));


        this.velocity = 14;

        this.orbitingPlanet = orbitingPlanet;
    }

    public Planet getOrbitingPlanet() {
        return orbitingPlanet;
    }

    public void setOrbitingPlanet(Planet orbitingPlanet) {
        this.orbitingPlanet = orbitingPlanet;
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
        pilotImage.setPosition(x-(PILOT_SIZE/2), centerY);
        centerX = x-(PILOT_SIZE/2);
    }

    public void setY(int y){
        pilotImage.setPosition(centerX, y-(PILOT_SIZE/2));
        centerY = y-(PILOT_SIZE/2);
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRealX(){
        return centerX+(PILOT_SIZE/2);
    }

    public int getRealY(){
        return centerY+(PILOT_SIZE/2);
    }

    public Sprite getPilotImage() {
        return pilotImage;
    }
}
