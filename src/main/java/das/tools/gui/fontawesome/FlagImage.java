package das.tools.gui.fontawesome;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class FlagImage {
    public static ImageView getFlagImage(Color fxColor1, Color fxColor2) {
        return new ImageView(SwingFXUtils.toFXImage(createFlag(convertColor(fxColor1), convertColor(fxColor2)), null));
    }

    private static java.awt.Color convertColor(Color fxColor) {
        return new java.awt.Color(
                (float) fxColor.getRed(),
                (float) fxColor.getGreen(),
                (float) fxColor.getBlue(),
                (float) fxColor.getOpacity());
    }
    private static BufferedImage createFlag(java.awt.Color color1, java.awt.Color color2) {
        int width = 14, height = 14;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        drawRect(g2d, width, height, color1, color2);
        return bi;
    }

    private static void drawRect(Graphics2D g2d, int w, int h, java.awt.Color c1, java.awt.Color c2) {
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, w-1, h-1);
        g2d.setColor(java.awt.Color.GRAY);
        g2d.fillRect(1, 1, w, h);
        g2d.setColor(c1);
        g2d.fill(getUpTriangle(w, h));
        g2d.setColor(c2);
        g2d.fill(getDnTriangle(w, h));
    }

    private static Path2D getUpTriangle(int w, int h) {
        Path2D path = new Path2D.Double();
        path.moveTo(1, 1);
        path.lineTo(w-1, 1);
        path.lineTo(1, h-1);
        path.lineTo(1, 1);
        path.closePath();
        return path;
    }

    private static Path2D getDnTriangle(int w, int h) {
        Path2D path = new Path2D.Double();
        path.moveTo(w-1, h-1);
        path.lineTo(1, h-1);
        path.lineTo(w-1, 1);
        path.lineTo(w-1, h-1);
        path.closePath();
        return path;
    }
}
