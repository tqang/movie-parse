package com.crawler.fx.parse;

import com.crawler.fx.bean.FileInfo;

public abstract class AbstractParse {

    public abstract boolean support(String fileName);

    public abstract boolean parse(FileInfo fileInfo);
}
