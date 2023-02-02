package rsea.tool.resource;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaParser;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.test.espresso.base.Default;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Objects;

import rsea.tool.resource.temp.ResourceCode;

public class RSHttpRunnable implements Runnable, DialogInterface.OnDismissListener {

    public static class HttpRunnableSetting {
        public boolean isprogressingDialog = true;
        public boolean isShowingEDialog = true;
    }

    private static final int THREA_PRE = 1001;
    private static final int THREA_POST = 1002;

    private RSHttpProgressDialog mProgress;
    private Handler mExcuterHandler;
    private Context mContext;
    private HttpBaseResource[] mResources;
    private HttpRunnableSetting mSetting = new HttpRunnableSetting();

    private DialogInterface.OnClickListener mResendingListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Object[] objs = new Objects [2];
            objs[0] = mResources;
            objs[1] = mSetting;
            mExcuterHandler.sendMessage(Message.obtain(mExcuterHandler, RSHttp.FINISH_JOB_FAIL_RESEND, objs));
        }
    };

    public RSHttpRunnable(Handler ex, Context context, HttpBaseResource[] resources){
        mExcuterHandler = ex;
        mContext = context;
        mResources = resources;
    }

    public void setProgress(boolean b){
        mSetting.isprogressingDialog = b;
    }

    public void setShowingEDialog(boolean b){
        mSetting.isShowingEDialog = b;
    }

    private void onPostExecute(){
        hideDialog();
        for (HttpBaseResource resource:mResources ) {
            switch (resource.errorCode){
                case ResourceCode
                        .SERVER_ERROR:
                    if (mSetting.isShowingEDialog){
                    }
                    return;
                case ResourceCode.SUCCESS:
                    continue;
                default:
                    if (mSetting.isShowingEDialog){

                    }else{
                        Object[] obj = new Object[2];
                        obj[0] = mResources;
                        obj[1] = resource.errorCode;
                        mExcuterHandler.sendMessage(Message.obtain(mExcuterHandler, RSHttp.FINISH_JOB_FAIL, obj));
                    }
                    return;
            }
        }
        mExcuterHandler.sendMessage(Message.obtain(mExcuterHandler, RSHttp.FINISH_JOB_SUCCESS, mResources));
    }

    private void onPreExecute() {
        Log.d("나야나", "1");
        showDialog();
    }

    public Handler mRunnableHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case THREA_PRE:
                    onPreExecute();
                    break;
                case THREA_POST:
                    onPostExecute();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onDismiss(DialogInterface dialogInterface) {


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        mRunnableHandler.sendEmptyMessage(THREA_PRE);
        for (int i = 0; i < mResources.length ; i++) {
            HttpBaseResource param = mResources[i];
            if (i != 0 && param.paramFactory != null){
                param.paramFactory.interceptParam(mResources[i - 1], param);

            }

            HttpURLConnection conn = null;
            try{
                param.getReqHeaders().put("User-Agent", Winfo.getPackage(mContext)+"/"+WInfo.getVersion(mContext));
                conn = (HttpURLConnection) param.makeRequestRes();
                conn.setConnectTimeout(RSHttpInfo.TIMEOUT);
                conn.setReadTimeout(RSHttpInfo.TIMEOUT);
                Iterator<String> reqHeader = conn.getHeaderFields().keySet().iterator();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK);{
                    param.headerParsor(conn);
                    param.parsorRes(conn.getInputStream());
                }
                conn.disconnect();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (MediaParser.ParsingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mRunnableHandler.sendEmptyMessage(THREA_POST);
    }

    private void showDialog(){
        mProgress = new RSHttpProgressDialog(mContext);
    }

    private void hideDialog(){
        if (mSetting.isprogressingDialog){

        }
    }
}
