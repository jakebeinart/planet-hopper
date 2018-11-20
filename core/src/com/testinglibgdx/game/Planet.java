package com.testinglibgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Planet {
    Texture planetImage = null;
    Texture gravityImage = new Texture(Gdx.files.internal("badlogic.png"));
    Circle planetInfo = null;
    Circle gravityInfo = null;

    Planet(int x, int y, int radius, int gravityRadius){
        planetInfo = new Circle((float)x, (float)y, (float)radius);
        gravityInfo = new Circle((float)x, (float)y, (float)gravityRadius);
        planetImage = new Texture("planet1.png");
    }

}
