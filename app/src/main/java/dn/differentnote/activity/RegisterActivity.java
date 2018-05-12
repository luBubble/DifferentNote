package dn.differentnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dn.differentnote.HttpManager;
import dn.differentnote.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {
    private TextView link_to_Login;
    private EditText account;
    private EditText first_psw;
    private EditText confirm_psw;
    private Button btn_register;
    final Pattern emailPattern = Pattern.compile("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
    final Pattern phonePattern = Pattern.compile("^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        bindViews();
    }

    private void bindViews()
    {
        link_to_Login=(TextView)findViewById(R.id.link_login);
        account=(EditText)findViewById(R.id.register_account);
        first_psw=(EditText)findViewById(R.id.register_first_psw);
        confirm_psw=(EditText)findViewById(R.id.register_confirm_psw);
        btn_register=(Button) findViewById(R.id.btn_register);
//跳转登录
        link_to_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });
        //注册
        btn_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String accountStr=account.getText().toString().trim();
                        //trim()去掉首尾空格
                        String firstPswStr=first_psw.getText().toString().trim();
                        String confirmPswStr=confirm_psw.getText().toString().trim();
                        if(accountStr.equals("")||firstPswStr.equals("")||confirmPswStr.equals(""))
                        {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("提示").setMessage("请将信息填写完整！")
                                    .setPositiveButton("确定", null).show();
                        }
                        else if(!emailPattern.matcher(accountStr).matches()&&!phonePattern.matcher(accountStr).matches())
                        {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("提示").setMessage("请输入正确的手机号码或邮箱！")
                                    .setPositiveButton("确定", null).show();
                        }
                        else if(!firstPswStr.equals(confirmPswStr))
                        {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("提示").setMessage("密码与确认密码不一致！")
                                    .setPositiveButton("确定", null).show();
                        }
                        else
                        {
                            String address="http://192.168.87.1:8080/DifferentNote/user/register";
                            RequestBody requestBody=new FormBody.Builder()
                                    .add("account",accountStr)
                                    .add("psw",firstPswStr)
                                    .build();
                            HttpManager.sendOkHttpRequest(address, requestBody, new Callback()
                             {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "onFailure: 无法连接服务器");
                                    Toast.makeText(RegisterActivity.this, "无法连接服务器", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String  Msg=response.body().string();
                                    try
                                    {
                                        JSONObject MsgJson=new JSONObject(Msg);
                                        Log.d(TAG, MsgJson.getString("msg"));
                                        Toast.makeText(RegisterActivity.this, MsgJson.getString("msg"), Toast.LENGTH_LONG).show();
                                        if(MsgJson.getString("msg").equals("注册成功"))
                                        {
                                            //跳转页面
                                        }

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
        );
    }

}
