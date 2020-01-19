package com.crawler.fx.parse;

import com.crawler.fx.bean.FileInfo;
import com.crawler.fx.util.FileNameRegexUtil;
import com.crawler.fx.util.FileUtil;
import com.crawler.fx.util.InfoUtil;
import com.crawler.fx.util.JsoupUtil;
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

/**
 * https://fc2club.com
 */
public class Fc2Parse extends AbstractParse {

    private static Logger logger = LoggerFactory.getLogger(Fc2Parse.class);

    @Override
    public boolean support(String fileName) {
        return !FileNameRegexUtil.isMultiFileOther(fileName) && FileNameRegexUtil.isNumberAndString(fileName) && FileNameRegexUtil.isFc2(fileName);
    }

    @Override
    public boolean parse(FileInfo fileInfo) {
        logger.info("Fc2Parse开始解析文件:{}", fileInfo);
        try {
            //判断是否是多文件
            if (FileNameRegexUtil.isMultiFile(fileInfo.getFileName())) {
                fileInfo.setSplit(true);
                //除去后缀
                fileInfo.setFileName(fileInfo.getFileName().replaceAll("-[a-zA-Z]$", ""));
            }

            Document document = JsoupUtil.get(String.format("https://fc2club.com/html/%s.html", fileInfo.getFileName()));
            //测试
//            Document document = Jsoup.parse(FileUtil.readFile());
            String title = document.select(".show-top-grids .col-sm-8 h3").text();
            if (StringUtils.isEmpty(title)) {
                logger.info("番号解析失败：{}", fileInfo);
                fileInfo.setStatus("解析失败");
                return false;
            } else {
                logger.info("番号解析成功：{}", title);
            }
            String num = fileInfo.getFileName().toUpperCase();
            //获取封面链接
            Elements covers = document.select("#slider li");
            String cover = null;
            if (covers != null && !covers.isEmpty()) {
                cover = "https://fc2club.com" + covers.get(0).getElementsByClass("responsive").attr("src");
            } else {
                logger.info("封面解析失败：{}", fileInfo);
                fileInfo.setStatus("解析失败");
                return false;
            }
            //姓名可能有多个
            Elements actorElements = document.select(".show-top-grids .col-sm-8 strong:contains(女优名字) + a");
            List<String> actor = new ArrayList<>();
            for (Element element : actorElements) {
                String actorStr = element.text();
                if (StringUtils.isEmpty(actorStr)) {
                    continue;
                }
                if (actorStr.contains("/")) {
                    String[] split = actorStr.split("/");
                    for (String s : split) {
                        actor.add(s);
                    }
                } else {
                    actor.add(actorStr);
                }
            }
            if (actor.isEmpty()) {
                actor.add("未知");
            }
            //类别
            List<String> tag = new ArrayList<>();
            tag.add("FC2");
            Elements tagElements = document.select(".show-top-grids .col-sm-8 h5:contains(影片标签) a");
            for (Element element : tagElements) {
                if (!StringUtils.isEmpty(element.text())) {
                    tag.add(element.text());
                }
            }

            fileInfo.setTitle(title.replace(num, "").trim() + " " + num);
            fileInfo.setNumber(num);
            fileInfo.setActor(actor);
            fileInfo.setTag(tag);
            fileInfo.setWebsite(String.format("https://fc2club.com/html/%s.html", fileInfo.getNumber()));
            logger.info("番号信息：{}", fileInfo);

            String rootPath = FileUtil.getRootPath();
            File imageFile = new File(String.format("%s/tmp/%s.jpg", rootPath, fileInfo.getNumber()));
            //下载图片
            FileUtil.downLoadFromUrl(cover, imageFile);
            //裁剪图片
            FileUtil.subimage(imageFile, new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber())));
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

    public String readFile() {
        String pathname = "D:/FC2-1040038.html";
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
}
