import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
        throw new RuntimeException();
    }

}
