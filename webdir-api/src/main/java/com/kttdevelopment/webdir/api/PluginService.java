package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * This class implements the methods required by the {@link WebDirPlugin}. Plugin developers do not use this class.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public abstract class PluginService {

    public abstract Logger getLogger();

    public abstract File getPluginFolder();

    public abstract PluginYml getPluginYml();

    public abstract WebDirPlugin getPlugin(final String pluginName);

    public abstract <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass);

    public abstract InputStream getResource(final String path);

    public abstract ConfigurationFile createConfiguration(final File file);

    public abstract ConfigurationFile createConfiguration(final InputStream stream);

    public abstract LocaleBundle getLocaleBundle(String resource);

}
