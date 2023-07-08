package part3;

import java.util.ArrayList;
import java.util.List;

public class RegisterServiceImpl implements RegisterService{
    List<String> clients = new ArrayList<>();

    @Override
    public void register(String identifier) {
        clients.add(identifier);
    }

    @Override
    public void exit(String identifier) {
        clients.remove(identifier);
    }
}
