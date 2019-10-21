package mycontroller;

import controller.CarController;
import world.Car;
import java.util.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController {

    private HashMap<Coordinate, MapTile> map = getMap();

    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;
    private SearchRoute searchRoute = new SearchRoute(this.map);
    private int speed;

    public MyAutoController(Car car) {
        super(car);
        this.exploreStrategy = new ExploreStrategy(getMap());
        for (Coordinate coord : map.keySet()){
            if (searchRoute.aStar(new CarState(new Coordinate(getPosition()), getOrientation(), 0), coord) == null){
                exploreStrategy.remove(coord);
            }
        }
        this.parcelStrategy = new ParcelStrategy();
        this.deliverStrategy = new DeliverStrategy(getMap());
        this.speed = 0;
    }

    @Override
    public void update() {
        if (getSpeed()==0){
            this.speed = 0;
        }
        exploreStrategy.update(getView());
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        Coordinate goal;
        Coordinate myPos = new Coordinate(getPosition());
        parcelStrategy.update(parcels);

        if(parcels.containsKey(myPos))
        {
            parcels.remove(myPos);
        }

        if(numParcels() - numParcelsFound() > 0)
        {
            if(parcels.size() != 0)
            {
                goal = parcelStrategy.getGoal(new Coordinate(getPosition()));
            }
            else {
                goal = exploreStrategy.getGoal(new Coordinate(getPosition()));
            }
        }
        else
        {
            goal = deliverStrategy.getGoal(new Coordinate(getPosition()));
        }

        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), this.speed);
        CarState a = searchRoute.aStar(car, goal);
        if (a != null) {
            WorldSpatial.Direction direction = a.getDirec();
            float speed = a.getVelocity();

            if (this.speed < speed) {
                applyForwardAcceleration();
                this.speed += 1;
            } else if (this.speed > speed) {
                applyReverseAcceleration();
                this.speed -= 1;
            }

            if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                turnLeft();
            } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                turnRight();
            }
        }
    }
}
