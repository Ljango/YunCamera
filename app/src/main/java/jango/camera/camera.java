package jango.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import jango.camera.activity.ExposureAcitivity;
import jango.camera.utils.Constants;
import jango.camera.utils.FileUtil;
import jango.camera.utils.SettingToast;

/**
 * @Title:
 * @Description:
 * @Author:辉清
 * @Since:2014年4月30日
 * @Version:1.1.0
 */

/**
 * @Title:
 * @Description:
 * @Author:辉清
 * @Since:2014年4月30日
 * @Version:1.1.0
 */
public class camera extends Activity implements OnClickListener {

    private final static String TAG = "CameraActivity";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private File mPictureFile;
    private Button mBtnSave;
    private TextView mExposureTv;
    private int mExposureNum = 0;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initViews();
        initDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview); // components
        mSurfaceHolder = mSurfaceView.getHolder();
        mBtnSave = (Button) findViewById(R.id.save_pic);
        mBtnSave.setOnClickListener(this);
        mExposureTv = (TextView) findViewById(R.id.exposure);
        mSharedPreferences = getSharedPreferences(Constants.EXPOSURE,
                MODE_WORLD_WRITEABLE);
        mEditor = mSharedPreferences.edit();
    }

    private void initDatas() {
        mSurfaceHolder.addCallback(surfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mExposureTv.setOnClickListener(this);
        mExposureNum = mSharedPreferences.getInt(Constants.EXPOSURE_NUM, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.EXPOSURE_REQUEST_CODE:
                if (resultCode == Constants.EXPOSURE_RESULT_CODE) {
                    if (data != null) {
                        mExposureNum = data.getExtras().getInt(
                                Constants.EXPOSURE_NUM);
                        mEditor.putInt(Constants.EXPOSURE_NUM, mExposureNum);
                        mEditor.commit();
                    }
                }

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA
                || keyCode == KeyEvent.KEYCODE_SEARCH) {
            takePic();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 图片回调
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        // @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
            camera.startPreview();
        }
    };

    // 保存图片
    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Message message = mHandler.obtainMessage();
                message.what = Constants.IS_HAVE_SDCARD;
                message.obj = getString(R.string.have_sdcard);
                mHandler.sendMessage(message);
                return null;

            }
            mPictureFile = FileUtil.getOutputMediaFile();
            try {
                FileOutputStream fos = new FileOutputStream(
                        mPictureFile.getPath()); // Get
                // stream
                fos.write(params[0]); // Written to the file
                Message message = mHandler.obtainMessage();
                message.what = Constants.UPDATA_TOAST_MSG;
                message.obj = getString(R.string.photo_save_path);
                mHandler.sendMessage(message);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            Log.v(TAG, "surfaceCallback====");
            mCamera = Camera.open(); // Turn on the camera
            try {
                mCamera.setPreviewDisplay(holder); // Set Preview
            } catch (IOException e) {
                mCamera.release();// release camera
                mCamera = null;
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.v(TAG, "====surfaceChanged");
            mParameters = mCamera.getParameters();
            if (mExposureNum == 0) {
                mExposureNum = mParameters.getExposureCompensation();
            }
            mExposureTv.setText("当前曝光度为：" + mExposureNum + " " + "点击可选择");
            mParameters.setExposureCompensation(mExposureNum);
            if (Integer.parseInt(Build.VERSION.SDK) >= 8)
                setDisplayOrientation(mCamera, 90);
            else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mParameters.set("orientation", "portrait");
                    mParameters.set("rotation", 90);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mParameters.set("orientation", "landscape");
                    mParameters.set("rotation", 90);
                }
            }
            mParameters.setPictureFormat(PixelFormat.JPEG);
            List<Size> sizes = mParameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(sizes, width, height);
            mParameters.setPictureSize(optimalSize.width, optimalSize.height);

            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(TAG, "====surfaceDestroyed");
            mCamera.stopPreview();// stop preview
            mCamera.release(); // Release camera resources
            mCamera = null;
        }
    };

    /**
     * @param sizes
     * @param w
     * @param h
     * @return
     * @Description:
     */
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            Log.v(TAG, "Checking size " + size.width + "w " + size.height + "h");
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * @param camera
     * @param angle
     * @Description:
     */
    protected void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod(
                    "setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        } catch (Exception e1) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_pic:
                takePic();
                break;
            case R.id.exposure:
                exposureListener();
                break;
            default:
                break;
        }

    }

    private void takePic() {
        mCamera.stopPreview();// stop the previe
        mCamera.takePicture(null, null, pictureCallback); // picture
    }

    private void exposureListener() {
        Intent intent = new Intent(camera.this, ExposureAcitivity.class);
        startActivityForResult(intent, Constants.EXPOSURE_REQUEST_CODE);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.UPDATA_TOAST_MSG:
                    SettingToast.setToastStrLong(camera.this,
                            msg.obj.toString() + mPictureFile.getPath());
                    break;
                case Constants.IS_HAVE_SDCARD:
                    SettingToast.setToastStrLong(camera.this,
                            msg.obj.toString());
                    break;
                default:
                    break;
            }
        }

    };
}