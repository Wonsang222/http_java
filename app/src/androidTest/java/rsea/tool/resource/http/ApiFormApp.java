package rsea.tool.resource.http;

import android.content.Context;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;

import rsea.tool.resource.temp.ResourceCode;
import rsea.tool.resource.temp.ResourceInfo;

@ResourceInfo(url = "/api/api_from_app.exe.php")
public class ApiFormApp extends ResourceBase {
    public ApiFormApp(Context context) {
        super();
        ap("device", "android");
        try {
            ap("device_id", WInfo.getUUID(context));
            ap("country_code", WInfo.getCountryCode(context));
        } catch (Exception e) {

        }
        ap("os_version", WInfo.getSdkVersion());
        ap("app_version", WInfo.getVersion(context));
        if (!WInfo.getAccountID(context).isEmpty()) {
            ap("account_id", WInfo.getAccountID(context));
        }
        getReqHeaders().put("core_version", AppUtils.CORE_VERSION);
    }

    @Override
    protected void parsor(InputStream response) throws Exception {
        String value = CharStreams.toString(new InputStreamReader(response, Charsets.UTF_8));
        responseData = new JSONObject(value);
        if (!responseData.optBoolean("success", true)){
            errorCode = ResourceCode.SERVER_ERROR;
            errorMsg = responseData.getString("msg");
        } else {
            errorCode = ResourceCode.SUCCESS;
        }
    }
}

