package part3;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class BrushServiceImpl implements BrushService{

    private List<PixelArt> clients = new ArrayList<>();

    private final Map<UUID, BrushManager.Brush> brushes = new HashMap<>();

    @Override
    public synchronized void addBrush(PixelArt client) throws RemoteException {
        System.out.println("ADD BRUSH:" + client.getClientId()+" "+client.getLocalBrush().getBrushId().toString());
        clients.add(client);
        brushes.put(client.getClientId(), client.getLocalBrush());
        client.receiveBrushes(new HashSet<>(brushes.values()));
    }

    @Override
    public synchronized void removeBrush(UUID clientId, BrushManager.Brush brush) throws RemoteException {
        clients = clients.stream().filter(c -> {
            try {
                return !c.getClientId().equals(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        brushes.remove(clientId);
        sendBrushes(clientId);
    }

    @Override
    public synchronized void receiveMovement(UUID clientId, BrushManager.Brush brush) throws RemoteException {
        brushes.put(clientId, brush);
        sendBrushes(clientId);
    }

    private void sendBrushes(UUID clientId) {
        Set<BrushManager.Brush> brushSet = new HashSet<>(brushes.values());
        clients.forEach(c -> {
            try {
                if (!c.getClientId().equals(clientId)) {
                    c.receiveBrushes(brushSet);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
