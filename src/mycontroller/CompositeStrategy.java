package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

public class CompositeStrategy implements IStrategy{
    private ArrayList<IStrategy> strategies = new ArrayList<IStrategy>();
    private boolean allCollected = false;

    @Override
    public Coordinate getGoal(Coordinate myPosit) {
        ExploreStrategy exploreStrategy = null;
        ParcelStrategy parcelStrategy = null;
        DeliverStrategy deliverStrategy = null;
        for(IStrategy strategy: strategies)
        {
            switch (strategy.getClass().toString()) {
                case "ExploreStrategy":
                    exploreStrategy = (ExploreStrategy) strategy;
                    break;
                case "ParcelStrategy":
                    parcelStrategy = (ParcelStrategy) strategy;
                    break;
                case "DeliverStrategy":
                    deliverStrategy = (DeliverStrategy) strategy;
                    break;
            }
        }
        Coordinate goal;
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        if(allCollected)
        {
            if(parcels.size() != 0)
            {
                goal = parcelStrategy.getGoal(myPosit);
            }
            else {
                goal = exploreStrategy.getGoal(myPosit);
            }
        }
        else
        {
            goal = deliverStrategy.getGoal(myPosit);
        }
        return goal;
    }

    public void update(HashMap<Coordinate, MapTile> view, boolean allCollected)
    {
        this.allCollected = allCollected;
        ExploreStrategy exploreStrategy = null;
        ParcelStrategy parcelStrategy = null;
        DeliverStrategy deliverStrategy;
        for(IStrategy strategy: strategies)
        {
            switch (strategy.getClass().toString()) {
                case "ExploreStrategy":
                    exploreStrategy = (ExploreStrategy) strategy;
                    break;
                case "ParcelStrategy":
                    parcelStrategy = (ParcelStrategy) strategy;
                    break;
                case "DeliverStrategy":
                    deliverStrategy = (DeliverStrategy) strategy;
                    break;
            }
        }
        exploreStrategy.update(view);
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        parcelStrategy.update(parcels);
    }

    public void addStrategy(IStrategy strategy){
        this.strategies.add(strategy);
    }


}
