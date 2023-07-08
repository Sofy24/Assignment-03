package part3;


import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BrushManager implements Serializable {

    // Generate a random UUID
    private static final UUID serialVersionUID = UUID.randomUUID();
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 2;
    private Set<Brush> brushes = new HashSet<>();

    void draw(final Graphics2D g) {
        brushes.forEach(brush -> {
            g.setColor(new Color(brush.color));
            var circle = new java.awt.geom.Ellipse2D.Double(brush.x - BRUSH_SIZE / 2.0, brush.y - BRUSH_SIZE / 2.0, BRUSH_SIZE, BRUSH_SIZE);
            // draw the polygon
            g.fill(circle);
            g.setStroke(new BasicStroke(STROKE_SIZE));
            g.setColor(Color.BLACK);
            g.draw(circle);
        });
    }

    public void addBrush(final Brush brush) {
        brushes.add(brush);
    }

    public void removeBrush(final Brush brush) {
        brushes.remove(brush);
    }

    public static class Brush implements Serializable{

        // Generate a random UUID
        private static final UUID serialVersionUID = UUID.randomUUID();
        private int x, y;
        private int color;
        public Brush(final int x, final int y, final int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void updatePosition(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
        // write after this getter and setters
        public int getX(){
            return this.x;
        }
        public int getY(){
            return this.y;
        }
        public int getColor(){
            return this.color;
        }
        public void setColor(int color){
            this.color = color;
        }

    }
}