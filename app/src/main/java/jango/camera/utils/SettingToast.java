package jango.camera.utils;

import android.content.Context;
import android.widget.Toast;


/**
 *@Title:
 *@Description:
 *@Author:����
 *@Since:2014��4��30��
 *@Version:1.1.0
 */
public class SettingToast {
    public static void setToastIntShort(Context context, int index) {

        Toast.makeText(context, index, Toast.LENGTH_SHORT).show();

    }

    public static void setToastStrShort(Context context, String text) {

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }

    public static void setToastIntLong(Context context, int index) {

        Toast.makeText(context, index, Toast.LENGTH_LONG).show();

    }

    public static void setToastStrLong(Context context, String text) {

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }
}
