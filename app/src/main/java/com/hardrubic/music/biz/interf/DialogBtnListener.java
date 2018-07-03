package com.hardrubic.music.biz.interf;

import android.content.DialogInterface;

/**
 * 通用Dialog按钮回调
 */
public interface DialogBtnListener {
    void onClickOkListener(DialogInterface dialog);

    void onClickCancelListener(DialogInterface dialog);
}
