package pony.xcode.common;

import android.text.Editable;
import android.text.TextWatcher;

/*editText输入监听器*/
public abstract class SimpleTextWatcher implements TextWatcher {
    /**
     * @param charSequence 文本改变之前的内容
     * @param start        被替换文本区域起点位置
     * @param count        将被替换的文本区域字符数目
     * @param after        替换后的文本字符数目
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    /**
     * @param charSequence 文本改变之后的内容
     * @param start        被替换文本区域起点位置,setText时是替换所有内容,此时数值为0
     * @param before       被替换之前的文本区域字符数目
     * @param count        替换后的文本字符数目
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    /**
     * @param editable 文本改变之后的内容
     */
    @Override
    public void afterTextChanged(Editable editable) {

    }
}
