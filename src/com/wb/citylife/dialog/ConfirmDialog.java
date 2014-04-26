package com.wb.citylife.dialog;

import com.wb.citylife.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog {
	
	/**
	 * 确认类型对话框
	 * @param context
	 * @param titleId 为-1时不显示标题
	 * @param messageId
	 * @param confirmListener
	 * @return
	 */
	public Dialog getDialog(Context context, int titleId, int messageId, DialogInterface.OnClickListener confirmListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		if(titleId != -1) {
			dialogBuilder.setTitle(titleId);
		}
		
		return dialogBuilder.setMessage(messageId)
        .setPositiveButton(R.string.dialog_confirm, confirmListener)
        .setNegativeButton(R.string.dialog_cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();                
            }
        })
        .create();
	}
	
	/**
	 * 确认类型对话框
	 * @param context
	 * @param titleId 为null时不显示标题
	 * @param messageId
	 * @param confirmListener
	 * @return
	 */
	public Dialog getDialog(Context context, String title, String message, DialogInterface.OnClickListener confirmListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		if(title != null) {
			dialogBuilder.setTitle(title);
		}
		
		return dialogBuilder.setMessage(message)
        .setPositiveButton(R.string.dialog_confirm, confirmListener)
        .setNegativeButton(R.string.dialog_cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();                
            }
        })
        .create();
	}
}
