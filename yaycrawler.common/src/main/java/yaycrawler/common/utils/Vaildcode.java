package yaycrawler.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Vaildcode {
    BufferedImage image;
    private int iw, ih;
    // 设定二值化的域值，默认值为100
    private int grey = 110;

    private int[] pixels;

    public Vaildcode(BufferedImage image) {
        this.image = image;
        iw = image.getWidth();
        ih = image.getHeight();
        pixels = new int[iw * ih];
        init();
    }

    public void init() {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setGrey(int g) {
        grey = g;
    }

    public BufferedImage changeGrey() {
// 对图像进行二值化处理，Alpha值保持不变
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 0; i < iw * ih; i++) {
            int red, green, blue;
            int alpha = cm.getAlpha(pixels[i]);
            if (cm.getRed(pixels[i]) > grey) {
                red = 255;
            } else {
                red = 0;
            }
            if (cm.getGreen(pixels[i]) > grey) {
                green = 255;
            } else {
                green = 0;
            }
            if (cm.getBlue(pixels[i]) > grey) {
                blue = 255;
            } else {
                blue = 0;
            }
            alpha = 255;
            pixels[i] = alpha << 24 | red << 16 | green << 8 | blue; //通过移位重新构成某一点像素的RGB值
        }
// 将数组中的象素产生一个图像
        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;
    }

    public int printImage() {
        long beginTime = System.currentTimeMillis();
        HashMap<String, Integer> hash = new HashMap<String, Integer>();
        for (int i = 0; i < pixels.length; i++) {
            try {
                if (!hash.isEmpty() && hash.containsKey(pixels[i]+"")) {
                    hash.put(pixels[i]+"", hash.get(pixels[i]+"") + 1);
                } else {
                    hash.put(pixels[i]+"", 1);
                }
            } catch (Exception e) {

            }
        }
        Map ha = sortMap(hash);
        printMap(ha,55);
        long endTime = System.currentTimeMillis();
        System.out.println("left time " + (endTime - beginTime));
        return printMap(ha,5);
    }

    private  int printMap(Map map,int num){
        System.out.println("===================mapStart==================");
        Iterator it = map.entrySet().iterator();
        int i = 0;
        int max = 0;
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            i++;
            if(i == num) {
                max =  Integer.parseInt(entry.getKey().toString());
                break;
            }
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("===================mapEnd==================");
        return max;
    }

    public static Map sortMap(Map oldMap) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> arg0,
                               Map.Entry<String, Integer> arg1) {
                return arg1.getValue() - arg0.getValue();
            }
        });
        Map newMap = new LinkedHashMap();
        for (int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;
    }

    //以九宫格的方式判断干扰点，并以底色替换去除
    public BufferedImage filteNoise(){
        for (int x = 0; x < iw; x++) {
            for(int y = 0; y < ih; y++) {
                int count=0;
                boolean[] m = new boolean[9];
                int clr = pixels[y * iw + x];
                if (clr == -1) continue;
                //  1   2   3
                //  4   5   6
                //  7   8   9
                for(int i = 0; i < 9; i++){
                    m[i] = checkSame(clr,(x-1)+(i/3),(y-1)+(i%3));
                    if(m[i])count++;
                }
                switch(count) {
                    case 1 :
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                    case 2 :
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                    case 3 :
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                }
                if(count<4) {
                    //System.out.println("color:"+clr+" x:"+x+" y:"+y+" m:"+mtos(m)+" c:"+count);
                }
            }
        }

// 将数组中的象素产生一个图像
        Image tempImg=Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw,ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null),tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR );
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;
    }
    //以九宫格的方式判断干扰点，并以底色替换去除
    public BufferedImage filteNoise(int clr) {
        for (int x = 0; x < iw; x++) {
            for (int y = 0; y < ih; y++) {
                int count = 0;
                boolean[] m = new boolean[9];
                if (clr == -1) continue;
                //  1   2   3
                //  4   5   6
                //  7   8   9
                System.out.print(pixels[y*iw+x]);
                if(pixels[y*iw +x ] != clr) {
                    pixels[y*iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                }
                for (int i = 0; i < 9; i++) {
                    m[i] = checkSame(clr, (x - 1) + (i / 3), (y - 1) + (i % 3));
                    if (m[i]) pixels[y * iw + x] = -4387532;
                }
                switch (count) {
                    case 1:
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                    case 2:
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                    case 3:
                        pixels[y * iw + x] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
                        break;
                }
                if (count < 4) {
//                    System.out.println("color:"+clr+" x:"+x+" y:"+y+" m:"+mtos(m)+" c:"+count);
                }
            }
            System.out.println();
        }

// 将数组中的象素产生一个图像
        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;
    }

    public String mtos(boolean[] m) {
        String s = "";
        for (int im = 0; im < m.length; im++) s += m[im] + ",";
        return s;
    }

    public boolean checkSame(int color,int x,int y){
        boolean check = false;
        try {
            if ((x < 0 || y < 0 || x >= iw || y >= ih)) {
                check = false;
            } else {
                check = (pixels[y * iw + x] == color ? true : false);
            }
        }catch(Exception e){
            System.out.println("iw:" + iw + "  ih:" + ih + "  x:" + x + "  y:" + y);
        }
        return check;
    }

    //找到从begin开始第一个非背景点的x轴，返回 -1 则没找到
    public int[] findLeft(int begin) {
        int[] left = {-1, -1};
        boolean found = false;
        for (int x = begin; x < iw; x++) {
            for (int y = 0; y < ih; y++) {
                if (pixels[y * iw + x] != -1) {
                    found = true;
                    left[0] = x;
                    left[1] = pixels[y * iw + x];
                    break;
                }
            }
            if (found) break;
        }
        return left;
    }

    //从begin[0]开始，往右找到第一个非begin[1]值的x轴
    public int findRight(int[] begin) {
        int right = -1;
        boolean found = false;
        if (begin[0] != -1) {
            int x = 0;
            for (x = begin[0]; x < iw; x++) {
                found = false;
                for (int y = 0; y < ih; y++) {
                    if (pixels[y * iw + x] == begin[1]) {
                        found = true;
                        break;
                    }
                }
                if (!found) break;
            }
            if (x < iw) right = x;
        }
        return right;
    }

    public boolean findWord(int begin, String wordFile) {
        boolean found = false;
        int[] wordPixel;
        int r = -1;
        int[] l = findLeft(begin);
        if (l[0] < iw) {
            r = findRight(l);
            if (r > 0) {
                wordPixel = copyWord(l[0], r);
                pixels = fillColor(pixels, l[1], -1, l[0], r);
                image = pixels2Image(pixels, iw, ih);
                wordPixel = fillColor(wordPixel, l[1], -1);
                saveFile(pixels2Image(wordPixel, r - l[0] + 1, ih), wordFile, "bmp");
                found = true;
            }
        }

        return found;
    }

    public BufferedImage pixels2Image(int[] pix, int ww, int wh) {
        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(ww, wh, pix, 0, ww));
        BufferedImage img = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        img.createGraphics().drawImage(tempImg, 0, 0, null);
        return img;
    }

    public int countMostColor() {
        int[][] count = new int[iw][2];
        int max = 0;
        for (int n = 0; n < iw; n++) {
            count[n][0] = pixels[n];
            count[n][1] = 1;
        }
        for (int n = 0; n < iw; n++) {
            if (count[n][1] == 0) {
                continue;
            } else {
                for (int m = n + 1; m < iw; m++) {
                    if (count[n][0] == count[m][0]) {
                        count[n][1] += count[m][1];
                        count[m][1] = 0;
                    }
                }
            }
            if (count[max][1] < count[n][1]) max = n;
        }
        return count[max][0];
    }

    public int[] copyWord(int left, int right) {
        int[] word = new int[(right - left + 1) * ih];
        for (int j = 0; j < ih; j++) {
            for (int i = left; i <= right; i++) {
                word[j * (right - left + 1) + (i - left)] = pixels[i + j * iw];
            }
        }
        return word;
    }

    public int[] fillColor(int[] pix, int targetColor, int fillColor, int left, int right) {
        for (int y = 0; y < ih; y++) {
            for (int x = left; x <= right; x++)
                if (pix[y * iw + x] == targetColor) pix[y * iw + x] = fillColor;
        }
        return pix;
    }

    public int[] fillColor(int[] pix, int saveColor, int fillColor) {
        for (int i = 0; i < pix.length; i++) {
            if (pix[i] != saveColor) pix[i] = fillColor;
        }
        return pix;
    }

    public BufferedImage fixBackground() {
        int bg = countMostColor();
        for (int i = 0; i < iw * ih; i++) {
            if (pixels[i] == bg) pixels[i] = -1;
        }

        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;
    }

    public BufferedImage getMedian() {
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
                pixels,
                0, iw);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
// 对图像进行中值滤波，Alpha值保持不变
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 1; i < ih - 1; i++) {
            for (int j = 1; j < iw - 1; j++) {
                int red, green, blue;
                int alpha = cm.getAlpha(pixels[i * iw + j]);
// int red2 = cm.getRed(pixels[(i - 1) * iw + j]);
                int red4 = cm.getRed(pixels[i * iw + j - 1]);
                int red5 = cm.getRed(pixels[i * iw + j]);
                int red6 = cm.getRed(pixels[i * iw + j + 1]);
// int red8 = cm.getRed(pixels[(i + 1) * iw + j]);
// 水平方向进行中值滤波
//                if(red2 >= red4) {
                if (red4 >= red5) {
                    if (red5 >= red6) {
                        red = red5;
                    } else {
                        if (red4 >= red6) {
                            red = red6;
                        } else {
                            red = red4;
                        }
                    }
                } else {
                    if (red4 > red6) {
                        red = red4;
                    } else {
                        if (red5 > red6) {
                            red = red6;
                        } else {
                            red = red5;
                        }
                    }
                }
                int green4 = cm.getGreen(pixels[i * iw + j - 1]);
                int green5 = cm.getGreen(pixels[i * iw + j]);
                int green6 = cm.getGreen(pixels[i * iw + j + 1]);
// 水平方向进行中值滤波
                if (green4 >= green5) {
                    if (green5 >= green6) {
                        green = green5;
                    } else {
                        if (green4 >= green6) {
                            green = green6;
                        } else {
                            green = green4;
                        }
                    }
                } else {
                    if (green4 > green6) {
                        green = green4;
                    } else {
                        if (green5 > green6) {
                            green = green6;
                        } else {
                            green = green5;
                        }
                    }
                }
// int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
                int blue4 = cm.getBlue(pixels[i * iw + j - 1]);
                int blue5 = cm.getBlue(pixels[i * iw + j]);
                int blue6 = cm.getBlue(pixels[i * iw + j + 1]);
// int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);
// 水平方向进行中值滤波
                if (blue4 >= blue5) {
                    if (blue5 >= blue6) {
                        blue = blue5;
                    } else {
                        if (blue4 >= blue6) {
                            blue = blue6;
                        } else {
                            blue = blue4;
                        }
                    }
                } else {
                    if (blue4 > blue6) {
                        blue = blue4;
                    } else {
                        if (blue5 > blue6) {
                            blue = blue6;
                        } else {
                            blue = blue5;
                        }
                    }
                }
                pixels[i * iw +
                        j] = alpha << 24 | red << 16 | green << 8 | blue;
            }
        }
// 将数组中的象素产生一个图像
        Image tempImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw, ih, pixels, 0, iw));
        image = new BufferedImage(tempImg.getWidth(null), tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
        image.createGraphics().drawImage(tempImg, 0, 0, null);
        return image;
    }

    public BufferedImage getGrey() {
        ColorConvertOp ccp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return image = ccp.filter(image, null);
    }

    //Brighten using a linear formula that increases all color values
    public BufferedImage getBrighten() {
        RescaleOp rop = new RescaleOp(1.25f, 0, null);
        return image = rop.filter(image, null);
    }

    //Blur by "convolving" the image with a matrix
    public BufferedImage getBlur() {
        float[] data = {
                .1111f, .1111f, .1111f,
                .1111f, .1111f, .1111f,
                .1111f, .1111f, .1111f,};
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image, null);
    }

    // Sharpen by using a different matrix
    public BufferedImage getSharpen() {
        float[] data = {
                0.0f, -0.75f, 0.0f,
                -0.75f, 4.0f, -0.75f,
                0.0f, -0.75f, 0.0f};
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
        return image = cop.filter(image, null);
    }

    // 11) Rotate the image 180 degrees about its center point
    public BufferedImage getRotate(int theta) {
        AffineTransformOp atop = new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI / 180 * theta, image.getWidth() / 2, image.getHeight() / 2),
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return image = atop.filter(image, null);
    }

    public BufferedImage getProcessedImg() {
        return image;
    }

    public String saveFile(BufferedImage img, String fileName, String fileType) {
        String pname = "";
        try {
            pname = fileName.substring(0, fileName.lastIndexOf("."));
            pname = pname + "." + fileType;
            File file = new File(pname);
            ImageIO.write(img, fileType, file);     //保存文件为bmp格式
        } catch (Exception e) {
            System.out.println(e);
        }
        return pname;
    }

    public static void main(String[] args) throws IOException {

        File testDataDir = new File("d:/tmp/ocr");
        final String destDir = testDataDir.getAbsolutePath() + "/tmp3";
        File destF = new File(destDir);
        if (!destF.exists()) {
            destF.mkdirs();
        }

        for (File file : testDataDir.listFiles()) {
            FileInputStream fin = new FileInputStream(file);
            BufferedImage bi = ImageIO.read(fin);
            Vaildcode flt = new Vaildcode(bi);
            flt.fixBackground();    //去除底色
            int clr = flt.printImage();
//            flt.filteNoise(clr);   //第二次去除干扰
            flt.changeGrey();   //二值化，单色化
            flt.getGrey();    //转换为灰度
//            flt.getBrighten();
//            flt.getMedian();
        /*
        int w = 1;
        while(flt.findWord(0,"C:\\Users\\max\\Pictures\\word"+w+".bmp")){
            w++;
        }
        */
//            flt.getProcessedImg();
//            flt.getSharpen();
//            flt.getMedian();
//            flt.getRotate(-30);
//            flt.getBrighten();
//            flt.filteNoise();
            //flt.getRotate(-30);
            //bi=flt.getProcessedImg();
            flt.saveFile(flt.getProcessedImg(), destDir + "/" + file.getName(), "jpg");
        }

    }
}