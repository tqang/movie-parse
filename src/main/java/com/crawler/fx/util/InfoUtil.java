package com.crawler.fx.util;

import com.crawler.fx.bean.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class InfoUtil {
    private static Logger logger = LoggerFactory.getLogger(InfoUtil.class);

    public static void createInfo(FileInfo fileInfo, File file) throws IOException {
        logger.info("开始生成.nfo文件：{}", fileInfo.getNumber());
        if (!file.exists()) {
            file.createNewFile();
        }

        StringBuilder info = new StringBuilder();
        info.append(String.format("<movie>\r\n"));
        info.append(String.format(" <title>%s</title>\r\n", Optional.ofNullable(fileInfo.getTitle()).orElse("")));
        info.append(String.format(" <studio>%s</studio>\r\n", Optional.ofNullable(fileInfo.getStudio()).orElse("")));
        info.append(String.format(" <year>%s</year>\r\n", Optional.ofNullable(fileInfo.getYear()).orElse("")));
        info.append(String.format(" <outline>%s</outline>\r\n", Optional.ofNullable(fileInfo.getOutline()).orElse("")));
        info.append(String.format(" <plot>%s</plot>\r\n", Optional.ofNullable(fileInfo.getOutline()).orElse("")));
        info.append(String.format(" <runtime>%s</runtime>\r\n", Optional.ofNullable(fileInfo.getRuntime()).orElse("")));
        info.append(String.format(" <director>%s</director>\r\n", Optional.ofNullable(fileInfo.getDirector()).orElse("")));
        if (fileInfo.isSplit()) {
            info.append(String.format(" <poster>%s-A.png</poster>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
            info.append(String.format(" <thumb>%s-A.png</thumb>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
            info.append(String.format(" <fanart>%s-A.jpg</fanart>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
        } else {
            info.append(String.format(" <poster>%s.png</poster>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
            info.append(String.format(" <thumb>%s.png</thumb>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
            info.append(String.format(" <fanart>%s.jpg</fanart>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
        }
        if (fileInfo.getActor() != null) {
            for (String actor : fileInfo.getActor()) {
                info.append(" <actor>\r\n");
                info.append(String.format("  <name>%s</name>\r\n", Optional.ofNullable(actor).orElse("")));
                info.append(String.format("  <thumb>%s</thumb>\r\n", "actor.jpg"));
                info.append(" </actor>\r\n");
            }
        }
        info.append(" <label></label>\r\n");
        if (fileInfo.getTag() != null) {
            for (String tag : fileInfo.getTag()) {
                info.append(String.format(" <genre>%s</genre>\r\n", Optional.ofNullable(tag).orElse("")));
            }
            for (String tag : fileInfo.getTag()) {
                info.append(String.format(" <tag>%s</tag>\r\n", Optional.ofNullable(tag).orElse("")));
            }
        }
        info.append(String.format(" <num>%s</num>\r\n", Optional.ofNullable(fileInfo.getNumber()).orElse("")));
        info.append(String.format(" <release>%s</release>\r\n", Optional.ofNullable(fileInfo.getRelease()).orElse("")));
        info.append(String.format(" <cover>%s</cover>\r\n", Optional.ofNullable(fileInfo.getCover()).orElse("")));
        info.append(String.format(" <website>%s</website>\r\n", Optional.ofNullable(fileInfo.getWebsite()).orElse("")));
        info.append(String.format("</movie>"));

        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter out = new BufferedWriter(fileWriter)) {
            out.write(new String(info.toString().getBytes(StandardCharsets.UTF_8)));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
