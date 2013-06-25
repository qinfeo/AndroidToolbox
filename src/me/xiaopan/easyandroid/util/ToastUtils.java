package me.xiaopan.easyandroid.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具箱
 */
public class ToastUtils {

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param resId 显示内容资源ID
	 */
	public static final void toastL(Context context, int resId){
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param resId 显示内容资源ID
	 */
	public static final void toastS(Context context, int resId){
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param content 显示内容
	 */
	public static final void toastL(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param content 显示内容
	 */
	public static final void toastS(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param formatResId 被格式化的字符串资源的ID
	 * @param args 参数数组
	 */
	public static final void toastL(Context context, int formatResId, Object... args){
		Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param formatResId 被格式化的字符串资源的ID
	 * @param args 参数数组
	 */
	public static final void toastS(Context context, int formatResId, Object... args){
		Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_SHORT).show();
	}

	/**
	 * 吐出一个显示时间较长的提示
	 * @param context 上下文对象
	 * @param format 被格式化的字符串
	 * @param args 参数数组
	 */
	public static final void toastL(Context context, String format, Object... args){
		Toast.makeText(context, String.format(format, args), Toast.LENGTH_LONG).show();
	}

	/**
	 * 吐出一个显示时间较短的提示
	 * @param context 上下文对象
	 * @param format 被格式化的字符串
	 * @param args 参数数组
	 */
	public static final void toastS(Context context, String format, Object... args){
		Toast.makeText(context, String.format(format, args), Toast.LENGTH_SHORT).show();
	}
	
}