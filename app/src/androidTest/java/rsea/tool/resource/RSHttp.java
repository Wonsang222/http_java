package rsea.tool.resource;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rsea.tool.resource.temp.ResourceCode;

public class RSHttp {
    public static final int FINISH_JOB_SUCCESS = 2001;
    public static final int FINISH_JOB_FAIL	 				= 2002;
    public static final int FINISH_JOB_FAIL_RESEND	 		= 2003;

    private Context mContext;
    private String key;
    private boolean isProgress = true;
    private boolean isShowingError = true;
    private RSHttpCallback callback;
    private static final String TAG = "RSHttp";

    public interface RSHttpCallback{
        void cbRSHttpSuccess(String key, HttpBaseResource... resp);
        void cbRSHttpFail(String key, int errorCode, HttpBaseResource... resp);
    }

    public RSHttp progress(boolean isProgress){
        this.isProgress = isProgress;
        return this;
    }

    public RSHttp showError(boolean isShowingError){
        this.isShowingError = isShowingError;
        return this;
    }

    public ExecutorService service = Executors.newFixedThreadPool(10);
    public void req(HttpBaseResource... resource){
        service.execute(createRunnable(isProgress, isShowingError));
    }

    private void reSendResource(Object obj){
        Object[] objs = (Object[]) obj;
        HttpBaseResource[] castResources = (HttpBaseResource[]) objs[0];
        RSHttpRunnable.HttpRunnableSetting runSetting = (RSHttpRunnable.HttpRunnableSetting) objs[1];
        service.execute();
    }

    public RSHttpRunnable createRunnable(boolean isProgress, boolean isShowingEDialog,HttpBaseResource... resources){
        RSHttpRunnable runnable = new RSHttpRunnable(mResultHandler, mContext, resources);
        runnable.setShowingEDialog(isShowingEDialog);
        runnable.setProgress(isProgress);
        return runnable;
    }

    private void onResultRunnable(Object resources){
        HttpBaseResource[] result = (HttpBaseResource[]) resources;
        if(callback != null){
            callback.cbRSHttpSuccess(key,result);
        }
    }

    private void onFailRunnable(Object obj){
        Object[] objs = (Object[]) obj;
        HttpBaseResource[] castResources = (HttpBaseResource[]) objs[0];
        Integer value = (Integer) objs[1];

        if (callback != null){
            callback.cbRSHttpFail(key,value, castResources);
        }
    }

   private Handler mResultHandler = new Handler(Looper.getMainLooper()){
      public void handleMessage(Message msg){
          switch (msg.what){
              case FINISH_JOB_SUCCESS:
                  onResultRunnable(msg.obj);
                  break;
              case FINISH_JOB_FAIL:
                  onFailRunnable(msg.obj);
                  break;
              case FINISH_JOB_FAIL_RESEND:
                  reSendResource(msg.obj);
              default:
                  break;
          }
      }
   } ;

    public static Object req(Context context, HttpBaseResource resource){
        HttpURLConnection conn = null;
        Object retVal = null;
        try{
            conn = (HttpURLConnection) resource.makeRequestRes();
            conn.setConnectTimeout(RSHttpInfo.TIMEOUT);
            conn.setReadTimeout(RSHttpInfo.TIMEOUT);
            Iterator<String> reqHeader = conn.getHeaderFields().keySet().iterator();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                resource.parsorRes(conn.getInputStream());
                retVal = resource.body();
            } else if(resource.isIgnoreError()){
                resource.parsorRes(null);
                retVal = resource.body();
            }else{
                resource.errorCode = ResourceCode.E9994;
                throw new Exception("Http ResponseCode" + conn.getResponseCode() + "," + conn.getResponseMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(conn != null){
                try{
                    conn.disconnect();
                }catch (Exception e){

                }
            }
        }
        return retVal;
    }
}
