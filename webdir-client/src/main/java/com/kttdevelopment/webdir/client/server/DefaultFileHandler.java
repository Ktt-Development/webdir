package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class DefaultFileHandler extends FileHandler {

    private final RootWatchService rootWatchService;
    private final PageRenderingService renderer;
    private final SimpleHttpServer server;

    public DefaultFileHandler(final PageRenderingService renderer, final SimpleHttpServer server){
        super(new HTMLNameAdapter());
        this.rootWatchService = new RootWatchService(this, Main.getConfig().string(ConfigService.CONTEXT));
        this.renderer = renderer;
        this.server   = server;
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        exchange.send(Objects.requireNonNull(renderer.render(source, server, exchange)).getContentAsBytes());
        rootWatchService.check();
        exchange.close();
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("renderers", renderer)
            .addObject("rootWatchService", rootWatchService)
            .toString();
    }

}