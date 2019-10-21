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
        this.parcelStrategy = (ParcelStrategy) this.strategyFactory.getStrategy("ParcelStrategy",getView());
        this.exploreStrategy = (ExploreStrategy) this.strategyFactory.getStrategy("ExploreStrategy",getMap());
        this.deliverStrategy = (DeliverStrategy)this.strategyFactory.getStrategy("DeliverStrategy",getMap());
        this.compositeStrategy = (CompositeStrategy) this.strategyFactory.getStrategy("CompositeStrategy",getView());
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

        if (getSpeed() == 0){
            this.speed = 0;
        }
        if(numParcels()-numParcelsFound()==0)
        {
            allCollected = true;
        }
        compositeStrategy.update(getView(), allCollected,new Coordinate(getPosition()));
        Coordinate goal = compositeStrategy.getGoal(new Coordinate(getPosition()));

        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), this.speed);
        CarState a = searchRoute.routeSearch(car, goal);
        if (a != null) {
            WorldSpatial.Direction direction = a.getDirec();
            float speed = a.getVelocity();
            switch (this.speed){
                case 1:
                    if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                        turnLeft();
                    } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                        turnRight();
                    }
                    break;
                case -1:
                    if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                        turnRight();
                    } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {

                        turnLeft();
                    }
                    break;
            }
            if (this.speed < speed) {
                applyForwardAcceleration();
                this.speed += 1;
            } else if (this.speed > speed) {
                applyReverseAcceleration();
                this.speed -= 1;
            }

        }
//        else{
//            if (this.speed == 1) {
//                if (map.get(SearchRoute.move(car.getCoord(), getOrientation())).isType(MapTile.Type.WALL)) {
//                    applyReverseAcceleration();
//                    this.speed -= 1;
//                }
//            }
//            else if (this.speed == -1){
//                if (map.get(SearchRoute.move(car.getCoord(),WorldSpatial.reverseDirection(getOrientation()))).isType(MapTile.Type.WALL)){
//                    applyForwardAcceleration();
//                    this.speed += 1;
//                }
//            }
//        }
    }
}
