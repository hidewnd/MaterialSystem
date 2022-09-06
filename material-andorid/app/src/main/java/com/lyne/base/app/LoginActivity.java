package com.lyne.base.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lyne.base.R;
import com.lyne.base.SpUtils;
import com.lyne.base.api.HttpPort;
import com.lyne.base.bean.LoginBody;
import com.lyne.base.bean.LoginRsp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText mEtName;
    private EditText mEtPsw;
    private Button mBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEtName = findViewById(R.id.et_name);
        mEtPsw = findViewById(R.id.et_psw);
        mBt = findViewById(R.id.bt);
        mBt.setOnClickListener(v -> {
            String name = mEtName.getText().toString().trim();
            String psw = mEtPsw.getText().toString().trim();
            if (!"".equals(name) && !"".equals(psw)) {
                //登录接口请求
                HttpPort.getInstance().login(new LoginBody(name, psw)).enqueue(new Callback<LoginRsp>() {
                    @Override
                    public void onResponse(Call<LoginRsp> call, Response<LoginRsp> response) {
                        LoginRsp body = response.body();
                        if (body != null) {
                            if (body.getCode() == 200 && body.getData() != null) {
                                String access_token = body.getData().getAccess_token();
                                String refresh_token = body.getData().getRefresh_token();
                                //登录保存token
                                SpUtils.get().putValue("access_token", access_token);
                                SpUtils.get().putValue("refresh_token", refresh_token);
                                SpUtils.get().putValue("username", name);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            Toast.makeText(LoginActivity.this, body.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRsp> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                //接口修改后注释下面两行
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }
}