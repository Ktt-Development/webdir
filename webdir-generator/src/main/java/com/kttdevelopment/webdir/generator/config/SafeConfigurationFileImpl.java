package com.kttdevelopment.webdir.generator.config;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.File;

public class SafeConfigurationFileImpl extends ConfigurationFileImpl {

    public SafeConfigurationFileImpl(final File file){
        super(file);
    }

    @Override
    public final void setDefault(final ConfigurationSection def){
        this.def = def;
    }

    @Override
    public final boolean reload(){
        return Exceptions.requireNonExceptionElse(super::reload, false);
    }

    @Override
    public final boolean save(){
        return Exceptions.requireNonExceptionElse(super::save, false);
    }

}
