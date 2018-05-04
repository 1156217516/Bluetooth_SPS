package com.example.admin.bluetooth_sps.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.bluetooth_sps.R;
import com.example.admin.bluetooth_sps.Ui.Activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by admin on 2017/9/14.
 */
public class FileScan extends Activity{
    private ListView lvFileName;
    private TextView tvFileName;
    private ArrayList<String> fileNameLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_filescan);

        fileNameLists=getIntent().getStringArrayListExtra("FileName");
//        for(int i=0;i<fileNameLists.size();i++)
//            System.out.println(String.valueOf(fileNameLists.get(i)));

        lvFileName= (ListView) findViewById(R.id.lv_fileName);
        lvFileName.setAdapter(new MyAdapter());
        lvFileName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FileScan.this, MainActivity.class);
                intent.putExtra("FileNameIndex",position);
                setResult(1, intent);
                finish();
            }
        });
    }



    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fileNameLists.size();
        }

        @Override
        public Object getItem(int position) {
            return fileNameLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(FileScan.this, R.layout.file_list_item, null);
            tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
            tvFileName.setText(fileNameLists.get(position));
            return view;
        }
    }
}
