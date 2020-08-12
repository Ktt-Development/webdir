import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.server.FileRendererAdapter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("firstFH", new FileRendererAdapter() {
            @Override
            public String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
                return "DUPLICATEFH";
            }

            @Override
            public String render(final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
                return null;
            }
        });
    }

}
