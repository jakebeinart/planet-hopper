package com.testinglibgdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.awt.Polygon;

public class Pilot {
    Texture pilotImage = new Texture("pilot.png");
    Polygon hitbox = null;
    int angle;
    int centerX;
    int centerY;
    Vector2 trajectory = null;

    Pilot(int x, int y, int angle){
        this.centerX = x;
        this.centerY = y;
        this.angle = angle;
    }

}
