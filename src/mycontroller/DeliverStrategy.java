package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
