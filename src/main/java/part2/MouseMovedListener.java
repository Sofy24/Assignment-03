package part2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface MouseMovedListener {
    void mouseMoved(int x, int y) throws IOException;
}
