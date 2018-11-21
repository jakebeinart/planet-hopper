package com.testinglibgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;

public class Planet {
    private Sprite planetImage = null;
    private Sprite gravityImage = null;
    private Circle planetInfo = null;
    private Circle gravityInfo = null;

    Planet(int x, int y, int radius, int gravityRadius){
        //Set up planet and info
        planetInfo = new Circle((float)x, (float)y, (float)radius);
        planetImage = new Sprite(new Texture("planet1.png"));
        planetImage.setSize(radius*2, radius*2);
        planetImage.setOrigin(radius, radius);
        planetImage.setPosition(x-radius, y-radius);

        //Set up gravity and info
        gravityInfo = new Circle((float)x, (float)y, (float)gravityRadius);
        gravityImage = new Sprite(new Texture("gravity.png"));
        gravityImage.setSize(gravityRadius*2,gravityRadius*2);
        gravityImage.setOrigin(gravityRadius, gravityRadius);
        gravityImage.setPosition(x-gravityRadius, y-gravityRadius);
    }

    public void setX(int x){
        planetImage.setPosition(x-planetInfo.radius, planetInfo.y-planetInfo.radius);
        planetInfo.x = x-planetInfo.radius;
        gravityImage.setPosition(x-gravityInfo.radius, gravityInfo.y-gravityInfo.radius);
        gravityInfo.x = x-gravityInfo.radius;
    }

    public void setY(int y){
        planetImage.setPosition(planetInfo.x-planetInfo.radius, y-planetInfo.radius);
        planetInfo.y = y-planetInfo.radius;
        gravityImage.setPosition(gravityInfo.x-gravityInfo.radius, y-gravityInfo.radius);
        gravityInfo.y = y-gravityInfo.radius;
    }


    public Sprite getPlanetImage() {
        return planetImage;
    }

    public Sprite getGravityImage() {
        return gravityImage;
    }

    public Circle getPlanetInfo() {
        return planetInfo;
    }

    public Circle getGravityInfo() {
        return gravityInfo;
    }

}
