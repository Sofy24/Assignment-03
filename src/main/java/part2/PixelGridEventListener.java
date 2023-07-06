package part2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface PixelGridEventListener {
	void selectedCell(int x, int y) throws IOException;
}
