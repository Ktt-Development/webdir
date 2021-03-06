/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.client.server.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class FileServer {

    private final SimpleHttpServer server;

    FileServer(final String port) throws IOException{
        final LocaleService locale      = Main.getLocale();
        final Map<String,Object> config = Main.getConfig();
        final Logger logger             = Main.getLogger(locale.getString("server.name"));

        logger.info(locale.getString("server.constructor.start"));

        try{
            this.server = SimpleHttpServer.create(Integer.parseInt(port));
        }catch(final IOException e){
            logger.severe(locale.getString("server.constructor.port") + LoggerService.getStackTraceAsString(e));
            throw e;
        }

        final DefaultThrottler throttler = new DefaultThrottler();

        final DefaultSiteHandler _siteHandler = new DefaultSiteHandler(Main.getPageRenderingService(), server, new File(config.get(ConfigService.OUTPUT).toString(), config.get(ConfigService.F04).toString()));
        _siteHandler.addDirectory(new File(config.get(ConfigService.OUTPUT).toString()), "", true);
        final DefaultFileHandler filesHandler = new DefaultFileHandler(Main.getPageRenderingService(), server);
        final FileHandler rawHandler          = new RawFileHandler();

        server.createContext("", new ThrottledHandler(_siteHandler, throttler));
        server.createContext(Main.getConfig().get(ConfigService.CONTEXT).toString(), new ThrottledHandler(filesHandler, throttler));
        server.createContext(Main.getConfig().get(ConfigService.RAW).toString(), new ThrottledHandler(rawHandler, throttler));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        logger.info(locale.getString("server.constructor.finish"));
    }

    public final SimpleHttpServer getServer(){
        return server;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("server", server)
            .toString();
    }

}
