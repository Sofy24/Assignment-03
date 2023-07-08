package part3.serializers;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class SerializableBufferedImage extends BufferedImage implements Serializable {
    public SerializableBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }
}
