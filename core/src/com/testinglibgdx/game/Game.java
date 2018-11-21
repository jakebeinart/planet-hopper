package com.testinglibgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
    ArrayList<Planet> planetList;
    Pilot pilot;
    boolean orbiting;
    int w = 0;
    int h = 0;
    int newPilotY = 100;


    @Override
	public void create () {
        //Get screen size
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();




		//PLANET GENERATION
        planetList = new ArrayList<Planet>();
        planetList.add(new Planet(w/2,200,60,130));

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
            planet.getPlanetImage().draw(batch);
        }

        //DRAW PILOT
        pilot.getPilotImage().draw(batch);
		batch.end();

		//DO LOGIC/MOVEMENT
        if(orbiting){
            pilot.setX((int)(pilot.getOrbitingPlanet().getGravityInfo().x + Math.cos(Math.toRadians(pilot.getAngle()))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setY((int)(pilot.getOrbitingPlanet().getGravityInfo().y + Math.sin(Math.toRadians(pilot.getAngle()))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setAngle((pilot.getAngle()+3)%360);
        }else{

        }

        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                orbiting = !orbiting;
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
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
