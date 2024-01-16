package me.kefir.screenshotter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Frame extends JFrame {

    Pane pane;
    BufferedImage img = null;
    boolean visible = false;
    Point start, end;
    public Frame() {
        pane = new Pane();

        setTitle("Screenshotter");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new Color(0,0,0,0));
        setContentPane(pane);
        pane.setOpaque(false);
        setPaneVisible(false);
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        setIconImage(icon);
    }

    public void handleMouse(Point pos, int event) {
        if(event==0) { // mouse press
            start = pos;
            end = pos;
        } else if(event==1) { // mouse release
            int width = Math.abs(start.x-pos.x);
            int height = Math.abs(start.y-pos.y);
            BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int left = Math.min(start.x, pos.x);
            int top = Math.min(start.y, pos.y);
            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    i.setRGB(x, y, img.getRGB(left+x, top+y));
                }
            }

            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    DataFlavor[] flavors = new DataFlavor[1];
                    flavors[0] = DataFlavor.imageFlavor;
                    return flavors;
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    DataFlavor[] flavors = getTransferDataFlavors();
                    for(int i = 0; i < flavors.length; i++) if(flavor.equals(flavors[i])) return true;
                    return false;
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if(flavor.equals(DataFlavor.imageFlavor) && i != null) return i;
                    else throw new UnsupportedFlavorException( flavor );
                }
            }, null);
            setPaneVisible(false);
        } else if(event==2) { // mouse drag
            end = pos;
            pane.repaint();
        }
    }

    public void setPaneVisible(boolean v) {
        visible = v;
        if(v) try {
                img = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            } catch (Exception e) { e.printStackTrace(); }
        pane.repaint();
    }

    public boolean isPaneVisible() {
        return visible;
    }

    public class Pane extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            if(visible) {
                requestFocusInWindow();

                g.drawImage(img, 0, 0, null);

                g.setColor(new Color(0,0,0,150));
                if(start==null||end==null) {
                    g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
                } else {
                    int left = Math.min(start.x, end.x);
                    int top = Math.min(start.y, end.y);
                    int right = Math.max(start.x, end.x);
                    int bot = Math.max(start.y, end.y);

                    g.fillRect(left, bot, (int) dim.getWidth(), (int) dim.getHeight());
                    g.fillRect(0, 0, left, (int) dim.getHeight());
                    g.fillRect(left, 0, (int) dim.getWidth(), top);
                    g.fillRect(right, top, (int) dim.getWidth(), bot-top);
                }

            } else {
                img = null;
                g.clearRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
                g.setColor(Color.black);
                g.fillRect(0, 0, 10, 10);

                start = null;
                end = null;
            }
            g.dispose();
        }
    }
}
