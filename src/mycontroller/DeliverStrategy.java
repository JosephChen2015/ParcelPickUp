package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliverStrategy implements Strategy{

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

    public Coordinate getGoal( Coordinate myPosit)
    {
        int max = 0;
        Coordinate exit = myPosit;
        for (Coordinate temp: exits)
        {
            int tempDis =
                    Math.abs(temp.x - myPosit.x) + Math.abs(temp.y - myPosit.y);
            if( tempDis > max)
            {
                max = tempDis;
                exit = temp;
            }
        }
        return exit;
    }
}
