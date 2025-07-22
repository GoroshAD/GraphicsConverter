package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private Integer maxWidth = null;
    private Integer maxHeight = null;
    private Double maxRatio = null;
    private TextColorSchema schema;

    private void ratioChecker(BufferedImage img) throws BadImageSizeException  {
        // Проверяем соотношения высоты и ширины и в плохом случае выбрасываем исключение.
        if (this.maxRatio == null) {
            return;
        }
        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();
        double imgRatio = (double) Math.max(imgWidth,imgHeight) / Math.min(imgWidth,imgHeight);
        if (imgRatio > this.maxRatio) {
            throw new BadImageSizeException(imgRatio, this.maxRatio);
        }
    }

    private double decreasingTimes(BufferedImage img) {
        // Возвращает значение, в которое нужно уменьшить изображение.
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        double times = 1;
        if (this.maxWidth != null) {
            times = Math.max((double) imgWidth / maxWidth, times);
        }
        if (this.maxHeight != null) {
            times = Math.max((double) imgHeight / maxHeight, times);
        }
        return times;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        ratioChecker(img);
        double times = decreasingTimes(img);
        int newWidth = (int) Math.floor(img.getWidth() / times); // изображение не будет размером больше чем инт на инт.
        int newHeight = (int) Math.floor(img.getHeight() / times);
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        StringBuilder resString = new StringBuilder();

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();
        setTextColorSchema(new Schema());
        for (int h = 0; h < newHeight; ++h) {
            for (int w = 0; w < newWidth; ++w) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = this.schema.convert(color);
                resString.append(c);
            }
            resString.append("\n");
        }

        return resString.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}

