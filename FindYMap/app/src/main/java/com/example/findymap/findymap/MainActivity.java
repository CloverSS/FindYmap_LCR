package com.example.findymap.findymap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private LocationClient locationClient;
    public BDLocationListener myListener = new MyLocationListenner();
    boolean isFirstLoc = true;
    double Mylatitude;
    double Mylongitude;
    private double Latitude=23.107419;
    private double Longitude=113.321046;//39.963175, 116.400244
    String address="";
    String fullMessage = "";

    TextureMapView  mMapView = null;
    BaiduMap mBaiduMap=null;

    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;

    ArrayList<persons> Fridata = new ArrayList<persons>();
    ArrayList<persons> Enedata = new ArrayList<persons>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

      if (getObject("friends_list") == null) {
          Fridata.add(new persons("+8615820575915", "test",22.257,113.5434));
            saveObject("friends_list",Fridata);
       } else {
           Fridata = (ArrayList<persons>) getObject("friends_list");
     }

       if (getObject("enemies_list") == null) {
            Enedata.add(new persons("+8615820575907", "test",22.2572,113.543408));
            saveObject("enemies_list",Enedata);
            // Fridata.add(new persons("15820575907", "段欣"));
        } else {
            Enedata = (ArrayList<persons>) getObject("enemies_list");
        }

        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        Maker();

        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, receiveFilter);
        initLocMap();//定位初始化

        Button button_t=(Button)findViewById(R.id.btn_locate);
        button_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
            });

        Button button_frineds=(Button)findViewById(R.id.btn_friends);
        button_frineds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Friendslist.class);
                startActivity(intent);
            }
        });

        Button button_enemies=(Button)findViewById(R.id.btn_enemies);
        button_enemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, enemylist.class);
                startActivity(intent);
            }
        });

        Button button_refresh=(Button)findViewById(R.id.btn_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < Fridata.size(); i++) {
                    persons ForFriend = Fridata.get(i);
                    String Input="where are you?";
                    sendMS(ForFriend.getNum(), Input);
                }
                for (int i = 0; i < Enedata.size(); i++) {
                    persons ForFriend = Enedata.get(i);
                    String Input="where are you?";
                    sendMS(ForFriend.getNum(), Input);
                }
            }
        });
    }

  public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            Mylatitude=location.getLatitude();
            Mylongitude=location.getLongitude();

            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

        class MessageReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                fullMessage = "";
                Bundle bundle = intent.getExtras();
                Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                address = messages[0].getOriginatingAddress();
                for (SmsMessage message : messages) {
                    fullMessage += message.getMessageBody(); // 获取短信内容
                }
                if (fullMessage.equals("where are you?")) {
                    String msginput = Mylatitude + "/" + Mylongitude;
                    sendMS(address, msginput);
                } else {
                    refresh(Fridata);
                    refresh(Enedata);
                    }
                }
            }



    private void sendMS(String to,String msgInput)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(to, null,msgInput, null, null);
    }
    private void initLocMap()
    {
        mBaiduMap.setMyLocationEnabled(true);
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        LocationClientOption option2 = new LocationClientOption();
        option2.setOpenGps(true); // 打开gps
        option2.setCoorType("bd09ll"); // 设置坐标类型
        option2.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option2);
        locationClient.start();
    }

    private void refresh(ArrayList<persons> freshdata)
    {
        for (int i = 0; i < freshdata.size(); i++) {
            persons nowfriend = freshdata.get(i);
            if (nowfriend.getNum().equals(address)) {
                String Lat = "";
                String Long = "";
                for (int j = 0; j < fullMessage.length(); j++) {
                    if (fullMessage.charAt(j) != '/') {
                        Lat = Lat + fullMessage.charAt(j);
                    } else {
                        Long = fullMessage.substring(j + 1);
                        break;
                    }
                }
                nowfriend.setLatiT(Double.valueOf(Lat));
                nowfriend.setLongT(Double.valueOf(Long));
                freshdata.set(i, nowfriend);
                Maker();
                break;
            }
        }
    }
    //刷新标记
    private void Maker()
    {
         mBaiduMap.clear();
        for(int pos=0;pos<Fridata.size();pos++) {
            persons makPerson = Fridata.get(pos);
            if (makPerson.getLongT()==0&&makPerson.getLatiT()==0) {
                continue;
            }
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            LatLng point = new LatLng(makPerson.getLatiT(), makPerson.getLongT());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_marker);
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            mBaiduMap.addOverlay(option);
            Fridata.set(pos, makPerson);
            saveObject("friends_list",Fridata);
        }
        for(int pos=0;pos<Enedata.size();pos++) {
            persons makPerson = Enedata.get(pos);
            if (makPerson.getLongT()==0&&makPerson.getLatiT()==0) {
                continue;
            }
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            LatLng point = new LatLng(makPerson.getLatiT(), makPerson.getLongT());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.enemy_marker);
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            mBaiduMap.addOverlay(option);
            Enedata.set(pos, makPerson);
            saveObject("enemies_list",Enedata);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void saveObject(String name,ArrayList<persons> savelist) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(savelist);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    //读取文件
    private Object getObject(String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

}
