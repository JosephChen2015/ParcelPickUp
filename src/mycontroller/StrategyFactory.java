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

    public static StrategyFactory getInstance(){
        if (instance == null){
            instance = new StrategyFactory();
        }
        return instance;
    }

    public IStrategy getStrategy(String name, HashMap<Coordinate, MapTile> map) {
        IStrategy strategy = null;
        switch (name){
            case "ExploreStrategy":
                if (exploreStrategy == null){
                    this.exploreStrategy = new ExploreStrategy(map);
                }
                strategy = this.exploreStrategy;
                break;
            case "ParcelStrategy":
                if (parcelStrategy == null){
                    this.parcelStrategy = new ParcelStrategy();
                }
                strategy = this.parcelStrategy;
                break;
            case "DeliverStrategy":
                if (deliverStrategy == null){
                    this.deliverStrategy = new DeliverStrategy(map);
                }
                strategy = this.deliverStrategy;
                break;
            case "CompositeStrategy":
                if (compositeStrategy == null){
                    this.compositeStrategy = new CompositeStrategy();
                }
                strategy = this.compositeStrategy;
                break;
        }
        return strategy;
    }

}
