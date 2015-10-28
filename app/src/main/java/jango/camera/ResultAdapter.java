package jango.camera;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 15-10-28.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    private Activity MyActivity;
    private List<String> mDatas;
    public ResultAdapter(Activity activity,List<String> date){
        this.MyActivity = activity;
        this.mDatas = date;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                MyActivity).inflate(R.layout.item_resultactivity, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.text.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        public MyViewHolder(View view){
            super(view);
            text = (TextView)view.findViewById(R.id.item_result_text);
        }
    }
}
