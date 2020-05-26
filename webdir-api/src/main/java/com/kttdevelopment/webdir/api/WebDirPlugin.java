package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.extension.Extension;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.page.Page;

import java.util.*;

public class WebDirPlugin {

    private final List<Extension> extensions = new ArrayList<>();

    public final List<Extension> getExtensions(){
        return Collections.unmodifiableList(extensions);
    }

    public final void addExtension(final Extension extension){
        extensions.add(extension);
    }

    private final List<Formatter> formatters = new ArrayList<>();

    public final List<Formatter> getFormatters(){
        return Collections.unmodifiableList(formatters);
    }

    public final void addFormatter(final Formatter formatter){
        formatters.add(formatter);
    }

    private final List<Page> pages = new ArrayList<>();

    public final List<Page> getPages(){
        return Collections.unmodifiableList(pages);
    }

    public final void addPage(final Page page){
        pages.add(page);
    }

    // instance +pluginService

    private final PluginServiceProvider provider;

    public WebDirPlugin(final PluginServiceProvider provider){
        this.provider = provider;
    }

    protected final PluginServiceProvider getPluginService(){
        return provider;
    }

    // override methods

    public void onEnable(){}

    public void onDisable(){}

}