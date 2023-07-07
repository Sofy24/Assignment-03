package part2;

import com.google.gson.GsonBuilder;
import com.rabbitmq.client.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import part1.Utils.Pair;


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
      /*channel.exchangeDelete("peer_exchange");
      channel.close();*/
      channel.queueDeclare(identifier + "mouse", false, false, false, null);
      channel.queueDeclare(identifier + "color", false, false, false, null);
      channel.queueDeclare(identifier + "history", false, false, false, null);
      // Bind the queue to the exchange
      channel.queueBind(identifier + "mouse", EXCHANGE_NAME, "topic.mouse");
      channel.queueBind(identifier + "color", EXCHANGE_NAME, "topic.color");
      channel.queueBind(identifier + "history", EXCHANGE_NAME, "topic.history");

      String requestMessage = "NEED_HISTORY";
      channel.basicPublish(EXCHANGE_NAME, "topic.history", null, requestMessage.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + requestMessage + "'");


      var brushManager = new BrushManager();
      var localBrush = new BrushManager.Brush(0, 0, randomColor(), identifier);
      brushManager.addBrush(localBrush);
      PixelGrid grid = new PixelGrid(40, 40);

      //delete them
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

      /*DeliverCallback deliverCallbackColor = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateColor(message, view, grid);
        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };*/

      DeliverCallback deliverCallbackMouse = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        updateMouse(message, view, brushManager);
/*        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }*/
      };

      DeliverCallback deliverCallbackHistory = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received A '" + message + "' by thread "+Thread.currentThread().getName());
        if (message.equals("NEED_HISTORY")){
          if ( !coloredPixels.isEmpty()){
            HistoryMap historyMap = new HistoryMap();
            Map<String, Integer> parsedMap = coloredPixels.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getX()+"_"+e.getKey().getY(), Map.Entry::getValue));
            historyMap.setWrappedMap(parsedMap);
            Gson customGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64Adapter()).create();
            String json = customGson.toJson(historyMap);
            channel.basicPublish(EXCHANGE_NAME, "topic.history", null, json.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + json + "'");
          }
        } else{
          System.out.println(message.replace("\"", "\\\""));
          Gson customGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64Adapter()).create();
          HistoryMap mapWrapper = customGson.fromJson(message.replace("\"", "\\\""), HistoryMap.class);
          coloredPixels.putAll(mapWrapper.getWrappedMap().entrySet().stream().collect(Collectors.toMap(e -> new Pair<>(Integer.parseInt(e.getKey().split("_")[0]), Integer.parseInt(e.getKey().split("_")[1])), e -> e.getValue())));

        }

        try {
          Thread.sleep(10);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      };

//      channel.basicConsume(identifier + "color", true, deliverCallbackColor, consumerTag -> {});
      channel.basicConsume(identifier + "mouse", true, deliverCallbackMouse, consumerTag -> {});
      channel.basicConsume(identifier + "history", true, deliverCallbackHistory, consumerTag -> {});
      view.addColorChangedListener(localBrush::setColor);
      coloredPixels.forEach((p, c) -> grid.set(p.getX(), p.getY(), c));
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
      BrushManager.Brush currentBrush = brushManager.getBrush(messageContent);
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
