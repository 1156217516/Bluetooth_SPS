package com.example.admin.bluetooth_sps.JavaBean;

import java.lang.ref.SoftReference;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/6/7/007.
 */

public class MyUser extends BmobUser{
    private String username;
    private String password;
    private String mailbox;


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }
}
