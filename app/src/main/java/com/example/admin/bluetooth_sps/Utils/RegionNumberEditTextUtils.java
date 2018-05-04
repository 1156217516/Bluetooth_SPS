package com.example.admin.bluetooth_sps.Utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * Created by admin on 2018/1/15.
 */

public class RegionNumberEditTextUtils {
    public RegionNumberEditTextUtils(final Context context, final EditText edit, final int index){
        edit.addTextChangedListener(new TextWatcher() {
            int l = 0;////////记录字符串被删除字符之前，字符串的长度
            int location = 0;//记录光标的位置

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                l = s.length();
                location = edit.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = null;
                if (index == 0)
                    p = Pattern.compile("^([0-9]|([0-9]\\d)|([0-1]\\d\\d)|(2([0-4]\\d|5[0-5])))$");  //匹配0-255
                    //p = Pattern.compile("^(([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5]))))$");  //匹配0-255
                else if (index == 1)
                    p = Pattern.compile("^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$");  //匹配0-65535
                else if (index == 2)
                    p = Pattern.compile("^([0-9]|(1[0-5]))$");//匹配0-15
                else if (index == 3)
                    p = Pattern.compile("^(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-3]\\d|40))))$");//匹配1-240
                else if (index == 4)
                    p = Pattern.compile("^([0-9]|[1-9][0-9]{0,7})$");//匹配0-99999999
                else if (index == 5)
                    p = Pattern.compile("^([0-9]|[0-9][0-9])$");//匹配0-99

                Matcher m = p.matcher(s.toString());
                if (m.find() || ("").equals(s.toString())) {
                    //System.out.print("OK!");
                } else {
                    //System.out.print("False!");
                    switch (index){
                        case 0:
                            Toast.makeText(context, "请输入0-255", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(context, "请输入0-65535", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(context, "请输入0-15", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(context, "请输入1-240", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(context, "请输入0-99999999", Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            if(!s.toString().contains("a")&&!s.toString().contains("b")&&!s.toString().contains("c")
                                    &&!s.toString().contains("d")&&!s.toString().contains("e")&&!s.toString().contains("f"))
                                Toast.makeText(context, "请输入0-99", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    String str = s.toString();
                    if(str.contains("a")||str.contains("b")||str.contains("c")
                            ||str.contains("d")||str.contains("e")||str.contains("f")){
                        str="99";
                        edit.setTextColor(Color.RED);
                        edit.setText(str);

                    }else {
                        edit.setTextColor(Color.BLACK);
                        edit.setText(str.substring(0, s.length() - 1));
                        edit.setSelection(s.length() - 1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }});
    }
}
