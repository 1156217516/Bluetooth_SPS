package com.example.admin.bluetooth_sps.Ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.bluetooth_sps.Base.BaseActivity;
import com.example.admin.bluetooth_sps.JavaBean.MyUser;
import com.example.admin.bluetooth_sps.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Administrator on 2018/6/7/007.
 */

public class LoginActivity extends BaseActivity{
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();

    }

    private void initView(){
        etUsername = (EditText) findViewById(R.id.et_login_username);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if(username.equals("")||password.equals("")){
                    showToast("请输入用户名或密码！");
                }else {
                    BmobUser.loginByAccount(username, password, new LogInListener<MyUser>() {
                        @Override
                        public void done(MyUser myUcser, BmobException e) {
                            if(myUcser!=null){
                                showToast("登录成功！");
                                Intent intent = new Intent();
                                intent.putExtra("et_login_username",username);
                                setResult(1,intent);
                            }else {
                                showToast("登录失败！");
                                Log.e("LoginActivity",e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void showToast(String msg){
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
