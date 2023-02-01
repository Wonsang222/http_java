package rsea.tool.resource;

import android.content.Context;
import android.media.MediaParser.ParsingException;
import android.security.identity.InvalidRequestMessageException;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import rsea.tool.resource.temp.ResourceCode;
import rsea.tool.resource.temp.ResourceInfo;

public abstract class HttpBaseResource {

    public int errorCode = ResourceCode.SUCCESS;
    public String errorMsg = null;
    protected boolean isMultiPart = false;
    protected boolean ignoreError = false;
    protected static final String CRLF = "/r/n";
    protected String req_url = null;
    public Object tag;
    protected List<KeyValue> params = new ArrayList<KeyValue>();
    protected JSONObject responseData;
    protected HashMap<String, String> reqHeaders = new HashMap<>();
    public ParamFactory paramFactory = null;

    public interface ParamFactory {
        public void interceptParam(HttpBaseResource beforeRes, HttpBaseResource interceptRes);
    }

    public HttpBaseResource(){

    }

    public abstract JSONObject body();
    public URLConnection makeRequestRes() throws Exception{
        URLConnection retVal;
        retVal = makeRequest();
        return retVal;
    }


    // parsor 가 child 객체마다 바뀔것

    protected abstract String charSet();
    protected abstract void parsor(InputStream response) throws Exception;
    public abstract void headParsor(HttpURLConnection conn) throws Exception;
    public abstract void setParameter(String... param);

    public HashMap<String, String> getReqHeaders(){
        return reqHeaders;
    }

    public void setParameter(ParamFactory factory){paramFactory = factory;}
    public void setParameter(ArrayList<KeyValue> params){
        this.params.clear();
        this.params.addAll(params);
    }

    public void parsorRes(InputStream response) throws ParsingException{
        try{
            parsor(response);
        }catch (Exception e){
        }
    }
    public String B64decode(String B64Data){
        String encodecredentials = new String(Base64.decode(B64Data, 0));
        return encodecredentials;
    }

    public List<KeyValue> getParams(){return params;}
    public void setTag(Object tag){this.tag = tag;}
    public Object getTag() {return tag;}

    public String generateParamter(){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < params.size() ; i++) {
            if(i==0){
                buffer.append(params.get(i).toString());
            }else{
                buffer.append("&"+params.get(i).toString());
            }
        }
        return buffer.toString();
    }

    public void trustAllHosts(){
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected URLConnection makeRequest() throws Exception{
        if (params == null){
            throw new InvalidRequestMessageException("no request param");
        }
        String boundary = null;
        ResourceInfo resourceInfo = getClass().getAnnotations(ResourceInfo.class);
        if (resourceInfo != null && !resourceInfo.url().isEmpty()){
           req_url = resourceInfo.url();
        }
        if (resourceInfo.method().equals("GET")){
            req_url = req_url + "?" + generateParamter();
        }
        URL url;
        if(req_url.startsWith("http://") || req_url.startsWith("https://")){
            url = new URL(req_url);
        } else {
            if(AppProperties.getInstance().get("app.id").startsWith("com.mywisa.kollshopsg.magicapp")){
                url = new URL("http://118.129.243.248:8109" + req_url);
            }else{
                url = new URL(RSHttpInfo.HOST + req_url);
                Log.d("여기에", "" + RSHttpInfo.HOST + req_url);
            }
        }
        HttpURLConnection conn;
        if(url.toString().startsWith("https")){
            trustAllHosts();
            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true
                }
            });
            conn = httpsConn;
        }
        else{
            conn = (HttpURLConnection) url.openConnection();
        }

        HashMap<String, String> header = getReqHeaders();
        for (String key:header.keySet()) {
            conn.setRequestProperty(key, header.get(key));
        }
        if (resourceInfo != null){
            conn.setRequestMethod(resourceInfo.method());
        } else {
            conn.setRequestMethod("POST");
        }

        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        if(conn.getRequestMethod().equals("GET")){

        }else{
            if(isMultiPart){
                boundary = "===" + System.currentTimeMillis() + "===";
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                writeMultiPartData(boundary, conn.getOutputStream());
                os.flush();
            } else {
                OutputStream os = conn.getOutputStream();
                os.write(generateParamter().getBytes(charSet()));
                os.flush();
            }
        }
        return conn;
    }

    private void writeMultiPartData(String boundary,OutputStream out) throws IOException {
        String delimiter = "--" + boundary + CRLF;
        for(KeyValue param : params){
            if(param.getKey().startsWith("$")){
                writeFilePart(delimiter,out,param.getKey().replaceAll("\\$",""),param.getValue());
            }else{
                writeTextBody(delimiter,out,param.getKey(),param.getValue());
            }
        }
        out.write(("--" + boundary + "--").getBytes(charSet()));
        out.write(CRLF.getBytes(charSet()));
    }

    public HttpBaseResource addParameter(String... param) {
        return ap(param[0],param[1]);
    }

    public static @NonNull HttpBaseResource instance(Class cls, Context context){
        try{
            Constructor constructor = cls.getConstructor(Context.class);
            return (HttpBaseResource) constructor.newInstance(context);
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @NonNull HttpBaseResource instance(Class cls) {
        try {
            Constructor constructor = cls.getConstructor();
            return (HttpBaseResource) constructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpBaseResource ap(String key, String value){
        params.add(new KeyValue(key, value));
        return this;
    }

    public boolean isIgnoreError(){ return ignoreError;}

    public HttpBaseResource useMultiPart(){
        this.isMultiPart = true;
        return this;
    }
    }


