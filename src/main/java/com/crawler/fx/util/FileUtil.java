package com.crawler.fx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private final static Set<String> MOVIE_TYPE = new HashSet<>();

    /**
     * 获取根路径
     * @return
     */
    public static String getRootPath() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取电影类型
     */
    public static Set<String> getMovieType() {
        if (MOVIE_TYPE.isEmpty()) {
            //默认格式
            MOVIE_TYPE.addAll(Arrays.asList("MP4", "AVI", "RMVB", "WMV", "MOV", "MKV", "FLV", "TS", "ISO"));
            String movieType = PropertiesUtil.getProperty("movie.type");
            if (!StringUtils.isEmpty(movieType)) {
                MOVIE_TYPE.addAll(Arrays.asList(movieType.split(",")));
            }
        }
        return MOVIE_TYPE;
    }

    public static String getFileType(File file) {
        return getFileType(file.getName());
    }

    public static String getFileType(String file) {
        int indexOf = file.lastIndexOf(".");
        if (indexOf != -1) {
            return file.substring(indexOf + 1).toUpperCase();
        } else {
            return null;
        }
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     */
    public static void downLoadFromUrl(String urlStr, File file) throws IOException {
        logger.info("开始下载文件：{}", urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = file.getParentFile();
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        logger.info("下载文件成功：{}", url);
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void subimage(File srcFile, File targetFile) throws IOException {
        logger.info("开始裁剪图片:{}", srcFile.getName());
        // 读取图片
        BufferedImage bufImage = ImageIO.read(srcFile);
        // 获取图片的宽高
        final int width = bufImage.getWidth();
        final int height = bufImage.getHeight();

        //裁剪图片
        int floor = (int) Math.floor(width / 1.9);
        BufferedImage subimage = bufImage.getSubimage(floor, 0, width - floor, height);

        // 把修改过的 bufImage 保存到本地
        ImageIO.write(subimage, "PNG", targetFile);
    }

    /**
     * 将图片转成base64
     * @param imgFile 图片地址
     * @return
     */
    public static String imageToBase64Str(String imgFile) throws IOException {
        return imageToBase64Str(new FileInputStream(imgFile));
    }

    public static String imageToBase64Str(InputStream inputStream) throws IOException {
        byte[] data = readInputStream(inputStream);
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 测试用，读文件
     *
     * @return
     */
    public static String readFile() {
        String pathname = "E:/SNIS-576.html";
        StringBuilder html = new StringBuilder();
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                html.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html.toString();
    }

    public static void main(String[] args) throws IOException {
//        subimage(new File("D:\\image\\SSNI-557.jpg"), new File("D:\\image\\SSNI-557.png"));
    }
}
