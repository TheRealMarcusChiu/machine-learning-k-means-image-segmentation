import util.ARGB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String [] args){
        if (args.length < 3){
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try{
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k=Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[0]));

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
        int w=originalImage.getWidth();
        int h=originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w,h , null);
        // Read rgb values from the image
        int[] rgb=new int[w*h];
        int count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                rgb[count++]=kmeansImage.getRGB(i,j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb,k);

        // Write the new rgb values to the image
        count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                kmeansImage.setRGB(i,j,rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
        // convert Integer to ARGB values
        ARGB[] argb = new ARGB[rgb.length];
        for (int i = 0; i < rgb.length; i++) {
            argb[i] = new ARGB(
                    (rgb[i] >> 24) & 0xFF,
                    (rgb[i] >> 16) & 0xFF,
                    (rgb[i] >>  8) & 0xFF,
                    (rgb[i]      ) & 0xFF
            );
        }

        kmeans(argb, k);

        // convert ARGB values to Integer
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = (argb[i].alpha << 24) | (argb[i].red << 16 ) | (argb[i].green<<8) | argb[i].blue;
        }
    }

    private static void kmeans(ARGB[] argb, int k){
        Random r = new Random();
        ARGB[] means = new ARGB[k];
        ArrayList<ARGB>[] groups = new ArrayList[k];
        for (int i = 0; i < k; i++) {
            means[i] = new ARGB(
                    r.nextInt(256),
                    r.nextInt(256),
                    r.nextInt(256),
                    r.nextInt(256)
            );
            groups[i] = new ArrayList();
        }

        boolean repeat = true;
        while (repeat) {
            repeat = false;

            // assign each pixel to closest mean
            for (int i = 0; i < argb.length; i++) {
                int minIndex = min(argb[i], means);
                groups[minIndex].add(argb[i]);
            }

            // compute average ARGB values of each group and update mean
            for (int i = 0; i < means.length; i++) {
                double alpha = 0;
                double red = 0;
                double green = 0;
                double blue = 0;

                for (ARGB a : groups[i]) {
                    alpha += a.alpha;
                    red += a.red;
                    green += a.green;
                    blue += a.blue;
                }

                alpha /= groups[i].size();
                red /= groups[i].size();
                green /= groups[i].size();
                blue /= groups[i].size();

                if ((int)alpha != means[i].alpha || (int)red != means[i].red || (int)green != means[i].green || (int)blue != means[i].blue) {
                    repeat = true;
                }

                means[i].alpha = (int)alpha;
                means[i].red = (int)red;
                means[i].green = (int)green;
                means[i].blue = (int)blue;
            }
        }

        for (int i = 0; i < means.length; i++) {
            for (ARGB a : groups[i]) {
                a.alpha = means[i].alpha;
                a.red = means[i].red;
                a.green = means[i].green;
                a.blue = means[i].blue;
            }
        }
    }

    private static int min(ARGB argb, ARGB[] means) {
        double minDistance = distance(argb, means[0]);
        int index = 0;
        for (int i = 1; i < means.length; i++) {
            double d = distance(argb, means[i]);
            if (minDistance > d) {
                minDistance = d;
                index = i;
            }
        }
        return index;
    }

    private static double distance(ARGB one, ARGB two) {
        double t = Math.pow(one.alpha - two.alpha, 2) +
                Math.pow(one.red - two.red, 2) +
                Math.pow(one.green - two.green, 2) +
                Math.pow(one.blue - two.blue, 2);
        return Math.sqrt(t);
    }
}
