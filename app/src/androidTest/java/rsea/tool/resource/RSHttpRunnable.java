package rsea.tool.resource;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import java.util.Objects;

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





    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

    @Override
    public void run() {

    }
}
