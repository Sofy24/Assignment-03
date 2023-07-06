package part2;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.UUID;

public class Users_prova {
  private static final String EXCHANGE_NAME = "name exchange";


  public static void main(String[] argv) throws Exception {
    try {
      // Generate a random UUID
      UUID uuid = UUID.randomUUID();
      String identifier = uuid.toString();

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
      /*channel.exchangeDelete("peer_exchange");
      channel.close();*/
      channel.queueDeclare(identifier + "mouse", false, false, false, null);
      channel.queueDeclare(identifier + "color", false, false, false, null);
      // Bind the queue to the exchange
      channel.queueBind(identifier + "mouse", EXCHANGE_NAME, "topic.mouse");
      channel.queueBind(identifier + "color", EXCHANGE_NAME, "topic.color");

      var brushManager = new BrushManager();
      var localBrush = new BrushManager.Brush(0, 0, randomColor(), identifier);
      brushManager.addBrush(localBrush);
      PixelGrid grid = new PixelGrid(40, 40);

      Random rand = new Random();
      for (int i = 0; i < 10; i++) {
        grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
      }

      PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);

      view.addMouseMovedListener((x, y) -> {
        localBrush.updatePosition(x, y);
        view.refresh();
        //the message contains the x and y of the mouse and the id and color of the brush
        String message = x + "_" + y + "_" + localBrush.getIdBrush() + "_" +  localBrush.getColor();
        channel.basicPublish(EXCHANGE_NAME, "topic.mouse", null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
      });

      view.addPixelGridEventListener((x, y) -> {
        grid.set(x, y, localBrush.getColor());
        view.refresh();
        //the message contains x, y, color of the brush
        String message = x + "_" + y + "_" + localBrush.getColor();
        channel.basicPublish(EXCHANGE_NAME, "topic.color", null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
      });

      DeliverCallback deliverCallbackColor = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateColor(message, view, grid);
        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };

      DeliverCallback deliverCallbackMouse = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateMouse(message, view, grid, brushManager);
        try {
          Thread.sleep(1000);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };

      channel.basicConsume(identifier + "color", true, deliverCallbackColor, consumerTag -> {});
      channel.basicConsume(identifier + "mouse", true, deliverCallbackMouse, consumerTag -> {});

      view.addColorChangedListener(localBrush::setColor);

      view.display();

    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }




    /*
    channel.close();
    connection.close();
    */


  public static int randomColor() {
    Random rand = new Random();
    return rand.nextInt(256 * 256 * 256);
  }

  public static void updateColor(String message, PixelGridView view, PixelGrid grid){
    String[] messageContent = message.split("_");
    //the message contains x, y, color of the brush
    grid.set(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]), Integer.parseInt(messageContent[2]));
    view.refresh();
  }

  private static void updateMouse(String message, PixelGridView view, PixelGrid grid, BrushManager brushManager) {
    String[] messageContent = message.split("_");
    //the message contains the x and y of the mouse and the id and color of the brush
    BrushManager.Brush currentBrush = brushManager.getBrush(messageContent);
    currentBrush.updatePosition(currentBrush.getX(), currentBrush.getY());
    view.refresh();
  }



}
