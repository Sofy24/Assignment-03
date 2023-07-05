package org.example.GUI;

import akka.actor.ActorRef;
import org.example.Utils.ComputedFile;
import org.example.Utils.LongRange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;


public class ViewDistributionFrame extends JFrame {
    
    private final DistributionPanel panel;
    private final ActorRef actorView;
    
    public ViewDistributionFrame(ActorRef actorView, int w, int h){
        setTitle("LoC Distribution");
        this.actorView = actorView;
        setSize(w, h);
        setResizable(false);
        this.setLocation(200, 400);
        panel = new DistributionPanel(w,h);
        this.getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
    }
    
    public void updateDistribution(List<ComputedFile> files, List<LongRange> ranges){
    	SwingUtilities.invokeLater(() -> panel.updateDistribution(files, ranges));
    }
        
    public static class DistributionPanel extends JPanel {
        private final int w;
        private final int h;
        private List<ComputedFile> files;
        private List<LongRange> ranges;
        
        public DistributionPanel(int w, int h){
            setSize(w,h);
            this.w = w;
            this.h = h - 150;
            this.files = new ArrayList<>();
            this.ranges = new ArrayList<>();
        }

        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0,0,this.getWidth(),this.getHeight());

            if (!files.isEmpty()) {
                List<Long> bands = new ArrayList<>();
                for (LongRange range : ranges) {
                    bands.add(files.stream().filter(f -> f.getMinRange().equals(range.getMin())).count());

                }

                long max = 0;
                for (Long band : bands) {
                    if (band > max) {
                        max = band;
                    }
                }

	    		if (max > 0) {
                    int x = 10;
                    double deltay = (((double) h) / max) * 0.9;

                    int deltax = (w - 20) / bands.size();

                    Font font = new Font(null, Font.PLAIN, 8);
                    g2.setFont(font);

                    for (Long band : bands) {
                        int height = (int) (band * deltay);
                        g2.drawRect(x, h - height, deltax - 10, height);
                        g2.drawString("" + band, x, h - height - 10);
                        x += deltax;
                    }


                    long nLocPerBand = ranges.get(ranges.size() - 1).getMin() / (bands.size() - 1);
                    long a = 0;
                    long b = nLocPerBand;

                    x = 12;

                    Font fontRanges = new Font(null, Font.PLAIN, 14);
                    AffineTransform affineTransform = new AffineTransform();
                    affineTransform.rotate(Math.toRadians(90), 0, 0);
                    Font rotatedFont = fontRanges.deriveFont(affineTransform);
                    g2.setFont(rotatedFont);

                    for (int i = 0; i < bands.size() - 1; i++) {
                        g2.drawString(" (" + a + " - " + b + ")", x, h + 10);
                        a = b;
                        b += nLocPerBand;
                        x += deltax;
                    }

                    g2.drawString(" ( > " + a + ")", x, h + 10);


                }
	    		}

        }

        public void updateDistribution(List<ComputedFile> files, List<LongRange> ranges){
            this.files = files;
            this.ranges = ranges;
        	repaint();
        }

    }
    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.actorView.tell(new GUIMessageProtocol.StartDistributionMessage(this), ActorRef.noSender());
            this.setVisible(true);
        });
    }
}