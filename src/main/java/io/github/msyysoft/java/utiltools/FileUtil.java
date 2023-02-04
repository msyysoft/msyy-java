package io.github.msyysoft.java.utiltools;

import org.apache.commons.lang.time.DateUtils;

import java.io.*;
import java.util.Date;
import java.util.List;

public class FileUtil extends org.apache.commons.io.FileUtils {

    /**
     * 检查是否创建了文件夹
     *
     * @param dirFile
     * @return
     * @throws IOException
     */
    public static File checkMkdir(File dirFile) throws IOException {
        if (dirFile != null && !dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }

    /**
     * 保存对象到文件
     *
     * @param obj
     * @param file
     * @throws IOException
     */
    public static void saveObjToFile(Object obj, File file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * 从文件恢复对象
     *
     * @param file
     * @throws IOException
     */
    public static Object getObjFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object obj = ois.readObject();
        return obj;
    }

    /**
     * 递归筛选若干天内的文件
     *
     * @param items 存放查找的文件
     * @param file  要查询的文件夹
     */
    public static void filterFilesByDays(List<File> items, File file, int beforeInDays) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                filterFilesByDays(items, f, beforeInDays);
            }
        } else if (file.isFile()) {
            Date beforeDate = DateUtils.addDays(new Date(), -beforeInDays);
            if (file.lastModified() > beforeDate.getTime())
                items.add(file);
        }
    }

    /**
     * 获取user.dir目录并可以无限添加子目录
     * @param child
     * @return
     * @throws IOException
     */
    public static File getUserDirWithChild(String... child) throws IOException {
        File retPath = new File(System.getProperty("user.dir"));
        if (child != null) {
            for (String c : child) {
                retPath = new File(retPath, c);
            }
        } else {
            return new File(System.getProperty("user.dir"));
        }
        FileUtil.checkMkdir(retPath);
        return retPath;
    }

    /**
     * inputStream 2 string
     *
     * @param inputStream inputStream
     * @param charsetName charsetName,default is "UTF-8"
     * @return string
     */
    public static String getStringFromInputStream(InputStream inputStream, String charsetName) {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        if (charsetName == null) {
            charsetName = "UTF-8";
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, charsetName);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 保存String到本地文件
     *
     * @param content
     * @param filePath
     */
    public static void saveAsFileWriter(String content, String filePath) {

        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(filePath);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
