package io.github.msyysoft.java.utiltools;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    // JGP格式
    public static final String JPG = "jpeg";
    // GIF格式
    public static final String GIF = "gif";
    // PNG格式
    public static final String PNG = "png";
    // BMP格式
    public static final String BMP = "bmp";

    /**
     * 按倍率缩小图片
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @param widthRatio   宽度缩小比例
     * @param heightRatio  高度缩小比例
     * @throws IOException
     */
    public static void reduceImageByRatio(String srcImagePath, String toImagePath, int widthRatio, int heightRatio) throws IOException {
        FileOutputStream out = null;
        try {
            //读入文件
            File file = new File(srcImagePath);
            // 构造Image对象
            BufferedImage src = javax.imageio.ImageIO.read(file);
            int width = src.getWidth();
            int height = src.getHeight();
            // 缩小边长
            BufferedImage tag = new BufferedImage(width / widthRatio, height / heightRatio, BufferedImage.TYPE_INT_RGB);
            // 绘制 缩小  后的图片
            tag.getGraphics().drawImage(src, 0, 0, width / widthRatio, height / heightRatio, null);
            out = new FileOutputStream(toImagePath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    /**
     * 按倍率缩小图片png透明不变黑
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @param widthRatio   宽度缩小比例
     * @param heightRatio  高度缩小比例
     * @throws IOException
     */
    public static void reduceImageByRatioPNG(String srcImagePath, String toImagePath, int widthRatio, int heightRatio) throws IOException {
        try {
            //读入文件
            File file = new File(srcImagePath);
            // 构造Image对象
            BufferedImage src = ImageIO.read(file);
            int width = src.getWidth();
            int height = src.getHeight();
            int newWidth = width / widthRatio;
            int newHeight = height / heightRatio;
            // 缩小边长
            BufferedImage to = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = to.createGraphics();
            to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight,Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = to.createGraphics();
            @SuppressWarnings("static-access")
            Image from = src.getScaledInstance(newWidth, newHeight, src.SCALE_AREA_AVERAGING);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newWidth, newHeight);
            g2d.drawImage(from, 0, 0, newWidth, newHeight,null);
            g2d.dispose();
            ImageIO.write(to, "png", new File(toImagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按尺寸缩小图片
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @param widthTarget  缩小目标宽度
     * @param heightTarget 缩小目标高度
     * @throws IOException
     */
    public static void reduceImageByTarget(String srcImagePath, String toImagePath, int widthTarget, int heightTarget) throws IOException {
        //读入文件
        File file = new File(srcImagePath);
        // 构造Image对象
        BufferedImage src = javax.imageio.ImageIO.read(file);
        int width = src.getWidth();
        int height = src.getHeight();
        int widthRatio = (int) (width * 1f / widthTarget);
        int heightRatio = (int) (height * 1f / heightTarget);
        int ratio = Math.max(widthRatio, heightRatio);
        reduceImageByRatio(srcImagePath, toImagePath, ratio, ratio);
    }


    /**
     * 按尺寸缩小图片
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @param widthTarget  缩小目标宽度
     * @param heightTarget 缩小目标高度
     * @throws IOException
     */
    public static void reduceImageByTargetPNG(String srcImagePath, String toImagePath, int widthTarget, int heightTarget) throws IOException {
        //读入文件
        File file = new File(srcImagePath);
        // 构造Image对象
        BufferedImage src = javax.imageio.ImageIO.read(file);
        int width = src.getWidth();
        int height = src.getHeight();
        int widthRatio = (int) (width * 1f / widthTarget);
        int heightRatio = (int) (height * 1f / heightTarget);
        int ratio = Math.max(widthRatio, heightRatio);
        reduceImageByRatioPNG(srcImagePath, toImagePath, ratio, ratio);
    }


    /**
     * 转换图片类型
     *
     * @param imgFile    原图片
     * @param format     图片格式
     * @param formatFile 新图片
     * @throws IOException
     */
    public static void converter(File imgFile, String format, File formatFile) throws IOException {
        // 构造Image对象
        BufferedImage src = javax.imageio.ImageIO.read(imgFile);
        int width = src.getWidth();
        int height = src.getHeight();
        // 缩小边长
        BufferedImage tag = new BufferedImage(width / 1, height / 1, BufferedImage.TYPE_INT_RGB);
        // 绘制 缩小  后的图片
        tag.getGraphics().drawImage(src, 0, 0, width / 1, height / 1, null);
        FileOutputStream out = new FileOutputStream(formatFile.toString());
        ImageIO.write(tag, format, out);
        out.flush();
        out.close();
        src.flush();
    }
}
