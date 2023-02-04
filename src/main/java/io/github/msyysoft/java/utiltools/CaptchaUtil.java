package io.github.msyysoft.java.utiltools;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 验证码生成器
 *
 * @author llq
 */
public class CaptchaUtil {

    /**
     * 验证码来源
     */
    final private char[] code = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    /**
     * 字体
     */
    final private String[] fontNames = new String[]{
            "黑体", "宋体", "Courier", "Arial",
            "Verdana", "Times", "Tahoma", "Georgia"};
    /**
     * 字体样式
     */
    final private int[] fontStyles = new int[]{
            Font.BOLD, Font.ITALIC | Font.BOLD
    };

    /**
     * 验证码长度
     * 默认4个字符
     */
    private int captchaLen = 4;
    /**
     * 验证码图片字体大小
     * 默认17
     */
    private int fontsize = 21;
    /**
     * 验证码图片宽度
     */
    private int width = (fontsize + 1) * captchaLen + 10;
    /**
     * 验证码图片高度
     */
    private int height = fontsize + 12;
    /**
     * 干扰线条数
     * 默认3条
     */
    private int disturbLine = 3;


    public CaptchaUtil() {
    }

    /**
     * 指定验证码长度
     *
     * @param captchaLen 验证码长度
     */
    public CaptchaUtil(int captchaLen) {
        this.captchaLen = captchaLen;
        this.width = (fontsize + 1) * captchaLen + 10;
    }

    /**
     * 指定验证码长度、图片宽度、高度
     *
     * @param captchaLen
     * @param width
     * @param height
     */
    public CaptchaUtil(int captchaLen, int width, int height) {
        this.captchaLen = captchaLen;
        this.width = width;
        this.height = height;
    }

    /**
     * 生成验证码图片
     *
     * @param captcha    要画的验证码
     * @param drawline 是否画干扰线
     * @return
     */
    public BufferedImage generatorCaptchaImage(String captcha, boolean drawline) {
        //创建验证码图片
        BufferedImage captchaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = captchaImage.getGraphics();
        //填充背景色
        g.setColor(new Color(246, 240, 250));
        g.fillRect(0, 0, width, height);
        if (drawline) {
            drawDisturbLine(g);
        }
        //用于生成伪随机数
        Random ran = new Random();
        //在图片上画验证码
        for (int i = 0; i < captcha.length(); i++) {
            //设置字体
            g.setFont(new Font(fontNames[ran.nextInt(fontNames.length)], fontStyles[ran.nextInt(fontStyles.length)], fontsize));
            //随机生成颜色
            g.setColor(getRandomColor());
            //画验证码
            g.drawString(captcha.charAt(i) + "", i * fontsize + 10, fontsize + 5);
        }
        //释放此图形的上下文以及它使用的所有系统资源
        g.dispose();

        return captchaImage;
    }

    /**
     * 获得旋转字体的验证码图片
     *
     * @param captcha
     * @param drawline 是否画干扰线
     * @return
     */
    public BufferedImage generatorRotateCaptchaImage(String captcha, boolean drawline) {
        //创建验证码图片
        BufferedImage rotateCaptchaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotateCaptchaImage.createGraphics();
        //填充背景色
        g2d.setColor(new Color(246, 240, 250));
        g2d.fillRect(0, 0, width, height);
        if (drawline) {
            drawDisturbLine(g2d);
        }
        //在图片上画验证码
        for (int i = 0; i < captcha.length(); i++) {
            BufferedImage rotateImage = getRotateImage(captcha.charAt(i));
            g2d.drawImage(rotateImage, null, (int) (this.height * 0.7) * i, 0);
        }
        g2d.dispose();
        return rotateCaptchaImage;
    }

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    public String generatorCaptcha() {
        int len = code.length;
        Random ran = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < captchaLen; i++) {
            int index = ran.nextInt(len);
            sb.append(code[index]);
        }
        return sb.toString();
    }

    /**
     * 为验证码图片画一些干扰线
     *
     * @param g
     */
    private void drawDisturbLine(Graphics g) {
        Random ran = new Random();
        for (int i = 0; i < disturbLine; i++) {
            int x1 = ran.nextInt(width);
            int y1 = ran.nextInt(height);
            int x2 = ran.nextInt(width);
            int y2 = ran.nextInt(height);
            g.setColor(getRandomColor());
            //画干扰线
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 获取一张旋转的图片
     *
     * @param c 要画的字符
     * @return
     */
    private BufferedImage getRotateImage(char c) {
        BufferedImage rotateImage = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotateImage.createGraphics();
        //设置透明度为0
        g2d.setColor(new Color(255, 255, 255, 0));
        g2d.fillRect(0, 0, height, height);
        Random ran = new Random();
        g2d.setFont(new Font(fontNames[ran.nextInt(fontNames.length)], fontStyles[ran.nextInt(fontStyles.length)], fontsize));
        g2d.setColor(getRandomColor());
        double theta = getTheta();
        //旋转图片
        g2d.rotate(theta, height / 2, height / 2);
        g2d.drawString(Character.toString(c), (height - fontsize) / 2, fontsize + 5);
        g2d.dispose();

        return rotateImage;
    }

    /**
     * @return 返回一个随机颜色
     */
    private Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(220), ran.nextInt(220), ran.nextInt(220));
    }

    /**
     * @return 角度
     */
    private double getTheta() {
        return ((int) (Math.random() * 1000) % 2 == 0 ? -1 : 1) * Math.random();
    }

    /**
     * @return 验证码字符个数
     */
    public int getCaptchaLen() {
        return captchaLen;
    }

    /**
     * 设置验证码字符个数
     *
     * @param captchaLen
     */
    public void setCaptchaLen(int captchaLen) {
        this.width = (fontsize + 3) * captchaLen + 10;
        this.captchaLen = captchaLen;
    }

    /**
     * @return 字体大小
     */
    public int getFontsize() {
        return fontsize;
    }

    /**
     * 设置字体大小
     *
     * @param fontsize
     */
    public void setFontsize(int fontsize) {
        this.width = (fontsize + 3) * captchaLen + 10;
        this.height = fontsize + 15;
        this.fontsize = fontsize;
    }

    /**
     * @return 图片宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 设置图片宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return 图片高度
     */
    public int getHeight() {
        return height;
    }

    /**
     * 设置图片高度
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return 干扰线条数
     */
    public int getDisturbLine() {
        return disturbLine;
    }

    /**
     * 设置干扰线条数
     *
     * @param disturbLine
     */
    public void setDisturbLine(int disturbLine) {
        this.disturbLine = disturbLine;
    }

    /**
     * 直接调用这个方法
     *
     * @param captchaLen
     * @param width
     * @param height
     * @param captchaType
     * @param request
     * @param response
     */
    public static void generateCaptcha(Integer captchaLen, Integer width, Integer height, String captchaType,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        CaptchaUtil captchaUtil = new CaptchaUtil(captchaLen, width, height);
        String generatorCaptcha = captchaUtil.generatorCaptcha();
        request.getSession().setAttribute(captchaType, generatorCaptcha);
        BufferedImage generatorRotateCaptchaImage = captchaUtil.generatorRotateCaptchaImage(generatorCaptcha, true);
        ImageIO.write(generatorRotateCaptchaImage, "gif", response.getOutputStream());
    }
}
