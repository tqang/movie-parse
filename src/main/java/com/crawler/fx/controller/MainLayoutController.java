package com.crawler.fx.controller;

import com.crawler.fx.bean.FileInfo;
import com.crawler.fx.log.TextAreaAppender;
import com.crawler.fx.parse.AbstractParse;
import com.crawler.fx.parse.Fc2Parse;
import com.crawler.fx.parse.JavBusParse;
import com.crawler.fx.util.ConstantUtil;
import com.crawler.fx.util.FileUtil;
import com.crawler.fx.util.MoveFileUtil;
import com.crawler.fx.util.PropertiesUtil;
import com.crawler.fx.view.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainLayoutController extends MainView implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    // 文件列表数据
    private final ObservableList<FileInfo> fileData = FXCollections.observableArrayList();
    // 文件选择器
    private final FileChooser fileChooser = new FileChooser();
    //文件夹选择器
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    //文件类型
    private Set<String> fileTypeList = FileUtil.getMovieType();
    //文件解析器
    private List<AbstractParse> parseList = Arrays.asList(new JavBusParse(), new Fc2Parse());
    //解析锁
    private ReentrantLock fileParseLock = new ReentrantLock();
    //选择的目录
    private String scanFileDirectory = null;
    //可重入锁
    private Lock fileLock = new ReentrantLock();

    /**
     * 初始化
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showList();
        //初始化日志显示
        TextAreaAppender.setLogTextArea(logTextArea);
    }

    /**
     * 选择文件夹
     */
    @FXML
    public void selectDirectory(ActionEvent event) {
        File file = directoryChooser.showDialog(ConstantUtil.getStage());
        if (file != null) {
            logger.info("选择文件夹:{}", file.getPath());
            scanFileDirectory = file.getAbsolutePath();
            filePathLabel.setText(file.getAbsolutePath());
            scanFile(file);
        }
    }

    /**
     * 刷新文件
     */
    @FXML
    public void refreshFile() {
        logger.info("刷新文件夹");
        String text = filePathLabel.getText();
        if (!StringUtils.isEmpty(text)) {
            scanFile(new File(text));
        }
    }

    @FXML
    public void moveFile() {
        if (fileLock.tryLock()) {
            for (FileInfo fileInfo : fileData) {
                if (!StringUtils.isEmpty(fileInfo.getStatus()) && "解析成功".equals(fileInfo.getStatus())) {
                    try {
                        logger.info("开始移动文件:{}", fileInfo);
                        int i = scanFileDirectory.indexOf(File.separator);
                        File rootFile = new File(scanFileDirectory.substring(0, i + 1));
                        //创建目录
                        File catalog = new File(rootFile, "AV" + File.separator + fileInfo.getActor().get(0)
                                + File.separator + fileInfo.getNumber() + File.separator);
                        if (!catalog.exists()) {
                            catalog.mkdirs();
                        }
                        //移动影片
                        String originalFileName = fileInfo.getOriginalFileName();
                        int indexOf = originalFileName.lastIndexOf(".");
                        String fileType = originalFileName.substring(indexOf);

                        if (fileInfo.isSplit()) {
                            //多部分的情况

                            //移动所有的部分文件
                            for (FileInfo file : fileData) {
                                //匹配文件
                                boolean matches = Pattern.matches(fileInfo.getFileName() + "-[a-zA-Z]\\..*", file.getOriginalFileName());
                                if (matches) {
                                    //文件名大写、后缀不变
                                    int index = file.getOriginalFileName().indexOf(".");
                                    FileUtils.moveFile(new File(file.getPath() + File.separator + file.getOriginalFileName())
                                            , new File(catalog, file.getOriginalFileName().substring(0, index).toUpperCase() + file.getOriginalFileName().substring(index)));
                                    file.setStatus("移动成功");
                                    logger.info("移动文件成功:{}", file);
                                }
                            }
                            //移动图片和信息
                            String rootPath = FileUtil.getRootPath();
                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s-A.png", fileInfo.getNumber())));
                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.jpg", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s-A.jpg", fileInfo.getNumber())));
                            //背景大图
                            FileUtils.copyFile(new File(catalog, String.format("%s-A.jpg", fileInfo.getNumber()))
                                    , new File(catalog, "Backdrop.jpg"));

                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.nfo", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s-A.nfo", fileInfo.getNumber())));
                        } else {
                            FileUtils.moveFile(new File(fileInfo.getPath() + File.separator + fileInfo.getOriginalFileName()), new File(catalog, fileInfo.getNumber() + fileType));
                            //移动图片和信息
                            String rootPath = FileUtil.getRootPath();
                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.png", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s.png", fileInfo.getNumber())));
                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.jpg", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s.jpg", fileInfo.getNumber())));
                            //背景大图
                            FileUtils.copyFile(new File(catalog, String.format("%s.jpg", fileInfo.getNumber()))
                                    , new File(catalog, "Backdrop.jpg"));

                            FileUtils.moveFile(new File(String.format("%s/tmp/%s.nfo", rootPath, fileInfo.getNumber()))
                                    , new File(catalog, String.format("%s.nfo", fileInfo.getNumber())));
                        }
                        fileInfo.setStatus("移动成功");
                        logger.info("移动文件成功:{}", fileInfo);
                    } catch (Exception e) {
                        fileInfo.setStatus("移动失败");
                        logger.error("移动文件失败:{}", fileInfo, e);
                    }
                }
            }
            fileLock.unlock();
        }
    }

    private void scanFile(File file) {
        logger.info("扫描文件:{}", file.getPath());
        fileData.clear();
        File[] files = file.listFiles();
        for (File item : files) {
            String fileName = item.getName();
            int indexOf = fileName.lastIndexOf(".");
            String fileType = null;
            if (indexOf != -1) {
                fileType = fileName.substring(indexOf + 1).toUpperCase();
            }
            if (item.isFile() && fileTypeList.contains(fileType)) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setPath(file.getAbsolutePath());
                fileInfo.setOriginalFileName(fileName);
                fileInfo.setFileName(standardFileName(fileName));
                fileInfo.setStatus("准备");

                fileData.add(fileInfo);
            }
        }
    }

    private String standardFileName(String fileName) {
        //去掉后缀
        int indexOf = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, indexOf);
        List<String> standard = PropertiesUtil.find("standard.file_name");
        if (standard != null) {
            for (String regx : standard) {
                fileName = fileName.replaceAll(regx, "");
            }
        }
        return fileName.toUpperCase();
    }

    /**
     * 并行解析文件
     */
    @FXML
    public void parseFile(ActionEvent event) {
        if (fileParseLock.tryLock()) {
            try {
                List<FileInfo> newFileData = fileData.stream().filter(node -> ("准备".equals(node.getStatus()) || "解析失败".equals(node.getStatus())))
                        .collect(Collectors.toList());
                logger.info("提交{}个解析文件", newFileData.size());
                for (FileInfo file : newFileData) {
                    file.setStatus("处理中");
                    ConstantUtil.getExecutorService().execute(() -> {
                        try {
                            for (AbstractParse abstractParse : parseList) {
                                if (abstractParse.support(file.getFileName())) {
                                    if (abstractParse.parse(file)) {
                                        //移动文件
                                        moveFile();
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("解析文件异常", e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("解析文件错误", e);
            } finally {
                fileParseLock.unlock();
            }
        }
    }

    @FXML
    public void moveMovieFile() {
        try {
            String srcFilePathText = srcFilePath.getText();
            String tarFilePathText = tarFilePath.getText();
            if (StringUtils.isNotEmpty(srcFilePathText) && StringUtils.isNotEmpty(tarFilePathText)) {
                moveMovieLog.setText("开始移动");
                MoveFileUtil.moveMovieFile(srcFilePathText, tarFilePathText);
                moveMovieLog.setText("移动成功");
            } else {
                moveMovieLog.setText("目录不能为空");
            }
        } catch (Exception e) {
            logger.error("移动文件异常", e);
        }
    }

    /**
     * 显示数据
     */
    public void showList() {
        colOriginalFileName.setCellValueFactory(new PropertyValueFactory("originalFileName"));
        colFileName.setCellValueFactory(new PropertyValueFactory("fileName"));
        colFileStatus.setCellValueFactory(new PropertyValueFactory("status"));

        fileTableView.setItems(fileData);
    }
}
