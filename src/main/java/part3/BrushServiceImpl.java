package part3;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class BrushServiceImpl implements BrushService{

    private List<PixelArt> clients = new ArrayList<>();

    private Set<BrushManager.Brush> brushes = new HashSet<>();

    @Override
    public void addBrush(PixelArt client) throws RemoteException {
        System.out.println("ADD BRUSH:" + client.getClientId()+" "+client.getLocalBrush().getBrushId().toString());
        clients.add(client);
        brushes.add(client.getLocalBrush());
        client.receiveBrushes(brushes);
    }

    @Override
    public void removeBrush(UUID clientId, BrushManager.Brush brush) throws RemoteException {
        clients = clients.stream().filter(c -> {
            try {
                return !c.getClientId().equals(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        brushes = brushes.stream().filter(b -> {
            return ! b.getBrushId().equals(brush.getBrushId());
        }).collect(Collectors.toSet());
        clients.forEach(c -> {
            try {
                c.receiveBrushes(brushes);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void receiveMovement(BrushManager.Brush brush) throws RemoteException {
        brushes = brushes.stream().map(b -> {
            if (b.getBrushId().equals(brush.getBrushId())) {
                return brush;
            } else {
                return b;
            }
        }).collect(Collectors.toSet());
        clients.forEach(c -> {
            try {
                c.receiveBrushes(brushes);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
