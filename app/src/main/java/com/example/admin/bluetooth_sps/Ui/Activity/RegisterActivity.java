package com.example.admin.bluetooth_sps.Ui.Activity;

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
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/6/7/007.
 */

public class RegisterActivity extends BaseActivity{
    private EditText etUsername;
    private EditText etPassword1;
    private EditText etPassword2;
    private EditText etMailbox;
    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password1 = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString();
                String mailbox = etMailbox.getText().toString();

                if(username.equals("")){
                    showToast("用户名不能为空！");
                }else if(password1.equals("")||etPassword2.equals("")){
                    showToast("密码不能为空！");
                }else{
                    if(password1 != password2){
                        showToast("两次输入密码不一致！");
                    }else {
                        BmobUser bmobUser = new BmobUser();
                        bmobUser.setUsername(username);
                        bmobUser.setPassword(password1);
                        bmobUser.setEmail(mailbox);
                        bmobUser.signUp(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                 if(e==null){
                                     showToast("注册成功！");
                                 }else {
                                     Log.e("RegisterActivity",e.getMessage());
                                 }
                            }
                        });

                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initView(){
        etUsername = (EditText)findViewById(R.id.et_register_username);
        etPassword1 = (EditText)findViewById(R.id.et_register_password1);
        etPassword2 = (EditText)findViewById(R.id.et_register_password2);
        etMailbox = (EditText)findViewById(R.id.et_register_mailbox);
        btnRegister = (Button)findViewById(R.id.btn_register);
        btnLogin = (Button)findViewById(R.id.btn_login);
    }

    private void showToast(String msg){
        Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
