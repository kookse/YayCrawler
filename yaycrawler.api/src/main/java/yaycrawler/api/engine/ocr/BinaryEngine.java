package yaycrawler.api.engine.ocr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yaycrawler.api.engine.Engine;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.thread.DynamicThreadPoolExecutorMaintainer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bill
 * @create 2017-08-29 10:40
 * @desc 二值化引擎
 **/
@Service("binaryEngine")
public class BinaryEngine implements Engine<BinaryDto> {

    private static final Logger logger = LoggerFactory.getLogger(BinaryEngine.class);

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private final static int MAXFAILURETIMES = 3;
    /**
     * 调度任务线程池
     */
    private final ThreadPoolExecutor XZ_EXECUTOR_POOL =
            DynamicThreadPoolExecutorMaintainer.get(BinaryEngine.class.getName(), 32);


    @Override
    public EngineResult execute(BinaryDto info) {
        File destF = new File(info.getDest());
        if (!destF.exists()) {
            destF.mkdirs();
        }
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<BinaryDto> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(BinaryDto info) {
        EngineResult engineResult = new EngineResult();
        try {
            String img = info.getImg();
            String[] paramArray = img.split("\\$\\$");
            String srcFileName = String.format("%s/%s",info.getSrc(),paramArray[0]);
            info.setSw(Integer.parseInt(paramArray[1]));
            info.setTopMin(Integer.parseInt(paramArray[2]));
            info.setTopMax(Integer.parseInt(paramArray[3]));
            File file = new File(srcFileName);
            BufferedImage bi = ImageIO.read(file);//通过imageio将图像载入
            int h = bi.getHeight();//获取图像的高
            int w = bi.getWidth();//获取图像的宽
            BufferedImage nbi = getBufferedImage(bi, h, w, info);
            printGrayRGB(h, w, nbi);
            ImageIO.write(nbi, "jpg", new File(info.getDest(), file.getName()));
            engineResult.setStatus(Boolean.TRUE);
            engineResult.setResult(file.getName());
        } catch (Exception e) {
            engineResult = failureCallback(info,e);
            e.printStackTrace();
        }
        return engineResult;
    }

    private BufferedImage getBufferedImage(BufferedImage bi, int h, int w, BinaryDto info) {
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = getGray(bi.getRGB(x, y), info.getTopMin(), info.getTopMax());
            }
        }

        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        int SW = info.getSw();
        int pw;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pw = getAverageColor(gray, x, y, w, h);
                if (pw > SW) {
                    int max = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, max);
                } else {
                    int min = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, min);
                }
            }
        }
        return nbi;
    }

    private void printGrayRGB(int h, int w, BufferedImage nbi) {
        // 矩阵打印
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isBlack(nbi.getRGB(x, y))) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    private int getGray(int rgb, int min, int max) {
        String str = Integer.toHexString(rgb);
        int r = Integer.parseInt(str.substring(2, 4), 16);
        int g = Integer.parseInt(str.substring(4, 6), 16);
        int b = Integer.parseInt(str.substring(6, 8), 16);
        //or 直接new个color对象
        Color c = new Color(rgb);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
        int top = (r + g + b) / 3;
        if (top <= min || top >= max) {
            return 255;
        }
        return (int) (top);
    }

    /**
     * 自己加周围8个灰度值再除以9，算出其相对灰度值
     *
     * @param gray
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    private int getAverageColor(int[][] gray, int x, int y, int w, int h) {
        int rs = gray[x][y]
                + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1])
                + (x == w - 1 ? 255 : gray[x + 1][y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }
}
