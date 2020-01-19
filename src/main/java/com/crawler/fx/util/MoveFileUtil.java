package com.crawler.fx.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public abstract class MoveFileUtil {

    private static Logger logger = LoggerFactory.getLogger(MoveFileUtil.class);

    private final static Set<String> FILE_TYPE = FileUtil.getMovieType();

    /**
     * char(65) = A
     */
    private final static int FIRST_POSTFIX = 64;

    public static boolean moveMovieFile(String srcFile, String target) throws IOException {
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        Files.walk(Paths.get(srcFile), FileVisitOption.values())
                .filter(path -> {
                    String fileType = FileUtil.getFileType(path.toString());
                    if (fileType == null) {
                        return false;
                    } else {
                        return FILE_TYPE.contains(fileType);
                    }
                })
                .forEach(path -> {
                    try {
                        logger.info("开始文件统一移动:{}", path);
                        File file = path.toFile();
                        File newFile = new File(target, file.getName());
                        newFile = createNewFile(newFile, FIRST_POSTFIX);

                        FileUtils.moveFile(file, newFile);
                        //尝试删除文件夹
                        boolean delete = true;
                        File parentFile = file.getParentFile();
                        for (File item : parentFile.listFiles()) {
                            if (FILE_TYPE.contains(FileUtil.getFileType(item))) {
                                delete = false;
                            }
                        }
                        if (delete) {
                            logger.info("删除文件夹:{}", parentFile.getPath());
                            FileUtils.deleteDirectory(parentFile);
                        }
                    } catch (Exception e) {
                        logger.error("开始文件统一移动失败", e);
                    }
                });
        return true;
    }

    private static File createNewFile(File file, int postfix) {
        if (file.exists()) {
            String filePath = file.getPath();
            int indexOf = filePath.lastIndexOf(".");
            File newFile = new File(String.format("%s-%s%s", filePath.substring(0, indexOf), (char) (postfix + 1), filePath.substring(indexOf)));
            return createNewFile(newFile, postfix + 1);
        } else {
            return file;
        }
    }
}
