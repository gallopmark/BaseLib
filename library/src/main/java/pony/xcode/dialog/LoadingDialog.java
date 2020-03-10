package pony.xcode.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pony.xcode.base.CommonDialog;
import pony.xcode.base.R;

/*通用加载等待对话框*/
public class LoadingDialog extends CommonDialog {
    private CharSequence mMessage;

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_common_loadingdialog;
    }

    public LoadingDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    @Override
    protected void initView(View contentView) {
        TextView tvMessage = contentView.findViewById(R.id.tv_loading_message);
        if (!TextUtils.isEmpty(mMessage)) {
            tvMessage.setText(mMessage);
            tvMessage.setVisibility(View.VISIBLE);
        } else {
            tvMessage.setVisibility(View.GONE);
        }
    }
}
