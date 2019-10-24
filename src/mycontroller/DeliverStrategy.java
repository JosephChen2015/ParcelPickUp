package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * This class defines the strategies applying on delivering the parcel to finish points.
 */
public class DeliverStrategy implements IStrategy{

    private ArrayList<Coordinate> exits;

    public DeliverStrategy(HashMap<Coordinate, MapTile> explored)
    {
        exits = new ArrayList<>();
        for (Map.Entry<Coordinate, MapTile> entry: explored.entrySet())
        {
            if(entry.getValue().getType().equals(MapTile.Type.FINISH))
            {
                exits.add(entry.getKey());
            }
        }
    }
    /**
     * This function aims to find the nearest finish point as the goal for win the game.
     * @param myPosit current position of our car
     * @return  the coordinate of the nearest finish point
     */
    @Override
    public Coordinate getGoal(Coordinate myPosit)
    {
        int min = 99999;
        Coordinate goal = null;
        for (Coordinate temp: exits)
        {
            int tempDis =
                    Math.abs(temp.x - myPosit.x) + Math.abs(temp.y - myPosit.y);
            if( tempDis < min)
            {
                min = tempDis;
                goal = temp;
            }
        }
        return goal;
    }
}
