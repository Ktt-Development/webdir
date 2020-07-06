package com.kttdevelopment.webdir.sitegenerator;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.sitegenerator.config.ConfigurationFileImpl;

import java.io.File;

public final class ConfigService {

    private final ConfigurationFile config = new ConfigurationFileImpl();

    public final ConfigurationFile getConfig(){
        return config;
    }

    private final File configFile;

    ConfigService(final File configFile, final String defaultConfigResource){
        this.configFile = configFile;


    }

}