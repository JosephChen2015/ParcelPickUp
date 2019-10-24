package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * This class is a singleton factory of strategies, it provides extensibility for more strategies.
 */
public class StrategyFactory {

    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;
    private CompositeStrategy compositeStrategy;

    private static StrategyFactory instance;

    private StrategyFactory(){

    }

    /**
     * @return return the single instance of the strategy factory.
     */
    public static StrategyFactory getInstance(){
        if (instance == null){
            instance = new StrategyFactory();
        }
        return instance;
    }

    /**
     * Identify different strategies by their name.
     * @param name of strategy
     * @param map the explored map.
     * @return an instance of a specific strategy.
     */
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
