package pony.xcode.dialog;

import android.content.Context;

import pony.xcode.base.R;

/*仿ios加载等待框*/
public class ImitationLoadingDialog extends LoadingDialog{

    public ImitationLoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_imitation_loading_dialog;
    }
}
