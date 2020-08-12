package com.kttdevelopment.webdir.server.render;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.server.ExchangeRenderer;
import com.kttdevelopment.webdir.api.server.ExchangeRendererAdapter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.QuinFunction;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class ExchangePageRenderer implements QuinFunction<SimpleHttpExchange,File,File,ConfigurationSection,byte[],byte[]> {

    @Override
    public final byte[] apply(final SimpleHttpExchange exchange, final File IN, final File OUT, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("exchangeRenderer"));
        final String sourceABS      = IN.getAbsolutePath();

        logger.finest(locale.getString("exchangeRenderer.debug.render",exchange,sourceABS,defaultFrontMatter,bytes));

        final String sourceContent;
        try{ sourceContent = Files.readString(IN.toPath());
        }catch(final IOException e){
            logger.warning(locale.getString("exchangeRenderer.failedRead", sourceABS + '\n' + Exceptions.getStackTraceAsString(e)));
            return bytes; }
    // create front matter from source
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(sourceContent).read();

        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.frontMatter",sourceABS,frontMatter));

        if(!frontMatter.hasFrontMatter() && defaultFrontMatter == null) return bytes;

        final ConfigurationSection mergedFrontMatter = new ConfigurationSectionImpl();
        if(defaultFrontMatter != null)
            mergedFrontMatter.setDefault(defaultFrontMatter);
        if(frontMatter.hasFrontMatter())
            mergedFrontMatter.setDefault(frontMatter.getFrontMatter());

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(IN,mergedFrontMatter);
    // get renderers
        final List<String> renderersStr = finalFrontMatter.getList(ServerVars.Renderer.exchangeRendererKey,String.class);

        // if no renderers then return given bytes
        if(renderersStr == null || renderersStr.isEmpty()) return bytes;

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(ServerVars.Renderer.exchangeRendererKey, renderersStr);
    // render page
        final AtomicReference<String> content = new AtomicReference<>(new String(bytes));

        final InetAddress address     = exchange.getPublicAddress().getAddress();
        final Permissions permissions = Main.getPermissions().getPermissions();
        logger.finest(locale.getString("exchangeRenderer.debug.permissions",permissions,address.getHostAddress()));
        renderers.forEach(renderer -> {
            final Renderer render = renderer.getRenderer();
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<String> future = executor.submit(() -> {
                final AtomicReference<String> buffer = new AtomicReference<>(content.get());
                String ct = content.get();

                try{
                    buffer.set(render.render(IN, OUT,defaultFrontMatter,buffer.get()));
                    logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply",renderer.getRendererName(),sourceABS,ct,buffer.get()));
                }catch(final Throwable e){
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
                }

                try{
                    ct = buffer.get();
                    // if is an adapter but not a class (adapter has no permissions) or is class and has permission
                    if((render instanceof ExchangeRendererAdapter && !(render instanceof ExchangeRenderer)) || render instanceof ExchangeRenderer && permissions.hasPermission(address, ((ExchangeRenderer) render).getPermission())){
                        buffer.set(((ExchangeRendererAdapter) renderer.getRenderer()).render(exchange, IN, OUT, finalFrontMatter, buffer.get()));
                        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply",renderer.getRendererName(),sourceABS,ct,buffer.get()));
                    }
                }catch(final Throwable e){
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
                }
                return buffer.get();
            });

            try{
                content.set(Objects.requireNonNull(future.get(Vars.Plugin.loadTimeout, Vars.Plugin.loadTimeoutUnit)));
            }catch(TimeoutException | InterruptedException e){
                logger.severe(
                    locale.getString("pageRenderer.pageRenderer.timedOut", renderer.getPluginName(), renderer.getRendererName(), IN.getPath(), Vars.Plugin.loadTimeout + " " + Vars.Plugin.loadTimeoutUnit.name().toLowerCase())
                );
            }catch(final Throwable e){
                logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }finally{
                future.cancel(true);
                executor.shutdownNow();
            }
        });
        return content.get().getBytes();
    }

}
