package com.crawler.fx;

import com.crawler.fx.util.ConstantUtil;
import com.crawler.fx.util.FileUtil;
import com.crawler.fx.util.PropertiesUtil;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class BootApplication extends AbstractJavaFxApplicationSupport {

    private static Logger logger = LoggerFactory.getLogger(BootApplication.class);

    public static void main(String[] args) {
        launch(BootApplication.class, args);
    }

    @PostConstruct
    public void init() {
        //读取配置文件
        Properties classPathProperties = null;
        try {
            classPathProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        } catch (IOException e) {
        }
        //获取当前jar包路径下文件
        Properties fileSystemProperties = null;
        try {
            fileSystemProperties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(FileUtil.getRootPath() + File.separator + "application.properties"));
        } catch (IOException e) {
            logger.warn("未找到配置文件将使用默认配置");
        }
        PropertiesUtil.putAll(classPathProperties);
        PropertiesUtil.putAll(fileSystemProperties);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ConstantUtil.setStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        //窗口关闭回调
        stage.setOnCloseRequest(e -> ConstantUtil.getExecutorService().shutdownNow());

        stage.setTitle("刮刮器");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FxApplication.class.getResource("/css/default.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
