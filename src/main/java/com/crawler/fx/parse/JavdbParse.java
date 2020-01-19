package com.crawler.fx.parse;

import com.crawler.fx.bean.FileInfo;
import com.crawler.fx.util.FileNameRegexUtil;
import com.crawler.fx.util.FileUtil;
import com.crawler.fx.util.InfoUtil;
import com.crawler.fx.util.JsoupUtil;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavdbParse extends AbstractParse {

    private static Logger logger = LoggerFactory.getLogger(JavdbParse.class);

    private final static String HTML_ELEMENT_REGEX = "<.*>.*</[\\w-\\W-]*>";

    @Override
    public boolean support(String fileName) {
        return !FileNameRegexUtil.isMultiFileOther(fileName) && FileNameRegexUtil.isNumberAndString(fileName) && !FileNameRegexUtil.isFc2(fileName);
    }

    @Override
    public boolean parse(FileInfo fileInfo) {
        logger.info("JavdbParse开始解析文件:{}", fileInfo);
        try {
            Document document = JsoupUtil.get(String.format("https://javdb.com/search?q=%s&f=all", fileInfo.getFileName()));
            //获取列表是否有值
            Elements results = document.select("div#videos .column:eq(0) a.box");
            String website = null;
            if (!results.isEmpty()) {
                String href = results.attr("href");
                document = JsoupUtil.get(String.format("https://javdb.com%s", href));
                website = String.format("https://javdb.com%s", href);
            }

            String title = document.select("h2.title").text();
            if (StringUtils.isEmpty(title)) {
                logger.info("番号解析失败：{}", fileInfo.getFileName());
                return false;
            } else {
                logger.info("番号解析成功：{}", title);
            }
            String num = document.select("div.panel-block span:contains(番號:) + span.value").text().trim().toUpperCase();
            String year = document.select("div.panel-block span:contains(時間:) + span.value").html().replaceAll(HTML_ELEMENT_REGEX, "").trim();
            String release = year;
            String runtime = document.select("div.panel-block span:contains(時長:) + span.value").html()
                    .replaceAll(HTML_ELEMENT_REGEX, "").replace("分鍾", "").trim();
            String director = "";
            //发行商
            String studio = document.select("div.panel-block span:contains(製作:) + span.value").text();
            //获取封面链接
            String cover = document.select("div.column-video-cover a").attr("href");
            //姓名可能有多个
            Elements actorElements = document.select("div.panel-block span:contains(演員) + span.value a");
            List<String> actor = new ArrayList<>();
            for (Element element : actorElements) {
                actor.add(element.text());
            }
            if (actor.isEmpty()) {
                actor.add("未知");
            }
            //系列(兄弟选择器)
            String serise = "";
            //类别
            Elements tagElements = document.select("div.panel-block span:contains(类别) + span.value a");
            List<String> tag = new ArrayList<>();
            for (Element element : tagElements) {
                tag.add(element.text());
            }

            fileInfo.setTitle(title.replace(num, "").trim() + " " + num);
            fileInfo.setNumber(num);
            fileInfo.setYear(year);
            fileInfo.setRelease(release);
            fileInfo.setRuntime(runtime);
            fileInfo.setDirector(director);
            fileInfo.setStudio(studio);
            fileInfo.setCover(cover);
            fileInfo.setActor(actor);
            fileInfo.setSerise(serise);
            fileInfo.setTag(tag);
            fileInfo.setWebsite(website);
            logger.info("番号信息：{}", fileInfo);

            String rootPath = FileUtil.getRootPath();
            File imageFile = new File(String.format("%s/tmp/%s.jpg", rootPath, fileInfo.getNumber()));
            //下载图片
            FileUtil.downLoadFromUrl(cover, imageFile);
            //裁剪图片
            FileUtils.copyFile(imageFile, new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber())));
            //生成info
            InfoUtil.createInfo(fileInfo, new File(String.format("%s/tmp/%s.nfo", rootPath, fileInfo.getNumber())));

            fileInfo.setStatus("解析成功");
            logger.info("文件解析成功:{}", fileInfo);
            return true;
        } catch (Exception e) {
            fileInfo.setStatus("解析失败");
            logger.error("文件解析失败:{}", fileInfo, e);
            return false;
        }
    }

    /**
     * 测试用
     *
     * @return
     */
    public String readFile() {
        //SSNI-563、SSNI-557
        String pathname = "D:/SSNI-557.html";
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

    public static void main(String[] args) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("010115_001");
        JavdbParse javBusParse = new JavdbParse();
        javBusParse.parse(fileInfo);
    }
}
