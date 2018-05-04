package com.example.admin.bluetooth_sps.File;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2018/1/16.
 */

public class FileWrite {
    final String FILE_NAME="/ErrorLog.txt";
    final String AbsolutePATH = "/mnt/sdcard/Download";
    //
    public FileWrite(String string){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //File sdCardDir = Environment.getExternalStorageDirectory();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            try {
                File targetFile = new File(AbsolutePATH+FILE_NAME);
                RandomAccessFile raf = new RandomAccessFile(targetFile,"rw");
                raf.seek(targetFile.length());
                raf.write(String.valueOf("\r").getBytes());
                raf.write(String.valueOf("\n").getBytes());
                raf.write(string.getBytes());
                raf.write(String.valueOf("\r").getBytes());
                raf.write(String.valueOf("\n").getBytes());
                raf.write(df.format(new Date()).getBytes());
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
