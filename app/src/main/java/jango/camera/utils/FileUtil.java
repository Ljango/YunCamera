package jango.camera.utils;

import java.io.File;

import android.os.Environment;
import android.util.Log;

/**
 * @Title:
 * @Description:
 * @Author:����
 * @Since:2014��4��30��
 * @Version:1.1.0
 */
public class FileUtil {
    private final static String TAG = "FileUtil";

    /** Create a File for saving an image */
    public static File getOutputMediaFile() {

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "CustomCamera001");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.v(TAG, "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = DateUtil.getFormatTime("yyyyMMddhhmmss");
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

}
