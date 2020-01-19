package com.crawler.fx.util;

import java.util.regex.Pattern;

public abstract class FileNameRegexUtil {

    private final static Pattern fc2 = Pattern.compile("^fc2|^FC2.*");

    private final static Pattern multi = Pattern.compile(".*-[a-zA-Z]$");

    private final static Pattern multiOther = Pattern.compile(".*-[b-zB-Z]$");

    private final static Pattern numberAndString = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /**
     * fc2文件
     * @param fileName
     * @return
     */
    public static boolean isFc2(String fileName) {
       return fc2.matcher(fileName).matches();
    }

    /**
     * 多文件
     * @param fileName
     * @return
     */
    public static boolean isMultiFile(String fileName) {
        return multi.matcher(fileName).matches();
    }

    /**
     * 多文件的其他部分
     * @param fileName
     * @return
     */
    public static boolean isMultiFileOther(String fileName) {
        return multiOther.matcher(fileName).matches();
    }

    /**
     * 只有数字和字母和-
     * @param fileName
     * @return
     */
    public static boolean isNumberAndString(String fileName) {
        return numberAndString.matcher(fileName).matches();
    }
}
