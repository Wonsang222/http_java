package rsea.tool.resource.http;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import rsea.tool.resource.HttpBaseResource;

public class ResourceBase extends HttpBaseResource {

    private static final String TAG = "ResourceBase";

    @Override
    public JSONObject body() {
        return responseData;
    }

    @Override
    protected String charSet() {
        return "utf-8";
    }

    @Override
    protected void parsor(InputStream response) throws Exception {
        String value = CharStreams.toString(new InputStreamReader(response, Charsets.UTF_8));
        responseData = new JSONObject(value);
    }

    @Override
    public void headerParsor(HttpURLConnection conn) throws Exception {

    }

    @Override
    public void setParameter(String... param) {

    }
}
