package com.crawler.fx.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.stage.Stage;

import java.util.concurrent.*;

public abstract class ConstantUtil {
    //线程池
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("file-parse-%d").build();
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(1000), threadFactory);

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ConstantUtil.stage = stage;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void setExecutorService(ExecutorService executorService) {
        ConstantUtil.executorService = executorService;
    }
}
