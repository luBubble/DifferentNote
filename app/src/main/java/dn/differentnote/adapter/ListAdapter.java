package dn.differentnote.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Date;
import java.util.Calendar;

import dn.differentnote.R;



public class ListAdapter extends BaseAdapter {
    private JSONArray lists;
    private Context mContext;

    public ListAdapter(JSONArray lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return lists.length();
    }

    @Override
    public Object getItem(int position)  {
        try
        {
            return lists.getJSONObject(position);
        }
        catch (JSONException je)
        {
            return je;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View view, ViewGroup parent)  {
        view= LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
        TextView headline=view.findViewById(R.id.list_item_headline);
        TextView endTime=view.findViewById(R.id.list_item_endtime);
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH)+1;
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        Date today=Date.valueOf(mYear+"-"+mMonth+"-"+mDay);
        try
        {
            headline.setText(lists.getJSONObject(position).getString("l_headline"));
            Date endTimeDate=Date.valueOf(lists.getJSONObject(position).getString("l_endtime"));
            if(endTimeDate.compareTo(today)==-1)
            {
                endTime.setTextColor(R.color.red);
                endTime.setBackgroundResource(R.drawable.list_danger_shape);
            }
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
        return view;
    }

    public void reSet(JSONArray datas) {
        lists = null;
        lists = new JSONArray();
        lists=datas;
        notifyDataSetChanged();
    }
}
