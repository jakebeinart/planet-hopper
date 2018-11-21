package com.testinglibgdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static com.testinglibgdx.game.Constants.PILOT_SIZE;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
    ArrayList<Planet> planetList;
    Pilot pilot;
    boolean orbiting;
    int w = 0;
    int h = 0;

    float precisePilotX = 0;
    float precisePilotY = 0;


    @Override
	public void create () {
        //Get screen size
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();

        Gdx.app.setLogLevel(Application.LOG_DEBUG);


		//PLANET GENERATION
        planetList = new ArrayList<Planet>();
        planetList.add(new Planet(w/2,200,60,150));

        createPlanetAtY(1200);
        //Generate random initial planets


        //PILOT
        pilot = new Pilot(planetList.get(0));

        orbiting= true;
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		//DRAW THINGS
		batch.begin();
		//DRAW PLANETS
		for(Planet planet: planetList){
		    planet.getGravityImage().draw(batch);
            planet.getPlanetImage().draw(batch);
        }

        //DRAW PILOT
        pilot.getPilotImage().draw(batch);
		batch.end();

		//DO LOGIC/MOVEMENT
        if(orbiting){
            pilot.setX((int)(pilot.getOrbitingPlanet().getGravityInfo().x + Math.cos(Math.toRadians(pilot.getAngle()))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setY((int)(pilot.getOrbitingPlanet().getGravityInfo().y + Math.sin(Math.toRadians(pilot.getAngle()))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setAngle((pilot.getAngle()+6)%360);
            //MAKE THIS DEPEND ON LAUNCH VELOCITY - w = v/r (fomula in radians)
        }else{
            //Use floats for more precision in takeoff
            precisePilotX += pilot.getVelocity()*Math.cos(Math.toRadians(pilot.getAngle()+90));
            precisePilotY += pilot.getVelocity()*Math.sin(Math.toRadians(pilot.getAngle()+90));
            pilot.setX((int)(precisePilotX));
            pilot.setY((int)(precisePilotY));
        }

        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                orbiting = !orbiting;
                precisePilotX = pilot.getRealX();
                precisePilotY = pilot.getRealY();

                if (!orbiting){
                    isGoingToCollide();
                }

                return true;
            }

        });
	}

    /**
     * Given an integer y, will generate and create a random planet at that y.
     */
	public void createPlanetAtY(int y) {
        // Generate the random planet size
        int planetRadius = (int)(Math.random() * ((Constants.MAX_PLANET_SIZE - Constants.MIN_PLANET_SIZE) + 1)) + Constants.MIN_PLANET_SIZE;
        int minX = planetRadius + Constants.MIN_BUFFER;
        int maxX = w - planetRadius - Constants.MIN_BUFFER;
        int planetX = (int)(Math.random() * ((maxX - minX) + 1)) + minX;
        planetList.add(new Planet(planetX,y,planetRadius,planetRadius+40));
    }

    /*
     * Determine if pilot is on trajectory to hit planet
     */

    public boolean isGoingToCollide(){
        boolean collision = false;

        //DETERMINE COLLISION
        //First point starts at unit length of 105 because we don't want to accidentally hit the
        float generatedX = precisePilotX;// + (float)(100*Math.cos(Math.toRadians(pilot.getAngle()+90)));
        float generatedY = precisePilotY;// + (float)(100*Math.sin(Math.toRadians(pilot.getAngle()+90)));

        int unitLength = 5;

        ArrayList<GridPoint2> trajectoryVector = new ArrayList<GridPoint2>();

        while(generatedX >=0 && generatedX <= w && generatedY >= 0 && generatedY <= h){
            generatedX += unitLength * Math.cos(Math.toRadians(pilot.getAngle()+90));
            generatedY += unitLength * Math.sin(Math.toRadians(pilot.getAngle()+90));
            trajectoryVector.add(new GridPoint2((int)generatedX, (int)generatedY));
        }

        for(GridPoint2 point: trajectoryVector){
            if(collision){
                break;
            }
            for(Planet planet: planetList){
                GridPoint2 planetCenter = new GridPoint2((int)planet.getGravityInfo().x, (int)planet.getGravityInfo().y);
                if(planet != pilot.getOrbitingPlanet()) {
                    if (planetCenter.dst(point) <= (planet.getGravityInfo().radius + PILOT_SIZE / 2)) {
                        collision = true;
                        break;
                    }
                }
            }
        }

        Gdx.app.debug("COLLISION?", String.valueOf(collision));
        return collision;
    }

	@Override
	public void dispose () {
		batch.dispose();
	}
}
