package part2;

import com.rabbitmq.client.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


public class Users_prova {
  private static final String EXCHANGE_NAME = "name exchange";


  public static void main(String[] argv) throws Exception {
    try {
      // Generate a random UUID
      UUID uuid = UUID.randomUUID();
      String identifier = uuid.toString();
      Map<Pair<Integer, Integer>, Integer> coloredPixels = new HashMap<>();
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

      channel.queueDeclare(identifier + "mouse", false, false, false, null);
      channel.queueDeclare(identifier + "color", false, false, false, null);
      channel.queueDeclare(identifier + "history", false, false, false, null);
      channel.queueDeclare(identifier + "exit", false, false, false, null);
      // Bind the queue to the exchange
      channel.queueBind(identifier + "mouse", EXCHANGE_NAME, "topic.mouse");
      channel.queueBind(identifier + "color", EXCHANGE_NAME, "topic.color");
      channel.queueBind(identifier + "history", EXCHANGE_NAME, "topic.history");
      channel.queueBind(identifier + "exit", EXCHANGE_NAME, "topic.exit");

      String requestMessage = "NEED_HISTORY";
      channel.basicPublish(EXCHANGE_NAME, "topic.history", null, requestMessage.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + requestMessage + "'");


      var brushManager = new BrushManager();
      var localBrush = new BrushManager.Brush(0, 0, randomColor(), identifier);
      brushManager.addBrush(localBrush);
      PixelGrid grid = new PixelGrid(40, 40);


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
        coloredPixels.put(new Pair<>(x, y), localBrush.getColor());
        System.out.println("adding x and y"+ "x="+x +"y="+y);
        System.out.println("coloredPixels => "+ coloredPixels);
        view.refresh();
        //the message contains x, y, color of the brush
        String message = x + "_" + y + "_" + localBrush.getColor();
        channel.basicPublish(EXCHANGE_NAME, "topic.color", null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
      });
      // Consume messages from the queues
      consumeMessages(channel, identifier + "color", coloredPixels, view, grid);

      DeliverCallback deliverCallbackMouse = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateMouse(message, view, brushManager);
      };

      DeliverCallback deliverCallbackExit = (consumerTag, delivery) -> {
        String idBrush = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + idBrush + "' by thread "+Thread.currentThread().getName());
        brushManager.removeBrush(brushManager.getBrushFromId(idBrush));
      };

      DeliverCallback deliverCallbackHistory = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        if (message.equals("NEED_HISTORY")){
          if ( !coloredPixels.isEmpty()){
            //HistoryMap historyMap = new HistoryMap();
            //Map<String, Integer> parsedMap = coloredPixels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getX()+"X"+e.getKey().getY(), Map.Entry::getValue));
            //historyMap.setWrappedMap(parsedMap);
            String stringMessage = coloredPixels.entrySet().stream().map(e -> e.getKey().getX()+"_"+e.getKey().getY()+"_"+e.getValue()).reduce((e1, e2) -> e1.concat("@".concat(e2))).orElse("");
            //Gson customGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64Adapter()).create();
            //String json = customGson.toJson(historyMap);
            channel.basicPublish(EXCHANGE_NAME, "topic.history", null, stringMessage.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + stringMessage + "'");
          }
        } else{
          System.out.println(message);
          //Gson customGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64Adapter()).create();
          //HistoryMap mapWrapper = customGson.fromJson(message.replace("\"", "\\\""), HistoryMap.class);
          String[] cellAndColor = message.split("@");
          coloredPixels.putAll(Arrays.stream(cellAndColor).collect(Collectors.toMap(e -> new Pair<>(Integer.parseInt(e.split("_")[0]), Integer.parseInt(e.split("_")[1])), e -> Integer.parseInt(e.split("_")[2]))));
          coloredPixels.forEach((p, c) -> grid.set(p.getX(), p.getY(), c));
          coloredPixels.forEach((p, c) -> System.out.println("The truth => "+p.getX()+ p.getY()+ c));
        }

        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };

      channel.basicConsume(identifier + "mouse", true, deliverCallbackMouse, consumerTag -> {});
      channel.basicConsume(identifier + "history", true, deliverCallbackHistory, consumerTag -> {});
      channel.basicConsume(identifier + "exit", true, deliverCallbackExit, consumerTag -> {});
      view.addColorChangedListener(localBrush::setColor);

      view.display();
      view.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          // Perform any necessary cleanup or actions
          System.out.println("User is leaving...");
          String brushId = localBrush.getIdBrush();
          try {
            channel.basicPublish(EXCHANGE_NAME, "topic.exit", null, brushId.getBytes(StandardCharsets.UTF_8));
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
          System.out.println(" [x] Sent '" + brushId + "'");

        }
      });


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

  public static void updateColor(String message, PixelGridView view, PixelGrid grid, Map<Pair<Integer, Integer>, Integer> coloredPixels){
    SwingUtilities.invokeLater(() -> {
      String[] messageContent = message.split("_");
      //the message contains x, y, color of the brush
      grid.set(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]), Integer.parseInt(messageContent[2]));
      view.refresh();
      coloredPixels.put(new Pair<>(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1])), Integer.parseInt(messageContent[2]));
    });
  }

  private static void updateMouse(String message, PixelGridView view, BrushManager brushManager) {
    SwingUtilities.invokeLater(() -> {
      String[] messageContent = message.split("_");
      //the message contains the x and y of the mouse and the id and color of the brush
      BrushManager.Brush currentBrush = brushManager.getBrushFromInfo(messageContent);
      currentBrush.updatePosition(Integer.parseInt(messageContent[0]), Integer.parseInt(messageContent[1]));
      view.refresh();
    });
  }

    private static void consumeMessages(Channel channel, String identifier, Map<Pair<Integer, Integer>, Integer> coloredPixels, PixelGridView view, PixelGrid grid) throws IOException {
      // Create a consumer and start consuming messages
      DeliverCallback deliverCallbackColor = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateColor(message, view, grid, coloredPixels);
        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };
      channel.basicConsume(identifier, true, deliverCallbackColor, consumerTag -> {});

  }




}
