package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.plugin.PluginRendererEntry;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PageRenderer {

    public static final String
        RENDERERS       = "renderers",
        EXCHANGE_RENDERERS = "exchange_renderers",
        PLUGIN          = "plugin",
        RENDERER        = "renderer",
        IMPORT          = "import",
        IMPORT_RELATIVE = "import_relative";

    private final LocaleService locale;
    private final Logger logger;

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final File sources, output;

    public PageRenderer(final File source, final File output, final DefaultFrontMatterLoader defaultFrontMatterLoader){
        this.locale = Main.getLocale();
        this.logger = Main.getLogger(locale.getString("page-renderer.name"));

        this.sources = source;
        this.output  = output;
        this.defaultFrontMatterLoader = defaultFrontMatterLoader;
    }

    public final FileRender render(final File IN, final File OUT){
        return render(IN, OUT, null, null);
    }

    public final FileRender render(final File IN, final File OUT, final SimpleHttpServer server, final SimpleHttpExchange exchange){
        byte[] bytes;
        try{
            bytes = Files.readAllBytes(IN.toPath());
        }catch(final OutOfMemoryError | SecurityException | IOException e){
            logger.severe(locale.getString("page-renderer.renderer.read", IN.getPath()) + LoggerService.getStackTraceAsString(e));
            return null;
        }

        // front matter
        final Map<String,? super Object> defaultFrontMatter = defaultFrontMatterLoader.getDefaultFrontMatter(IN);
        final YamlFrontMatter frontMatter = new YamlFrontMatter(new String(bytes, StandardCharsets.UTF_8));
        bytes = frontMatter.getContent().getBytes(StandardCharsets.UTF_8);
        // merge
        final Map<String,? super Object> merged = new HashMap<>();
        if(defaultFrontMatter != null)
            merged.putAll(defaultFrontMatter);
        if(frontMatter.getFrontMatter() != null)
            merged.putAll(frontMatter.getFrontMatter());
        final Map<String,? super Object> finalFrontMatter = YamlFrontMatter.loadImports(IN, merged); // do not make immutable, variables are shared across renders

        // render
        final FileRenderImpl fileRender = new FileRenderImpl(IN, OUT, finalFrontMatter, bytes, server, exchange);
        {
            final Object r = Objects.requireNonNullElse(merged.get(server == null || exchange == null ? RENDERERS : EXCHANGE_RENDERERS), new ArrayList<>());
            final List<?> renderersStr = r instanceof List ? (List<?>) r : List.of(r);

            if(renderersStr.isEmpty())
                return fileRender;

            final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

            for(final PluginRendererEntry entry : renderers){
                final Renderer renderer = entry.getRenderer();
                final ExecutorService executor = Executors.newSingleThreadExecutor();

                final String pluginName   = entry.getPluginName();
                final String rendererName = entry.getRendererName();

                final Future<byte[]> future = executor.submit(() -> {
                    try{
                        return renderer.render(fileRender);
                    }catch(final Throwable e){
                        logger.severe(locale.getString("page-renderer.renderer.exception", pluginName, rendererName) + LoggerService.getStackTraceAsString(e));
                    }
                    return fileRender.getContentAsBytes();
                });

                try{
                    fileRender.setBytes(future.get(30,TimeUnit.SECONDS));
                }catch(final TimeoutException | InterruptedException e){
                    logger.severe(locale.getString("page-renderer.renderer.time", pluginName, rendererName) + LoggerService.getStackTraceAsString(e));
                }catch(final Throwable e){
                    logger.severe(locale.getString("page-renderer.renderer.exception", pluginName, rendererName) + LoggerService.getStackTraceAsString(e));
                }finally{
                    future.cancel(true);
                    executor.shutdownNow();
                }
            }
        }
        return fileRender;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("sources", sources)
            .addObject("output", output)
            .addObject("defaultFrontMatterLoader", defaultFrontMatterLoader)
            .toString();
    }

}
