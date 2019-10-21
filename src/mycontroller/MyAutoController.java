package mycontroller;

import controller.CarController;
import world.Car;
import java.util.*;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

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

    public MyAutoController(Car car) {
        super(car);

        this.strategyFactory = StrategyFactory.getInstance();
        this.strategyFactory.init(getMap());
        this.parcelStrategy = this.strategyFactory.getParcelStrategy();
        this.exploreStrategy = this.strategyFactory.getExploreStrategy();
        this.deliverStrategy = this.strategyFactory.getDeliverStrategy();
        this.compositeStrategy = this.strategyFactory.getCompositeStrategy();
        compositeStrategy.addStrategy(parcelStrategy);
        compositeStrategy.addStrategy(exploreStrategy);
        compositeStrategy.addStrategy(deliverStrategy);


        for (Coordinate coord : map.keySet()){
            if (searchRoute.aStar(new CarState(new Coordinate(getPosition()), getOrientation(), 0), coord) == null){
                exploreStrategy.remove(coord);
            }
        }

        this.speed = 0;
    }

    @Override
    public void update() {
        if(numParcels()-numParcelsFound()>0)
        {
            allCollected = true;
        }
        compositeStrategy.update(getView(), allCollected);
        Coordinate goal = compositeStrategy.getGoal(new Coordinate(getPosition()));

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
