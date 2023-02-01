package rsea.tool.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class KeyValue {

    private String key;
    private String value;

    public KeyValue(String key,String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        try {
            if(value == null){
                return key +"=";
            }else{
                return key +"=" + URLEncoder.encode(value,"UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
