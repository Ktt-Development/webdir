package com.kttdevelopment.webdir.client.plugin;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.PluginLoader;
import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.util.logging.Logger;

public class PluginServiceImpl extends PluginService {

    private static final String badFileChars = "[\\\\/:*?\"<>|]";

    private final Logger logger;
    private final File pluginFolder, sources, output, defaults, plugins;

    public PluginServiceImpl(final YamlMapping plugin, final File pluginFolder){
        this.pluginFolder   = new File(pluginFolder,plugin.string(PluginLoader.NAME).replaceAll('[' + badFileChars + ']', "_"));
        this.logger         = Main.getLogger(plugin.string(PluginLoader.NAME));
        this.sources        = Main.getPageRenderingService().getSources();
        this.output         = Main.getPageRenderingService().getOutput();
        this.defaults       = Main.getPageRenderingService().getDefaults();
        this.plugins        = Main.getPluginLoader().getPluginsFolder();
    }

    @Override
    public final Logger getLogger(){
        return logger;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public File getPluginFolder(){
        if(!pluginFolder.exists()) pluginFolder.mkdirs();
        return pluginFolder;
    }

    @Override
    public WebDirPlugin getPlugin(final String pluginName){
        return Main.getPluginLoader().getPlugin(pluginName);
    }

    @Override
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return Main.getPluginLoader().getPlugin(pluginName, pluginClass);
    }

    @Override
    public final LocaleBundle getLocaleBundle(final String resource, final ClassLoader classLoader){
        return new LocaleBundleImpl(Main.getLocale(), resource, classLoader);
    }

    @Override
    public final File getSourcesFolder(){
        return sources;
    }

    @Override
    public final File getOutputFolder(){
        return output;
    }

    @Override
    public final File getDefaultsFolder(){
        return defaults;
    }

    @Override
    public final File getPluginsFolder(){
        return plugins;
    }

    @Override
    public String toString(){
        return new ToStringBuilder("PluginService")
            .addObject("logger", logger)
            .addObject("pluginFolder", pluginFolder)
            .addObject("sources", sources)
            .addObject("output", output)
            .addObject("defaults", defaults)
            .addObject("plugins", plugins)
            .toString();
    }

}