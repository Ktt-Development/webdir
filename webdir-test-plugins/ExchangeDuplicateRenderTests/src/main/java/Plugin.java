import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.server.ExchangeRenderAdapter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("firstEx", new ExchangeRenderAdapter(){

            @Override
            public String render(final File output, final ConfigurationSection yamlFrontMatter, final String content){
                return content;
            }

            @Override
            public String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content){
                return "DUPLICATEEX";
            }
        });
    }

}