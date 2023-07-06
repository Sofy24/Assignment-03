package part2;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.UUID;

public class Users_prova {
  private static final String EXCHANGE_NAME_COLOR = "Color";
  private static final String EXCHANGE_NAME_MOUSE = "Mouse";

  public static void main(String[] argv) throws Exception {
    try {
      // Generate a random UUID
      UUID uuid = UUID.randomUUID();
      String identifier = uuid.toString();

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connectionColor = factory.newConnection();
      Connection connectionMouse = factory.newConnection();
      Channel channelColor = connectionColor.createChannel();
      Channel channelMouse = connectionMouse.createChannel();
      channelColor.exchangeDeclare(EXCHANGE_NAME_COLOR, BuiltinExchangeType.TOPIC);
      channelMouse.exchangeDeclare(EXCHANGE_NAME_MOUSE, BuiltinExchangeType.TOPIC);
      /*channelColor.exchangeDelete("peer_exchange");
      channelColor.close();*/
      channelColor.queueDeclare(identifier, false, false, false, null);
      channelMouse.queueDeclare(identifier, false, false, false, null);
      // Bind the queue to the exchange
      channelColor.queueBind(identifier, EXCHANGE_NAME_COLOR, EXCHANGE_NAME_COLOR);
      channelMouse.queueBind(identifier, EXCHANGE_NAME_MOUSE, EXCHANGE_NAME_MOUSE);

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
        channelMouse.basicPublish(EXCHANGE_NAME_MOUSE, EXCHANGE_NAME_MOUSE, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
      });

      view.addPixelGridEventListener((x, y) -> {
        grid.set(x, y, localBrush.getColor());
        view.refresh();
        //the message contains x, y, color of the brush
        String message = x + "_" + y + "_" + localBrush.getColor();
        channelColor.basicPublish(EXCHANGE_NAME_COLOR, EXCHANGE_NAME_COLOR, null, message.getBytes("UTF-8"));
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

      channelColor.basicConsume(identifier, true, deliverCallbackColor, consumerTag -> {});
      channelMouse.basicConsume(identifier, true, deliverCallbackMouse, consumerTag -> {});

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
