package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

public class StrategyFactory {

    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;
    private CompositeStrategy compositeStrategy;

    private static StrategyFactory instance;

    private StrategyFactory(){

    }

    public void init(HashMap<Coordinate, MapTile> map){
        this.parcelStrategy = new ParcelStrategy();
        this.deliverStrategy = new DeliverStrategy(map);
        this.exploreStrategy = new ExploreStrategy(map);
        this.compositeStrategy = new CompositeStrategy();
    }

    public static StrategyFactory getInstance(){
        if (instance == null){
            instance = new StrategyFactory();
        }
        return instance;
    }

    public ExploreStrategy getExploreStrategy() {
        return exploreStrategy;
    }

    public ParcelStrategy getParcelStrategy() {
        return parcelStrategy;
    }

    public DeliverStrategy getDeliverStrategy() {
        return deliverStrategy;
    }

    public CompositeStrategy getCompositeStrategy() {
        return compositeStrategy;
    }
}
