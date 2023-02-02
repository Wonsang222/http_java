package rsea.tool.resource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import rsea.tool.resource.temp.ResourceCode;

public class RSHttpPopup extends AlertDialog {

    public RSHttpPopup(Context context) {
        super(context);
    }

    private static RSHttpPopup mDialog;

    public  int errorCode;
    public void setErrorCode(int errorCode){this.errorCode = errorCode;}
    public static RSHttpPopup getPopup(Context context, int errorCode, String message, final OnDismissListener listener){
        if (mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
        if (message == null) message ="";
        mDialog = new RSHttpPopup(context);
        mDialog.setTitle("알림");
        mDialog.setErrorCode(errorCode);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage(Html.fromHtml(message));
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mDialog.setOnDismissListener(listener);
        return mDialog;
    }

    public static RSHttpPopup getPopup(Context context, int errorCode, final OnDismissListener listener){
        if(mDialog!=null){
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new RSHttpPopup(context);
        mDialog.setTitle("알림");
        mDialog.setErrorCode(errorCode);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage(ResourceCode.getErrorMsg(errorCode));
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                listener.onDismiss(mDialog);
            }
        });
        return mDialog;
    }
}
