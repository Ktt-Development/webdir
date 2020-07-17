import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        try{
            Thread.sleep(1000 * 60);
        }catch(InterruptedException ignored){ }
    }

}