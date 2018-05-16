package dn.differentnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import dn.differentnote.HttpManager;
import dn.differentnote.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {
    private TextView link_to_register;
    private EditText account;
    private EditText psw;
    private Button btn_login;
    final Pattern emailPattern = Pattern.compile("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
    final Pattern phonePattern = Pattern.compile("^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        bindViews();
    }

    private void bindViews() {
        link_to_register = (TextView) findViewById(R.id.link_register);
        account = (EditText) findViewById(R.id.login_account);
        psw = (EditText) findViewById(R.id.login_psw);
        btn_login = (Button) findViewById(R.id.btn_login);
        //跳转注册
        link_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });

        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String accountStr = account.getText().toString().trim();
                        //trim()去掉首尾空格
                        String pswStr = psw.getText().toString().trim();
                        if (accountStr.equals("") || pswStr.equals("") ) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("提示").setMessage("请将信息填写完整！")
                                    .setPositiveButton("确定", null).show();
                        } else if (!emailPattern.matcher(accountStr).matches() && !phonePattern.matcher(accountStr).matches()) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("提示").setMessage("请输入正确的手机号码或邮箱！")
                                    .setPositiveButton("确定", null).show();
                        }else {
                            String address = "http://192.168.87.1:8080/DifferentNote/user/login";
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("account", accountStr)
                                    .add("psw", pswStr)
                                    .build();
                            HttpManager.sendOkHttpRequest(address, requestBody, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "onFailure: 无法连接服务器");
                                    Looper.prepare();//解决线程 1
                                    Toast.makeText(LoginActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String Msg = response.body().string();
                                    try {
                                        JSONObject MsgJson = new JSONObject(Msg);
                                        Log.d(TAG, MsgJson.getString("msg"));
                                        Looper.prepare();//解决线程 1
                                        Toast.makeText(LoginActivity.this, MsgJson.getString("msg"), Toast.LENGTH_SHORT).show();
                                        if (MsgJson.getString("msg").equals("登录成功")) {
                                            //跳转页面
                                            Intent test=new Intent(LoginActivity.this,TestActivity.class);
                                            startActivity(test);
                                        }
                                        Looper.loop();//解决线程 1
                                    } catch (Exception e) {
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
