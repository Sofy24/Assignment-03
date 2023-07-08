package part3;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;


public class PixelArtImpl implements PixelArt, Serializable {

  // Generate a random UUID
  private static final UUID serialVersionUID = UUID.randomUUID();
  private BrushManager brushManager = new BrushManager();
  private BrushManager.Brush localBrush = new BrushManager.Brush(0, 0, randomColor());
  private PixelGrid grid = new PixelGrid(40, 40);
  private Map<Pair<Integer, Integer>, Integer> coloredPixels = new HashMap<>();
  private PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);
  private GridService gridService;

  public void configuration()  {
    try {
      Registry registry = LocateRegistry.getRegistry(null);
      this.gridService = (GridService) registry.lookup("grid");
      //registration of the client
      gridService.register(this);
      //deve essere serializzabile
      //gridService.setPixel(0, 0, 0);
    } catch (Exception e) {
      System.err.println("Client exception: " + e);
      e.printStackTrace();
    }
    brushManager.addBrush(localBrush);

    //listener for the movement of the mouse
    view.addMouseMovedListener((x, y) -> {
      localBrush.updatePosition(x, y);
      view.refresh();

    });

    //listener for the addition of the pixel
    view.addPixelGridEventListener((x, y) -> {
      this.gridService.setPixel(x, y, localBrush.getColor());
    });

    view.addColorChangedListener(localBrush::setColor);



    //listener for the closure of the window
    view.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // Perform any necessary cleanup or actions
        System.out.println("User is leaving...");
        //String brushId = localBrush.getIdBrush();
        //registration.exit(identifier);
      }
    });

    view.display();
  }



  public static void main(String[] args) throws Exception {
    PixelArt pixelArt = new PixelArtImpl();
    pixelArt.configuration();
    }

  //get a random color
  public static int randomColor() {
    Random rand = new Random();
    return rand.nextInt(256 * 256 * 256);
  }

  //update of the colored pixels
  public static void updateColor(String message, PixelGridView view, PixelGrid grid, Map<Pair<Integer, Integer>, Integer> coloredPixels){
    SwingUtilities.invokeLater(() -> {

      //grid.set(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]), Integer.parseInt(messageContent[2]));
      view.refresh();
      //coloredPixels.put(new Pair<>(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1])), Integer.parseInt(messageContent[2]));
    });
  }

  //update of the movement of the mouse
  private static void updateMouse(String message, PixelGridView view, BrushManager brushManager) {
    SwingUtilities.invokeLater(() -> {

      //BrushManager.Brush currentBrush = brushManager.getBrushFromInfo(messageContent);
      //currentBrush.updatePosition(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]));
      view.refresh();
    });
  }



  @Override
  public void receiveGrid(Map<Pair<Integer, Integer>, Integer> map) {
    this.coloredPixels.putAll(map);
    this.coloredPixels.forEach((p, c) -> this.grid.set(p.getX(), p.getY(), c));
    //renderizza mappa
    view.refresh();
  }
}