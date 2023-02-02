package rsea.tool.resource.http;

import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ResourceWingLogin extends ResourceBase {
    private static final String COOKIES_HEADER = "Set-Cookie";
    private JSONObject response = new JSONObject();

    public ResourceWingLogin(String api_url){
        super();
        this.req_url = api_url+"/main/exec.php";
    }

    @Override
    public JSONObject body() {
        return response;
    }

    @Override
    public HashMap<String, String> getReqHeaders() {
        HashMap<String, String> header = super.getReqHeaders();
        header.put("Referer", this.req_url);
        return header;
    }

    protected String charSet() {
        return "utf-8";
    }

    @Override
    protected void parsor(InputStream response) throws Exception {
        String value = CharStreams.toString(new InputStreamReader(response, Charset.forName("UTF-8")));
        Pattern p  = Pattern.
    }

    @Override
    public void headerParsor(HttpURLConnection conn) throws Exception {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookieHeader = headerFields.get(COOKIES_HEADER);
        JSONArray array = new JSONArray();
        if (cookieHeader != null){
            for (String cookie: cookieHeader) {
                String cookieName = HttpCookie.parse(cookie).get(0).getName();
                String cookieValue = HttpCookie.parse(cookie).get(0).getValue();
                if(cookieName.endsWith("SESSID")|| cookieName.endsWith("session")){
                    array.put(cookieName + "=" + cookieValue);
                }
            }
        }
        response.put("cookie",array);
    }
}
