package part3;

import scala.Int;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class GridServiceImpl implements GridService {
    Map<Pair<Integer, Integer>, Integer> grid = new HashMap<>();
    List<PixelArt> clients = new ArrayList<>();

    @Override
    public void register(PixelArt client) throws RemoteException {
        System.out.println("ADD CLIENT:" + client.getClientId());
        clients.add(client);
        client.receiveGrid(grid);
    }

    @Override
    public void exit(UUID clientId) {
        System.out.println("BEFORE CLIENT:" +clients.size());
        clients = clients.stream().filter(c -> {
            try {
                return !c.getClientId().equals(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        System.out.println("AFTER CLIENT:" +clients.size());
        sendGrid();
    }

    @Override
    public void setPixel(Integer x, Integer y, Integer color) {
        this.grid.put(new Pair<>(x, y), color);
        //notifica aggiornamento
        sendGrid();
    }

    private void sendGrid() {
        this.clients.forEach(c -> {
            try {
                c.receiveGrid(this.grid);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
