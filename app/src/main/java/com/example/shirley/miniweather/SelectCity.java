package com.example.shirley.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirley.app.MyApplication;
import com.example.shirley.bean.City;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by shirley on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackbtn;
    private TextView currentCity;
    private EditText editText;
    public String selectedID;


    @Override
    protected void onCreate(Bundle savedInstanceStage) {

        super.onCreate(savedInstanceStage);
        setContentView(R.layout.select_city);

        currentCity = (TextView)findViewById(R.id.title_city_name);
        SharedPreferences sharedPreferences = getSharedPreferences("UPCT", MODE_PRIVATE);
        currentCity.setText("当前城市：" + sharedPreferences.getString("updateCityName", "Mini Weather"));

        mBackbtn = (ImageView)findViewById(R.id.title_back);
        mBackbtn.setOnClickListener(this);


        MyApplication myApplication = (MyApplication)getApplication();

        final List<String> data = new ArrayList<>();//存放城市名称
        final List<String> data2 = new ArrayList<>();//存放城市名称，与上面一起完成搜索城市时候的配合
        final List<String> cityID = new ArrayList<>();//存放城市的代号
        final List<String> cityID2 = new ArrayList<>();//存放城市代号，配合完成搜索
        final List<String> cityPY = new ArrayList<>();//存放城市的拼音，全拼

        Iterator<City> it = myApplication.getCityList().iterator();

        while (it.hasNext()) {//从原来整个database中筛选城市代号、城市名和城市拼音
            City tmp = it.next();
            String cityname1 = tmp.getCity();
            String cityid1 = tmp.getNumber();
            String cityAllPY = tmp.getAllPY();//retrieve the pinyin item from database
            String cityname2 = tmp.getCity();
            data.add(cityname1);
            data2.add(cityname2);
            cityID.add(cityid1);
            cityID2.add(cityid1);
            cityPY.add(cityAllPY.toLowerCase());//insert that item into a list called cityPY，拼音变成小写
        }

        //将数据库中的城市显示到列表中，需要利用适配器adapter
        ListView mlistView = (ListView)findViewById(R.id.list_view);
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        mlistView.setAdapter(adapter1);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//设置list view的点击监听
                Toast.makeText(SelectCity.this, "您选择的城市是:" + data.get(i), Toast.LENGTH_SHORT).show();
                selectedID = cityID2.get(i);

                Intent intent = new Intent(SelectCity.this, MainActivity.class);//用intent传递选择城市的号码，在点击list view的item后，直接返回主界面并更新选择城市的天气信息
                intent.putExtra("cityCode", selectedID);
                Log.d("cityid", selectedID);
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        //实现输入城市名称的查找
        editText = (EditText)findViewById(R.id.search_edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                data.clear();
                cityID2.clear();
                int pyindex = 0;
                int pyindexCh = 0;
                Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");//正则表达式用来判断输入的是否是汉字
                //汉字搜索
                if (s.equals(p_str)) {
                    for (String str : data2) {
                        if (str.indexOf(editText.getText().toString()) != -1) {
                            data.add(str);
                            cityID2.add(cityID.get(pyindexCh));
                        }
                        pyindexCh++;
                    }
                }

            }
        });


    }

      /*
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.title_back:
                    Intent i = new Intent();
                    i.putExtra("cityCode", "101160101");
                    setResult(RESULT_OK, i);
                    finish();
                    break;
                default:
                    break;

            }
        }
*/

    @Override
    public void onClick(View v) { //对于城市有无进行区分
        Intent i = new Intent();
        switch (v.getId()) {
            case R.id.title_back:
                if (selectedID == null) {
                    SharedPreferences sharedPreferences= getSharedPreferences("LC", MODE_PRIVATE);//这个sharePre用于传递定位城市ID
                    selectedID = sharedPreferences.getString("LocatedCity", "101160101");//如果点击返回按钮，则返回定位城市
                }
                i.putExtra("cityCode", selectedID);
                setResult(RESULT_OK, i);
                finish();
                break;

            default:
              break;
        }
    }
}

