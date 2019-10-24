package mycontroller;

import controller.CarController;
import world.Car;
import java.util.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * Our implementation of auto car controller.
 */
public class MyAutoController extends CarController {

    private HashMap<Coordinate, MapTile> map = getMap();

    private StrategyFactory strategyFactory;
    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;
    private CompositeStrategy compositeStrategy;
    private SearchRoute searchRoute = new SearchRoute(this.map);
    private int speed;
    private boolean allCollected = false;
    private CurrentCar car;

    public MyAutoController(Car car) {
        super(car);

        this.strategyFactory = StrategyFactory.getInstance();
        this.parcelStrategy = (ParcelStrategy) this.strategyFactory.getStrategy("ParcelStrategy", getView());
        this.exploreStrategy = (ExploreStrategy) this.strategyFactory.getStrategy("ExploreStrategy", getMap());
        this.deliverStrategy = (DeliverStrategy) this.strategyFactory.getStrategy("DeliverStrategy", getMap());
        this.compositeStrategy = (CompositeStrategy) this.strategyFactory.getStrategy("CompositeStrategy", getView());
        compositeStrategy.addStrategy(parcelStrategy);
        compositeStrategy.addStrategy(exploreStrategy);
        compositeStrategy.addStrategy(deliverStrategy);

        this.car = new CurrentCar();

        for (Coordinate coord : map.keySet()) {//manual remove the parcels eaten from the goal list.
            if (!searchRoute.isReachable(getPosition(), getOrientation(), coord)) {
                exploreStrategy.remove(coord);
            }
        }

        this.speed = 0;
    }

    /**
     * For each update operation, we update the explored map with the new view of car,
     * and get goal according to the composite strategies.
     */
    @Override
    public void update() {

        if (getSpeed() == 0) {
            this.speed = 0;
        }
        if (numParcels() - numParcelsFound() == 0) {
            allCollected = true;
        }
        compositeStrategy.update(getView(), allCollected, new Coordinate(getPosition()));
        Coordinate goal = compositeStrategy.getGoal(new Coordinate(getPosition()));

        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), this.speed);
        CarState a = searchRoute.routeSearch(car, goal);
        if (a != null) {
            takeActions(a,this.car);
        }
    }

    /**
     * Given the Car state for the next action and the limitations,
     * transfer it to actual sequence of actions  can be taken from car.
     * @param next the next car state.
     * @param features limitations.
     */
    private void takeActions(CarState next, CarFeature features){
        WorldSpatial.Direction direction = next.getDirec();
        float speed = next.getVelocity();
        switch (this.speed) {
            case 1:
                if (features.maximumTurnings == 1){
                    if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                    turnLeft();
                } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                    turnRight();
                    }
                }
                break;
            case -1:
                if (features.maximumTurnings == 1){
                    if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                    turnRight();
                } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                    turnLeft();
                    }
                }
                break;
        }
        if (this.speed < Math.min(speed, features.maximumForwardSpeed)) {
            applyForwardAcceleration();
            this.speed += 1;
        } else if (this.speed > Math.max(speed,features.maximumBackwardSpeed)) {
            applyReverseAcceleration();
            this.speed -= 1;
        }
    }
}
