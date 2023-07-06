package part2;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import part1.Utils.Pair;

import java.util.Random;

public class Users_prova {

  private final static String QUEUE_NAME = "hello";
  private final static String NO_EXCHANGE_USED = "";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);


    var brushManager = new BrushManager();
    var localBrush = new BrushManager.Brush(0, 0, randomColor());
    var fooBrush = new BrushManager.Brush(0, 0, randomColor());
    brushManager.addBrush(localBrush);
    brushManager.addBrush(fooBrush);
    PixelGrid grid = new PixelGrid(40,40);

    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
    }

    PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);

    view.addMouseMovedListener((x, y) -> {
      localBrush.updatePosition(x, y);
      view.refresh();
      String message = "mouse moved! "+x+" "+y;
      channel.basicPublish(NO_EXCHANGE_USED, QUEUE_NAME, null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + message + "'");
    });

    view.addPixelGridEventListener((x, y) -> {
      grid.set(x, y, localBrush.getColor());
      view.refresh();
      String message = "pixel colored! "+x+" "+y+" "+localBrush.getColor();
      channel.basicPublish(NO_EXCHANGE_USED, QUEUE_NAME, null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + message + "'");
    });

    view.addColorChangedListener(localBrush::setColor);

    view.display();




    /*
    channel.close();
    connection.close();
    */
  }


  public static int randomColor() {
    Random rand = new Random();
    return rand.nextInt(256 * 256 * 256);
  }




}
