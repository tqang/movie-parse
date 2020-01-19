package com.crawler.fx.log;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;

public class TextAreaAppender extends AppenderBase<LoggingEvent> {

    private PatternLayoutEncoder encoder = new PatternLayoutEncoder();

    private static TextArea logTextArea = null;

    public static void setLogTextArea(TextArea logTextArea) {
        TextAreaAppender.logTextArea = logTextArea;
    }

    @Override
    public void start() {
        super.start();
        encoder.setContext(this.getContext());
        encoder.setPattern("%-5level - %msg%n");
        encoder.start();
    }

    @Override
    protected void append(LoggingEvent event) {
        byte[] bytes = encoder.encode(event);
        if (logTextArea != null) {
            Platform.runLater(() -> logTextArea.appendText(new String(bytes, StandardCharsets.UTF_8)));
        }
    }
}
