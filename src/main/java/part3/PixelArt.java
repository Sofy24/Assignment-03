package part3;


import java.util.Map;

public interface PixelArt  {

    void receiveGrid(Map<Pair<Integer, Integer>, Integer> grid);

    void configuration();

}
