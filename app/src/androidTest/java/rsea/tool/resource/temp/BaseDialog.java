package rsea.tool.resource.temp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class BaseDialog extends Dialog {
    protected DialogInterface.OnClickListener okListener;
    protected DialogInterface.OnCancelListener cancelListener;

    public BaseDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void setOkListener(DialogInterface.OnClickListener l){
        okListener = l;
    }
    public void setCancelListener(DialogInterface.OnClickListener l){
        cancelListener = (OnCancelListener) l;
    }
}
