package jango.camera.activity;



import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jango.camera.R;
import jango.camera.camera;
import jango.camera.utils.Constants;
import jango.camera.utils.SettingToast;

/**
 * @Title:
 * @Description:
 * @Author:����
 * @Since:2014��4��30��
 * @Version:1.1.0
 */
public class ExposureAcitivity extends ListActivity {
    private String[] mExposureArrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        iniDatas();
    }

    private void iniDatas() {
        setTitle(R.string.txt_exposure);
        mExposureArrays = this.getResources().getStringArray(
                R.array.exposure_spinner_arrays);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mExposureArrays));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(ExposureAcitivity.this,
                camera.class);
        intent.putExtra(Constants.EXPOSURE_NUM,
                Integer.parseInt(mExposureArrays[position]));
        ExposureAcitivity.this
                .setResult(Constants.EXPOSURE_RESULT_CODE, intent);
        SettingToast.setToastStrLong(ExposureAcitivity.this,
                getString(R.string.exposure_set) + mExposureArrays[position]);
        ExposureAcitivity.this.finish();
    }

}
