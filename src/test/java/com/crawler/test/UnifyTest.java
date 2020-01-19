package com.crawler.test;

import com.crawler.fx.bean.FileInfo;
import com.crawler.fx.parse.Fc2Parse;
import com.crawler.fx.parse.JavBusParse;
import com.crawler.fx.util.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class UnifyTest {

    private final Logger log = LoggerFactory.getLogger(UnifyTest.class);

    @Before
    public void before() {
        //读取配置文件
        Properties classPathProperties = null;
        try {
            classPathProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        } catch (IOException e) {
        }
        PropertiesUtil.putAll(classPathProperties);
    }

    @Test
    public void moveFileTest() throws IOException {
        MoveFileUtil.moveMovieFile("C:\\move", "C:\\move-copy");
    }

    @Test
    public void createNfoTest() throws IOException {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setNumber("SNN");
        fileInfo.setTitle("测试中文编码,テストコード");
        InfoUtil.createInfo(fileInfo, new File("E:/test.nfo"));
    }

    @Test
    public void loadPropertiesTest() throws IOException {
        String path = System.getProperty("user.dir");
        Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(path + File.separator + "application.properties"));
        System.out.println(properties.isEmpty());
    }

    @Test
    public void javBusTest() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("SNIS-576");
        JavBusParse javBusParse = new JavBusParse();
        javBusParse.parse(fileInfo);
    }

    @Test
    public void fc2Test() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("SNIS-576");
        Fc2Parse fc2Parse = new Fc2Parse();
        fc2Parse.parse(fileInfo);
    }

    @Test
    public void patternTest() {
        String fileName = "IPX-023-A";
        System.out.println(!FileNameRegexUtil.isMultiFileOther(fileName) && FileNameRegexUtil.isNumberAndString(fileName) && !FileNameRegexUtil.isFc2(fileName));
    }

    /**
     * 更新用户图片
     */
    @Test
    public void updateImageTest() throws IOException {
        String ip = "192.168.0.200";
        String apiKey = "81f5ea155d3a4dbc871e066043f0724a";
        String id = "213";
        RestTemplate restTemplate = new RestTemplate();
        String image = FileUtil.imageToBase64Str("E:\\2t4_a.jpg");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        HttpEntity<String> entity = new HttpEntity<>(image, headers);
        restTemplate.exchange(String.format("http://%s:8096/emby/Items/%s/Images/Primary?api_key=%s", ip, id, apiKey), HttpMethod.POST, entity, String.class);
    }


    /**
     * 自动上传图片
     *
     * @throws IOException
     */
    @Test
    public void personsTest() {
        String ip = "192.168.0.200";
        String apiKey = "81f5ea155d3a4dbc871e066043f0724a";
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("http://%s:8096/emby/Persons??api_key=%s", ip, apiKey);
        Map response = restTemplate.getForObject(url, Map.class);
        Object items = response.get("Items");
        if (items != null) {
            for (Map item : (List<Map>) items) {
                String name = String.valueOf(item.get("Name"));
                String id = String.valueOf(item.get("Id"));
                Object imageTags = item.get("ImageTags");
                if (imageTags != null) {
                    Map imageTagsMap = (Map) imageTags;
                    if (!StringUtils.isEmpty(imageTagsMap.get("Primary"))) {
                        log.warn("[{}]改演员已经存在照片", name);
                        continue;
                    }
                }
                try {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>开始获取[{}]图片", name);
                    //获取演员图片
                    String image = getStarImage(name);
                    //上传图片
                    log.info("开始上传[{}]图片", name);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_JPEG);
                    HttpEntity<String> entity = new HttpEntity<>(image, headers);
                    restTemplate.exchange(String.format("http://%s:8096/emby/Items/%s/Images/Primary?api_key=%s", ip, id, apiKey), HttpMethod.POST, entity, String.class);
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>上传[{}]图片成功", name);
                } catch (Exception e) {
                    log.error(">>>>>>>>>>>>>>>>>>>>>>>>上传[{}]图片失败", name);
                }
            }
        }
    }

    @Test
    public void getStarTest() throws IOException {
        System.out.println(getStarImage("奥田咲"));
    }

    /**
     * 获取图片
     *
     * @param name
     * @return
     * @throws IOException
     */
    public String getStarImage(String name) throws IOException {
        Document document = JsoupUtil.get("https://www.javbus.com/searchstar/" + name);
        Elements elements = document.select(".photo-frame");
        if (elements != null && !elements.isEmpty()) {
            String src = elements.get(0).select("img").attr("src");
//            FileUtil.downLoadFromUrl(src, new File("E:\\奥田咲.jpg"));
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            return FileUtil.imageToBase64Str(inputStream);
        } else {
            return null;
        }
    }
}
