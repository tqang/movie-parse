package com.crawler.fx.parse;

import com.crawler.fx.bean.FileInfo;
import com.crawler.fx.util.*;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavBusParse extends AbstractParse {

    private static Logger logger = LoggerFactory.getLogger(JavBusParse.class);

    private final static String HTML_ELEMENT_REGEX = "<.*>.*</[\\w-\\W-]*>";

    @Override
    public boolean support(String fileName) {
        return !FileNameRegexUtil.isMultiFileOther(fileName) && FileNameRegexUtil.isNumberAndString(fileName) && !FileNameRegexUtil.isFc2(fileName);
    }

    @Override
    public boolean parse(FileInfo fileInfo) {
        logger.info("JavBusParse开始解析文件:{}", fileInfo);
        try {
            //判断是否是多文件
            if (FileNameRegexUtil.isMultiFile(fileInfo.getFileName())) {
                fileInfo.setSplit(true);
                //除去后缀
                fileInfo.setFileName(fileInfo.getFileName().replaceAll("-[a-zA-Z]$", ""));
            }

            Document document = JsoupUtil.get(String.format("https://www.javbus.com/%s", fileInfo.getFileName()));
            //测试
//            Document document = Jsoup.parse(FileUtil.readFile());
            String title = document.select("div.container h3").text();
            if (StringUtils.isEmpty(title)) {
                logger.info("番号解析失败：{}", fileInfo);
                fileInfo.setStatus("解析失败");
                return false;
            } else {
                logger.info("番号解析成功：{}", title);
            }
            String num = document.select("div.movie div.info span:contains(識別碼) + span").text().trim().toUpperCase();
            String year = document.select("div.movie div.info p:eq(1)").html().replaceAll(HTML_ELEMENT_REGEX, "").trim();
            String release = year;
            String runtime = document.select("div.movie div.info p:eq(2)").html()
                    .replaceAll(HTML_ELEMENT_REGEX, "").replace("分鐘", "").trim();
            String director = document.select("div.movie div.info span:contains(導演) + a").text().trim();
            //制作商
            String studio = document.select("div.movie div.info span:contains(製作商) + a").text();
            //获取封面链接
            String cover = document.select("a.bigImage").attr("href");
            //姓名可能有多个
            Elements actorElements = document.select("div.movie div.info p:last-child span a");
            List<String> actor = new ArrayList<>();
            for (Element element : actorElements) {
                if (!StringUtils.isEmpty(element.text())) {
                    actor.add(element.text());
                }
            }
            if (actor.isEmpty()) {
                actor.add("未知");
            }
            //系列(兄弟选择器)
            String serise = document.select("div.movie div.info span:contains(系列) + a").text().trim();
            //类别
            Elements tagElements = document.select("div.movie div.info p:contains(類別) + p span");
            List<String> tag = new ArrayList<>();
            for (Element element : tagElements) {
                tag.add(element.text());
            }
            //添加其他tag
            if (!StringUtils.isEmpty(studio)) {
                tag.add("制造商:" + studio);
            }
            if (!StringUtils.isEmpty(director)) {
                tag.add("导演:" + director);
            }
            if (!StringUtils.isEmpty(serise)) {
                tag.add("系列:" + serise);
            }
            //解析是否有中文字幕或者高清资源
            Document tagDocument = JsoupUtil.get(String.format("https://www.javbus.com/search/%s", fileInfo.getFileName()));
            Elements elements = tagDocument.select(".item .item-tag button");
            if (elements != null) {
                for (Element element : elements) {
                    String text = element.text();
                    if (!text.contains("新種")) {
                        tag.add(text);
                    }
                }
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
            fileInfo.setWebsite(String.format("https://www.javbus.com/%s", fileInfo.getNumber()));
            //添加字幕标题
            if (tag.contains("字幕")) {
                fileInfo.setTitle("【字幕】" + fileInfo.getTitle());
            }
            //多部的情况
            if (fileInfo.isSplit()) {
                tag.add("多部");
                fileInfo.setTitle("【多部】" + fileInfo.getTitle());
            }
            logger.info("番号信息：{}", fileInfo);

            String rootPath = FileUtil.getRootPath();
            File imageFile = new File(String.format("%s/tmp/%s.jpg", rootPath, fileInfo.getNumber()));
            //下载图片
            FileUtil.downLoadFromUrl(cover, imageFile);
            //裁剪图片
            String notSubimage = PropertiesUtil.getProperty("not_subimage.studio");
            if (!StringUtils.isEmpty(notSubimage)) {
                List<String> list = Arrays.asList(notSubimage.split(","));
                if (list.contains(studio)) {
                    //不裁剪
                    FileUtils.copyFile(imageFile, new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber())));
                } else {
                    FileUtil.subimage(imageFile, new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber())));
                }
            } else {
                FileUtil.subimage(imageFile, new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber())));
            }
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
}
