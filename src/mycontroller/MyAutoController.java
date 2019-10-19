package mycontroller;

import controller.CarController;
import world.Car;
import java.util.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController {

    private HashMap<Coordinate, MapTile> map = getMap();
    private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;
    private SearchRoute searchRoute = new SearchRoute(this.map);
    public MyAutoController(Car car) {
        super(car);
        this.exploreStrategy = new ExploreStrategy(getMap());
        for (Coordinate coord : map.keySet()){
            if (searchRoute.aStar(new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed()), coord) == null){
                exploreStrategy.remove(coord);
            }
        }
    }



    // Coordinate initialGuess;
    // boolean notSouth = true;
    @Override
    public void update() {
        exploreStrategy.update(getView());
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        parcelStrategy = new ParcelStrategy(exploreStrategy.getParcels(), new Coordinate(getPosition()));
        deliverStrategy = new DeliverStrategy(getMap(), new Coordinate(getPosition()));
        Coordinate goal;
        Coordinate myPos = new Coordinate(getPosition());


        if(parcels.containsKey(myPos))
        {
            parcels.remove(myPos);
        }

        if(numParcels() - numParcelsFound() > 0)
        {
            if(parcels.size() != 0)
            {
                goal = parcelStrategy.getGoal();
            }
            else {
                goal = exploreStrategy.goal(new Coordinate(getPosition()));
            }
        }
        else
        {
            goal = deliverStrategy.getGoal();
        }

        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed());
        CarState a = searchRoute.aStar(car, goal);
        if (a != null) {
            WorldSpatial.Direction direction = a.getDirec();
            float speed = a.getVelocity();

            if (getSpeed() < speed) {
                applyForwardAcceleration();
            } else if (getSpeed() > speed) {
                applyReverseAcceleration();
            }

            if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                turnLeft();
            } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                turnRight();
            }
        }
    }





}
