package com.hesc.csdnblog.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hesc.csdnblog.R;

import butterknife.ButterKnife;

/**
 * Created by hesc on 15/5/26.
 */
public class MessageBox {
    public static interface DialogClickListener{
        boolean onClick(View v);
    }

    /**
     * 弹出确认框，当点击窗体以外的地方时就关掉窗体
     * @param context 上下文
     * @param message 消息文本
     * @param okClickListener 点击确定按钮后回调的接口
     */
    public static void showConfirm(Context context, CharSequence message,
                                   DialogClickListener okClickListener){
        showConfirm(context, true, message, null, okClickListener);
    }

    /**
     * 弹出确认框
     * @param context 上下文
     * @param cancelable 是否点击窗体以外的地方就关掉窗体
     * @param message 消息文本
     * @param cancelClickListener 点击取消按钮后回调的接口
     * @param okClickListener 点击确定按钮后回调的接口
     */
    public static void showConfirm(Context context, boolean cancelable,
                                   CharSequence message,
                                   DialogClickListener cancelClickListener,
                                   DialogClickListener okClickListener){
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_text, null);
        TextView messageView = (TextView) contentView.findViewById(R.id.tv_message);
        messageView.setText(message);
        showConfirm(context, cancelable, contentView, cancelClickListener, okClickListener);
    }

    /**
     * 弹出确认框
     * @param context 上下文
     * @param contentView 内容视图
     * @param cancelable 是否点击窗体以外的地方就关掉窗体
     * @param cancelClickListener 点击取消按钮后回调的接口
     * @param okClickListener 点击确定按钮后回调的接口
     */
    public static void showConfirm(Context context, boolean cancelable,
                                   View contentView,
                                   DialogClickListener cancelClickListener,
                                   DialogClickListener okClickListener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(cancelable)
                .create();

        ViewGroup containerView = (ViewGroup) view.findViewById(R.id.ll_content);
        TextView cancelView = (TextView) view.findViewById(R.id.btn_cancel);
        TextView okView = (TextView) view.findViewById(R.id.btn_ok);
        //添加内容视图
        containerView.addView(contentView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        //点击取消按钮
        cancelView.setOnClickListener(v->{
            if(cancelClickListener!=null){
                boolean consumeClick = cancelClickListener.onClick(cancelView);
                if(consumeClick)
                    dialog.dismiss();
            } else {
                dialog.dismiss();
            }
        });
        //点击确定按钮
        okView.setOnClickListener(v->{
            if(okClickListener != null){
                boolean consumeClick = okClickListener.onClick(okView);
                if(consumeClick)
                    dialog.dismiss();
            } else {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
