package rsea.tool.resource;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

public class RSHttpProperties {
    private static RSHttpProperties _manager;

    private Properties mProperties;

    public static RSHttpProperties getInstance(){
        if(_manager == null){
            _manager = new RSHttpProperties();
        }
        return _manager;
    }

    public RSHttpProperties() {
    }

    public void load(Context context){
        try{
            InputStream in = context.getAssets().open("http.properties");
            mProperties = new Properties();
            mProperties.load(in);
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String get(String key){
        return mProperties.getProperty(key);
    }
}
