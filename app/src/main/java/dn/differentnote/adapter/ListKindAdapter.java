package dn.differentnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dn.differentnote.R;

public class ListKindAdapter extends BaseAdapter {
    private JSONArray listkinds;
    private Context mContext;

    public ListKindAdapter(JSONArray listkinds, Context mContext) {
        this.listkinds = listkinds;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return listkinds.length();
    }

    @Override
    public Object getItem(int position)  {
        try
        {
            return listkinds.getJSONObject(position);
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

    @Override
    public View getView(int position, View view, ViewGroup parent)  {
        view= LayoutInflater.from(mContext).inflate(R.layout.list_kind_item,null);
        TextView listKindItem=view.findViewById(R.id.list_kind_item);
       try
       {
           listKindItem.setText(listkinds.getJSONObject(position).getString("lk_listname"));
       }
       catch (JSONException je)
       {
           je.printStackTrace();
       }
        return view;
    }

    public void reSet(JSONArray datas) {
        listkinds = null;
        listkinds = new JSONArray();
        listkinds=datas;
        notifyDataSetChanged();
    }

}
