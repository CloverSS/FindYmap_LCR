package com.example.findymap.findymap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.example.findymap.findymap.persons;
public class Friendslist extends Activity {
    private ListView mListView;
    private FriLAdapter adpter;
    private Dialog addDialog = null;
    int NowPosition;
    ArrayList<persons> Fridata = new ArrayList<persons>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friends_list);

        Fridata = (ArrayList<persons>) getObject("friends_list");


        mListView = (ListView) findViewById(R.id.lvw_friends_list);
        adpter = new FriLAdapter(this, Fridata);
        mListView.setAdapter(adpter); //为ListView设置适配器
        //按下list中元素，进入编辑页面，传入对象
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowPosition=position;
                Intent intent = new Intent(Friendslist.this, editFri.class);
                persons NowFri = Fridata.get(position);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("nowfriend", NowFri);
                intent.putExtras(mBundle);
                startActivityForResult(intent, 1);
            }
        });

        Button Return_Main = (Button) findViewById(R.id.btn_friends_list_radar);
        Return_Main.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View arg0) {
                                               Intent intent = new Intent(Friendslist.this, MainActivity.class);
                                               startActivity(intent);
                                               finish();
                                           }
                                       });

        Button Go_enemy = (Button) findViewById(R.id.btn_friends_list_enemies);
        Go_enemy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Friendslist.this, enemylist.class);
                startActivity(intent);
                finish();
            }
        });

        Button add_fri = (Button) findViewById(R.id.btn_friends_list_add);
        add_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addDialog = new Dialog(Friendslist.this);
                addDialog.show();
                addDialog.getWindow().setContentView(R.layout.dialog_add_friend);
                final EditText add_name = (EditText) addDialog.getWindow().findViewById(R.id.txt_friend_name);
                final EditText add_num = (EditText) addDialog.getWindow().findViewById(R.id.txt_friend_number);
                addDialog.getWindow()
                        .findViewById(R.id.btn_dialog_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fridata.add(new persons(add_num.getText().toString(), add_name.getText().toString(),0,0));
                                saveObject("friends_list");
                                adpter = new FriLAdapter(Friendslist.this, Fridata);
                                mListView.setAdapter(adpter);
                                addDialog.dismiss();
                            }
                        });
                addDialog.getWindow()
                        .findViewById(R.id.btn_dialog_close)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDialog.dismiss();
                            }
                        });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    persons nowData=Fridata.get(NowPosition);
                    nowData.setNames(data.getStringExtra("new_name"));
                    nowData.setNum(data.getStringExtra("new_num"));
                    Fridata.set(NowPosition,nowData);
                    saveObject("friends_list");
                    adpter = new FriLAdapter(Friendslist.this, Fridata);
                    mListView.setAdapter(adpter);
                }
                break;
            default:
        }
    }

    //将数据保存到本地
    private void saveObject(String name) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(Fridata);
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

    /**
     * 实体类
     */
    public final class ViewHolder {
        public TextView Friname;
        public Button button_del;
    }

    class FriLAdapter extends BaseAdapter {
        private List<persons> mData;       //创建Diary类型的List表
        private LayoutInflater mInflater;               //定义线性布局过滤器

        public FriLAdapter(Context context, List<persons> data) {
            this.mData = data;
            mInflater = LayoutInflater.from(context);       //获取布局
        }

        /**
         * 得到列表长度
         *
         * @return
         */
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;    //得到子项位置id
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.friends_list_item, null);
                holder.Friname = (TextView) convertView.findViewById(R.id.name_cell);
                holder.button_del = (Button) convertView.findViewById(R.id.delete_button_cell);
                holder.Friname.setText(mData.get(position).getNames());
                holder.button_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        addDialog = new AlertDialog.Builder(Friendslist.this).create();
                        addDialog.show();
                        addDialog.getWindow().setContentView(R.layout.dialog_delete);
                        addDialog.getWindow().findViewById(R.id.btn_dialog_ok)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Fridata.remove(position);
                                        saveObject("friends_list");
                                        adpter = new FriLAdapter(Friendslist.this, Fridata);
                                        mListView.setAdapter(adpter);
                                        addDialog.dismiss();
                                    }
                                });
                        addDialog.getWindow().findViewById(R.id.btn_dialog_close)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addDialog.dismiss();
                                    }
                                });
                    }
                });
                convertView.setTag(holder);
            } else {
                convertView.getTag();
            }
            return convertView;
        }
    }
}

