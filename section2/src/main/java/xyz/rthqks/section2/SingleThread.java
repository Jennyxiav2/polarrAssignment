package xyz.rthqks.section2;

import android.graphics.Bitmap;

public class SingleThread {

    public int[][] histogram(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] rHistogram = new int[256];
        int[] gHistogram = new int[256];
        int[] bHistogram = new int[256];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = bitmap.getPixel(j, i);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                rHistogram[red] += 1;
                gHistogram[green] += 1;
                bHistogram[blue] += 1;

            }
        }
        return new int[][] {rHistogram, gHistogram, bHistogram};
    }
}
