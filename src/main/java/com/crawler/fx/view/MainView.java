package com.crawler.fx.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public abstract class MainView {

    @FXML
    public Label filePathLabel;

    @FXML
    public TextArea logTextArea;

    @FXML
    public TableView fileTableView;
    @FXML
    public TableColumn colOriginalFileName;
    @FXML
    public TableColumn colFileName;
    @FXML
    public TableColumn colFileStatus;

    /**
     * 移动文件相关
     */
    @FXML
    public Label moveMovieLog;
    @FXML
    public TextField srcFilePath;
    @FXML
    public TextField tarFilePath;
}
