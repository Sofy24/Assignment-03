package part2;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.UUID;

public class Users_prova {

  //private final static String QUEUE_NAME = "hello";
  private final static String NO_EXCHANGE_USED = "";

  private static final String EXCHANGE_NAME = "peer_exchange";

  public static void main(String[] argv) throws Exception {
    try {
      // Generate a random UUID
      UUID uuid = UUID.randomUUID();
      String queueName = uuid.toString();

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();

      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT); //BuiltinExchangeType.DIRECT
      /*channel.exchangeDelete("peer_exchange");
      channel.close();*/


      channel.queueDeclare(queueName, false, false, false, null);
      // Bind the queue to the exchange
      channel.queueBind(queueName, EXCHANGE_NAME, "");
      DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };

      channel.basicConsume(queueName, true, deliverCallback1, consumerTag -> {});
      // Start consuming messages
      /*Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
          String message = new String(body, "UTF-8");
          System.out.println("Received message: " + message);
        }
      };

      channel.basicConsume(QUEUE_NAME, true, consumer);*/

      // Publish a message
      String message2 = "Hello, peers!" + randomColor();
      channel.basicPublish(EXCHANGE_NAME, "", null, message2.getBytes("UTF-8"));
      System.out.println("Sent message: " + message2);


      var brushManager = new BrushManager();
      var localBrush = new BrushManager.Brush(0, 0, randomColor());
      var fooBrush = new BrushManager.Brush(0, 0, randomColor());
      brushManager.addBrush(localBrush);
      brushManager.addBrush(fooBrush);
      PixelGrid grid = new PixelGrid(40, 40);

      Random rand = new Random();
      for (int i = 0; i < 10; i++) {
        grid.set(rand.nextInt(40), rand.nextInt(40), randomColor());
      }

      PixelGridView view = new PixelGridView(grid, brushManager, 800, 600);

      view.addMouseMovedListener((x, y) -> {
        localBrush.updatePosition(x, y);
        view.refresh();
        String message = "mouse moved! " + x + " " + y;
//        channel.basicPublish(NO_EXCHANGE_USED, QUEUE_NAME, null, message.getBytes("UTF-8"));
//        System.out.println(" [x] Sent '" + message + "'");
      });

      view.addPixelGridEventListener((x, y) -> {
        grid.set(x, y, localBrush.getColor());
        view.refresh();
        String message = "pixel colored! " + x + " " + y + " " + localBrush.getColor();
        channel.basicPublish(NO_EXCHANGE_USED, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
      });

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



}
