package com.roland.bang.core;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Utils {
    public static boolean has(int[] array, int n) {
        for (int num : array) {
            if (num == n) return true;
        }
        return false;
    }

    public static boolean range(int i, int low, int high) {
        return (i >= low && i <= high);
    }


    public static BufferedImage scaleImage(BufferedImage img, int width, int height,
                                           Color background) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth*height < imgHeight*width) {
            width = imgWidth*height/imgHeight;
        } else {
            height = imgHeight*width/imgWidth;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }
}
