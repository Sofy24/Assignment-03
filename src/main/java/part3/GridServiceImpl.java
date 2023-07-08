package part3;

import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridServiceImpl implements GridService {
    Map<Pair<Integer, Integer>, Integer> grid = new HashMap<>();
    List<PixelArt> clients = new ArrayList<>();

    @Override
    public void register(PixelArt identifier) {
        clients.add(identifier);
        identifier.receiveGrid(grid);
    }

    /*@Override
    public void exit(PixelArt identifier) {
        clients.remove(identifier);
    }*/

    @Override
    public void setPixel(Integer x, Integer y, Integer color) {
        this.grid.put(new Pair<>(x, y), color);
        //notifica aggiornamento
        this.clients.forEach(c -> c.receiveGrid(this.grid));
    }


    /*@Override
    public void setMouse(Integer x, Integer y) {

    }*/
}
