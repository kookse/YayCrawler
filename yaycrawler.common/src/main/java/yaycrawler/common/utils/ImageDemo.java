package yaycrawler.common.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * @author bill
 * @create 2017-08-21 10:09
 * @desc 二值化处理
 **/

public class ImageDemo {

    private static final Logger logger  = LoggerFactory.getLogger(ImageDemo.class);

    public void binaryImage() throws IOException{
        File testDataDir = new File("d:/tmp/ocr");
        final String destDir = testDataDir.getAbsolutePath()+"/tmp4";
        for (File file : testDataDir.listFiles()) {
            BufferedImage image = ImageIO.read(file);

            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(i, j);
                    grayImage.setRGB(i, j, rgb);
                }
            }

            File newFile = new File(destDir,file.getName());
            ImageIO.write(grayImage, "jpg", newFile);
        }
    }

    public void grayImage() throws IOException{
        File testDataDir = new File("d:/tmp/ocr");
        final String destDir = testDataDir.getAbsolutePath()+"/tmp5";
        for (File file : testDataDir.listFiles()) {
            BufferedImage image = ImageIO.read(file);

            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(i, j);
                    grayImage.setRGB(i, j, rgb);
                }
            }

            File newFile = new File(destDir,file.getName());
            ImageIO.write(grayImage, "jpg", newFile);
        }
    }

    public static void main(String[] args) throws IOException {
        ImageDemo demo = new ImageDemo();
        demo.binaryImage();
        demo.grayImage();
    }

}