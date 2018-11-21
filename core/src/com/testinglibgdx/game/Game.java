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
    boolean needToAdjustCourse;
    Planet globalCollisionPlanet = null;
    boolean collidingClockwise = false;
    boolean orbitingClockwise = false;
    GridPoint2 orbitPoint;
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

        planetList.add(new Planet(500,1200,60,250));

        //createPlanetAtY(800);
        //Generate random initial planets


        //PILOT
        pilot = new Pilot(planetList.get(0));
        pilot.setAngle(90);

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
            pilot.setX((int)(pilot.getOrbitingPlanet().getGravityInfo().x + Math.cos(Math.toRadians(pilot.getAngle()-90))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setY((int)(pilot.getOrbitingPlanet().getGravityInfo().y + Math.sin(Math.toRadians(pilot.getAngle()-90))*pilot.getOrbitingPlanet().getGravityInfo().radius));
            pilot.setAngle((pilot.getAngle()+(int)Math.toDegrees(pilot.getVelocity()/pilot.getOrbitingPlanet().getGravityInfo().radius))%360);
            //MAKE THIS DEPEND ON LAUNCH VELOCITY - w = v/r (fomula in radians)

        }else{
            //Use floats for more precision in takeoff
            precisePilotX += pilot.getVelocity()*Math.cos(Math.toRadians(pilot.getAngle()));
            precisePilotY += pilot.getVelocity()*Math.sin(Math.toRadians(pilot.getAngle()));
            pilot.setX((int)(precisePilotX));
            pilot.setY((int)(precisePilotY));


            if(globalCollisionPlanet != null){

                changeCourse(new GridPoint2(orbitPoint));

                if(pilot.getCenterY() >= (globalCollisionPlanet.getPlanetInfo().y-(pilot.getVelocity()*2))){
                    pilot.setOrbitingPlanet(globalCollisionPlanet);
                    globalCollisionPlanet = null;
                    orbiting = true;
                }
            }
        }

        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                orbiting = !orbiting;
                precisePilotX = pilot.getRealX();
                precisePilotY = pilot.getRealY();

                if (!orbiting){
                     needToAdjustCourse = isGoingToCollide();
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
        int planetRadius = genRandom(Constants.MIN_PLANET_SIZE, Constants.MAX_PLANET_SIZE);
        int gravityDiff = genRandom(Constants.MIN_GRAVITY_DIFFERENCE, Constants.MAX_GRAVITY_DIFFERENCE);
        int gravityRadius = planetRadius + gravityDiff;
        int minX = gravityRadius + Constants.MIN_BUFFER;
        int maxX = w - gravityRadius - Constants.MIN_BUFFER;
        int planetX = genRandom(minX, maxX);
        planetList.add(new Planet(planetX,y,planetRadius,gravityRadius));
    }

    /**
     * Generates a random int between min(inclusive) and max(inclusive)
     * @param min
     * @param max
     * @return random number
     */
    public int genRandom(int min, int max) {
	    return (int)(Math.random() * ((max - min) + 1)) + min;
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
            generatedX += unitLength * Math.cos(Math.toRadians(pilot.getAngle()));
            generatedY += unitLength * Math.sin(Math.toRadians(pilot.getAngle()));
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
                        globalCollisionPlanet = planet;
                        break;
                    }
                }
            }
        }

        //If we're on track to hit a planet, determine which way we're going to spin
        if(collision){
            //y = y1 + ((y2 - y1)/(x2 - x1))*(x - x1)
            //y = planet y value and computed x value will be used to determine rotation

            float x2 = generatedX + (float)(unitLength * Math.cos(Math.toRadians(pilot.getAngle())));
            float y2 = generatedY + (float)(unitLength * Math.sin(Math.toRadians(pilot.getAngle())));

            float slope = (y2- generatedY)/(x2-generatedX);
            float yIntercept = y2 - (slope*x2);

            float trajX = (globalCollisionPlanet.getGravityInfo().y - yIntercept)/slope;

            //If trajX is NaN, slope is infinite because trajectory is perfectly vertical
            //use any of the Xs instead
            if(Float.isNaN(trajX)){
                trajX = generatedX;
            }

            if(trajX >= globalCollisionPlanet.getGravityInfo().x){

                collidingClockwise = false;
            }else{

                collidingClockwise = true;
            }
            Gdx.app.debug("banana x values?", String.valueOf(trajX) + " " + String.valueOf(globalCollisionPlanet.getGravityInfo().x));
            //Gdx.app.debug("banana slope?", String.valueOf(slope));
            Gdx.app.debug("banana ROTATION?", String.valueOf(collidingClockwise));
            //Gdx.app.debug("banana COLLISION?", String.valueOf(collision));


            //FIND POINT TO CHARTER TO
            float distance = new GridPoint2((int)globalCollisionPlanet.getPlanetInfo().x, (int)globalCollisionPlanet.getPlanetInfo().y).dst(pilot.getRealX(), pilot.getRealY());

            float desiredAngle = (float)Math.toDegrees(Math.acos((globalCollisionPlanet.getPlanetInfo().x-pilot.getRealX())/distance));

            float computedAngle;

            if(collidingClockwise){
                computedAngle = 90-desiredAngle;
                computedAngle = 180-computedAngle;
                computedAngle = 180;
            }else{
                computedAngle = desiredAngle-90;
                computedAngle = 0;
            }

            Gdx.app.debug("banana computedAngle", String.valueOf(computedAngle));

            int orbitX = (int)(globalCollisionPlanet.getGravityInfo().x + ((Math.cos(computedAngle))*globalCollisionPlanet.getGravityInfo().radius));
            int orbitY = (int)(globalCollisionPlanet.getGravityInfo().y + ((Math.sin(computedAngle))*globalCollisionPlanet.getGravityInfo().radius));
            orbitPoint = new GridPoint2(orbitX, orbitY);


        }




        return collision;
    }



    public void changeCourse(GridPoint2 point){
        float distance = point.dst(pilot.getRealX(), pilot.getRealY());

        float desiredAngle = (float)Math.toDegrees(Math.acos((point.x-pilot.getRealX())/distance));
        float y2 = precisePilotY +  (float)(5 * Math.sin(Math.toRadians(pilot.getAngle())));

        //Gdx.app.debug("banana desiredAngle", String.valueOf(desiredAngle) + " " + (pilot.getAngle()));
        //Gdx.app.debug("banana y vals", String.valueOf(y2) + " " + precisePilotY);

        if(desiredAngle - ((pilot.getAngle())%360) > 2 && y2-precisePilotY >= 0){
            pilot.setAngle(pilot.getAngle()+1);
        }

        if(desiredAngle - ((pilot.getAngle())%360) < 2 && y2-precisePilotY >= 0){
            pilot.setAngle(pilot.getAngle()-1);
        }

//        float trajSlope = (y2- precisePilotY)/(x2-precisePilotX);
//        float desiredSlope = (point.y- precisePilotY)/(point.x-precisePilotX);
//
//        Gdx.app.debug("banana Slopes?", String.valueOf(trajSlope) + " " + String.valueOf(desiredSlope));
//
//        if(Float.isNaN(trajSlope)){
//            trajSlope = 999999;
//        }
//
//        if(Float.isNaN(desiredSlope)){
//            desiredSlope = 999999;
//        }
//
//        if(desiredSlope - trajSlope > 0.1 && y2-precisePilotY > 0){
//            pilot.setAngle(pilot.getAngle()+3);
//        }
//
//        if(desiredSlope - trajSlope < 0.1 && x2-precisePilotX > 0){
//            pilot.setAngle(pilot.getAngle()-3);
//        }
//
//        if(precisePilotY >= planetList.get(1).getPlanetInfo().y){
//            needToAdjustCourse = false;
//        }
    }

	@Override
	public void dispose () {
		batch.dispose();
	}
}
