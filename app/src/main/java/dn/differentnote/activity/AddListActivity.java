package dn.differentnote.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import dn.differentnote.HttpManager;
import dn.differentnote.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class AddListActivity extends Activity {
    private EditText startTime;
    private EditText endTime;
    private EditText alarmTime;
    private EditText headLine;
    private EditText content;
    private Button btnSave;
    private Button btnCancel;
    private TextView back;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMin;
    private String editFlag;

    private Spinner repeat;
    //判断是否为刚进去时触发onItemSelected的标志
    private String userId;
    private String listKindId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list);
        userId=getIntent().getExtras().getString("userId");
        listKindId=getIntent().getExtras().getString("listKindId");
        editFlag="";
//获取当前时间
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        mHour = ca.get(Calendar.HOUR);
        mMin = ca.get(Calendar.MINUTE);
        bindView();
    }
    public void bindView()
    {
        startTime=(EditText)findViewById(R.id.start_time);
        endTime=(EditText)findViewById(R.id.end_time);
        alarmTime=(EditText)findViewById(R.id.alarm_time);
        headLine=(EditText)findViewById(R.id.list_headline);
        content=(EditText)findViewById(R.id.list_content);
        repeat=(Spinner)findViewById(R.id.spinner_repeat);
        btnSave=(Button)findViewById(R.id.btn_save);
        btnCancel=(Button)findViewById(R.id.btn_cancel);
        back=(TextView)findViewById(R.id.btn_back);
        //保存
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sTime=startTime.getText().toString().trim();
                String eTime=endTime.getText().toString().trim();
                String aTime=alarmTime.getText().toString().trim()+":00";
                String listHeadline=headLine.getText().toString().trim();
                String listContent=content.getText().toString().trim();
                String repeatStr=repeat.getSelectedItem().toString().trim();
                String repeatInt="";
                switch (repeatStr)
                {
                    case "不重复":
                        repeatInt="-1";
                        break;
                    case "仅一次":
                        repeatInt="0";
                        break;
                    case "每一天":
                        repeatInt="1";
                        break;
                    case "每一周":
                        repeatInt="7";
                        break;
                    case "每一月":
                        repeatInt="30";
                        break;
                }
                String address = "http://192.168.87.1:8080/DifferentNote/list/addList";
                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", userId)
                        .add("listKindId", listKindId)
                        .add("startDay", sTime)
                        .add("endDay", eTime)
                        .add("alarmTime", aTime)
                        .add("headLine", listHeadline)
                        .add("content", listContent)
                        .add("repeat", repeatInt)
                        .build();
                HttpManager.sendOkHttpRequest(address, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG,"新增清单失败，服务器错误");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Looper.prepare();//解决线程 1
                        Log.d(TAG,"新增清单成功");
                        Toast.makeText(AddListActivity.this, "新增清单成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
            }
        });
        //返回
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //调用时间选择器
        startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    //TODO 调用时间选择器
                    editFlag="startTime";
                    new DatePickerDialog(AddListActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
                }
            }
        });
        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    //TODO 调用时间选择器
                    editFlag="startTime";
                    new DatePickerDialog(AddListActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
                }
            }
        });
        alarmTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    //TODO 调用时间选择器
                    new TimePickerDialog(AddListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour=hourOfDay;
                            mMin=minute;
                            if (mMin < 10){
                                alarmTime.setText(mHour+":"+"0"+mMin);
                            }else {
                                alarmTime.setText(mHour+":"+mMin);
                            }
                        }
                    },0,0,true).show();
                }
            }
        });

    }
    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            }
            switch (editFlag)
            {
                case "startTime":
                    startTime.setText(days);
                    break;
                case "endTime":
                    endTime.setText(days);
                    break;
            }
        }
    };

}
