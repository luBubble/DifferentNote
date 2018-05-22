package dn.differentnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import dn.differentnote.HttpManager;
import dn.differentnote.R;
import dn.differentnote.adapter.ListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ListActivity extends Activity {
    private TextView back;
    private TextView addList;
    private ListView lists;
    private ListView listDone;
    private Button showListDone;
    private String userId;
    private String listKindId;
    private JSONArray listData;
    private ListAdapter listAdapter;
    private Handler handler;

    @Override
    protected void onResume() {
        super.onResume();
        if(listData==null)
        {
            setLists();
        }
        else
        {
            reSetLists();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        userId=getIntent().getExtras().getString("user_id");
        listKindId=getIntent().getExtras().getString("listkind_id");
        handler=new Handler();
        setLists();
        bindView();
    }
    public void bindView()
    {
        back=(TextView)findViewById(R.id.btn_back);
        addList=(TextView)findViewById(R.id.add_list);
        lists=(ListView) findViewById(R.id.listview_list);
        listDone=(ListView) findViewById(R.id.listview_done_list);
        showListDone=(Button)findViewById(R.id.show_done_list);
        //长按删除
        lists.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        //添加清单
        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addListIntent=new Intent(ListActivity.this,AddListActivity.class);
                Bundle thisList=new Bundle();
                thisList.putString("userId",userId);
                thisList.putString("listKindId",listKindId);
                addListIntent.putExtras(thisList);
                startActivity(addListIntent);
            }
        });
        //返回
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
    //第一次填充数据
    public void setLists()
    {
        //读取清单分类
        String getlistKindsUrl = "http://192.168.87.1:8080/DifferentNote/list/getLists";
        RequestBody requestBody = new FormBody.Builder()
                .add("listKindId", listKindId)
                .build();
        HttpManager.sendOkHttpRequest(getlistKindsUrl, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 无法连接服务器");
                Looper.prepare();//解决线程 1
                Toast.makeText(ListActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String Msg = response.body().string();
                try {
                    JSONObject dataJson = new JSONObject(Msg);
                    Log.d(TAG, dataJson.getString("msg"));
                    listData=dataJson.getJSONArray("data");
                    if (dataJson.getString("msg").equals("读取成功")&&listData.length()>0) {
                        //填充清单分类数据
                        listAdapter=new ListAdapter(listData,ListActivity.this);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                        lists.setAdapter(listAdapter);

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(ListActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //更新数据
    public void reSetLists()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://192.168.87.1:8080/DifferentNote/list/getLists?listKindId="+listKindId)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        String Msg = response.body().string();
                        JSONObject dataJson = new JSONObject(Msg);
                        listData=dataJson.getJSONArray("data");
                        if(listData.length()>0)
                        {
                            Log.d(TAG, "listData"+listData.toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.reSet(listData);
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(ListActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
