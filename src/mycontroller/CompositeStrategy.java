package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class applying the composite patterns for various strategies.
 */
public class CompositeStrategy implements IStrategy{
    private ArrayList<IStrategy> strategies = new ArrayList<IStrategy>();
    private boolean allCollected = false;

    /**
     * This method combines the getGoal() functions of all strategies and handling the logic of which strategy is used now.
     * @param myPosit
     * @return
     */
    @Override
    public Coordinate getGoal(Coordinate myPosit) {
        ExploreStrategy exploreStrategy = null;
        ParcelStrategy parcelStrategy = null;
        DeliverStrategy deliverStrategy = null;
        for(IStrategy strategy: strategies)
        {
            if (strategy instanceof ExploreStrategy){
                exploreStrategy = (ExploreStrategy) strategy;
            }
            else if (strategy instanceof ParcelStrategy){
                parcelStrategy = (ParcelStrategy) strategy;
            }
            else if (strategy instanceof DeliverStrategy){
                deliverStrategy = (DeliverStrategy) strategy;
            }
        }
        Coordinate goal;
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        if(!allCollected) // if the required number of parcels is not collected
        {
            if(parcels.size() != 0) // if there is parcel available in explored map
            {
                goal = parcelStrategy.getGoal(myPosit);
            }
            else {  // keep exploration
                goal = exploreStrategy.getGoal(myPosit);
            }
        }
        else
        {
            goal = deliverStrategy.getGoal(myPosit);
        }
        return goal;
    }

    public void update(HashMap<Coordinate, MapTile> view, boolean allCollected,Coordinate myPos)
    {
        this.allCollected = allCollected;
        ExploreStrategy exploreStrategy = null;
        ParcelStrategy parcelStrategy = null;
        DeliverStrategy deliverStrategy;
        for(IStrategy strategy: strategies)
        {
            if (strategy instanceof ExploreStrategy){
                exploreStrategy = (ExploreStrategy) strategy;
            }
            else if (strategy instanceof ParcelStrategy){
                parcelStrategy = (ParcelStrategy) strategy;
            }
            else if (strategy instanceof DeliverStrategy){
                deliverStrategy = (DeliverStrategy) strategy;
            }


        }
        exploreStrategy.update(view);
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        parcelStrategy.update(parcels);
        if (parcels.containsKey(myPos)){
            exploreStrategy.pickUp(myPos);
        }
    }

    public void addStrategy(IStrategy strategy){
        this.strategies.add(strategy);
    }


}
