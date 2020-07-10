package com.kttdevelopment.webdir.generator.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandlerAdapter;

import java.io.File;

public class HTMLNameAdapter implements FileHandlerAdapter {

    final int len = ".html".length();

    @Override
    public String getName(final File file){
        final String name = file.getName();
        return name.endsWith(".html") ? name.substring(0,name.length()-len) : name;
    }

}