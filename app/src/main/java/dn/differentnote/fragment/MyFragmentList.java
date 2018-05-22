package dn.differentnote.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dn.differentnote.HttpManager;
import dn.differentnote.R;
import dn.differentnote.activity.ListActivity;
import dn.differentnote.adapter.ListKindAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragmentList extends Fragment implements AdapterView.OnItemClickListener{
    private JSONArray listKinds;
    private ListView lv_listKinds;
    private TextView thingsToday;
    private TextView addListKind;
    public ListKindAdapter listKindAdapter;
    private String userId;
    private EditText nameEdit;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_list,container,false);
        userId=getArguments().getString("user_id");//获取用户id
        handler=new Handler();
        bindView(view);
        setListKinds();
        return view;
    }

    //点击一条清单分类
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            String listKindId=listKinds.getJSONObject(position).getString("lk_listkindid");
            Bundle thisList=new Bundle();
            thisList.putString("user_id",userId);
            thisList.putString("listkind_id",listKindId);
            Intent listIntent=new Intent(getActivity(),ListActivity.class);
            listIntent.putExtras(thisList);//传递用户id和清单分类id
            startActivity(listIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            reSetListKinds();
        }
    }

    //绑定元件
    public void bindView(View view)
    {
        thingsToday=(TextView)view.findViewById(R.id.things_today_text);
        addListKind=(TextView)view.findViewById(R.id.add_list_kind);
        lv_listKinds=(ListView)view.findViewById(R.id.listview_list_kind);
        lv_listKinds.setOnItemClickListener(this);//绑定item监事件

        //添加清单分类
        addListKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框
                View newLKDialog = LayoutInflater.from(getActivity()).inflate(R.layout.input_list_kind_name, null);
                nameEdit = (EditText) newLKDialog.findViewById(R.id.input_list_kind_name);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                       .setIcon(R.mipmap.icon)//设置标题的图片
                        .setTitle("新建清单分类")//设置对话框的标题
                        .setView(newLKDialog)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String listKindName = nameEdit.getText().toString();
                                //新建清单分类
                                String addListKindUrl = "http://192.168.87.1:8080/DifferentNote/listKind/addListKind";
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("userId", userId)
                                        .add("listKindName",listKindName)
                                        .build();
                                HttpManager.sendOkHttpRequest(addListKindUrl, requestBody, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Looper.prepare();//解决线程 1
                                        Toast.makeText(getActivity(), "无法连接服务器", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        Looper.prepare();//解决线程 1
                                        Toast.makeText(getActivity(), "新增成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                });
                                dialog.dismiss();
                                reSetListKinds();
                            }
                        }).create();
                dialog.show();
            }
        });

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart: 这里");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume: 哈哈哈");
//    }

    //第一次填充数据
    public void setListKinds()
    {
        //读取清单分类
        String getlistKindsUrl = "http://192.168.87.1:8080/DifferentNote/listKind/getListKinds";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .build();
        HttpManager.sendOkHttpRequest(getlistKindsUrl, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 无法连接服务器");
                Looper.prepare();//解决线程 1
                Toast.makeText(getActivity(), "无法连接服务器", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String Msg = response.body().string();
                try {
                    JSONObject dataJson = new JSONObject(Msg);
                    Log.d(TAG, dataJson.getString("msg"));
                    listKinds=dataJson.getJSONArray("data");
                    if (dataJson.getString("msg").equals("读取成功")&&listKinds.length()>0) {
                        //填充清单分类数据
                        listKindAdapter=new ListKindAdapter(listKinds,getActivity());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                lv_listKinds.setAdapter(listKindAdapter);
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //更新数据
    public void reSetListKinds()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://192.168.87.1:8080/DifferentNote/listKind/getListKinds?userId="+userId)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        String Msg = response.body().string();
                        JSONObject dataJson = new JSONObject(Msg);
                        listKinds=dataJson.getJSONArray("data");
                        if(listKinds.length()>0)
                        {
                            Log.d(TAG, "tempListKinds"+listKinds.toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listKindAdapter.reSet(listKinds);
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
