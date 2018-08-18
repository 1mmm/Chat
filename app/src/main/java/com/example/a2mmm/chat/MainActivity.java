package com.example.a2mmm.chat;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private List<Msg> msgList=new ArrayList<>();
    private Button send,sysend,wzsend;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private EditText nr;
    public static String HOST="",mains="";
    public BufferedWriter writer=null;
    public BufferedReader reader=null;
    private static InputStream is=null;
    private Handler handler = new Handler() {
        public void handleMessage(final Message msg) {
            Thread thread = new Thread() {
                public void run() {
                    try
                    {
                        Socket a_socket = RequestSocket(HOST,8055);
                        Log.d("success", "success ");
                        writer = new BufferedWriter(new OutputStreamWriter(a_socket.getOutputStream()));
                        is=a_socket.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(is));
                        writer.write("phone");
                        writer.flush();
                        handedad.sendEmptyMessage(1);
                        while (true)
                        {
                            if (!mains.equals(""))
                            {
                                String h=mains+"\n";
                                writer.write(h);
                                writer.flush();
                                mains="";
                            }
                            else
                            {
                                writer.write("!@#flush");
                                writer.flush();
                            }
                            char[] buffer =new char[1024];
                            int count=reader.read(buffer);
                            String h=new String(buffer,0,count);

                            if ((h.length()<3)||(!h.substring(0,3).equals("!@#"))){
                                if (h.equals("phon")) h="对方已上线!";
                                Msg msg;
                                if ((h.length()>2)&&(h.substring(0,2).equals("!@")))
                                    msg = new Msg(h.substring(2), Msg.TYPE_SENT);
                                else
                                    msg = new Msg(h, Msg.TYPE_RECEIVED);
                                msgList.add(msg);
                                handedad.sendEmptyMessage(3);
                            }
                        }

                    }catch (IOException e)
                    {
                        handedad.sendEmptyMessage(2);
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            super.handleMessage(msg);
        }
    };
    final Handler handedad = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1)
            {

                Toast.makeText(getApplicationContext(), "连接SOCKET成功！", Toast.LENGTH_SHORT).show();

            }
            else if (msg.what==2)
            {

                Toast.makeText(getApplicationContext(), "连接SOCKET失败！", Toast.LENGTH_SHORT).show();
            }
            else if (msg.what==3)
            {
                adapter.notifyItemInserted(msgList.size() - 1);           //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgs();                                                         //初始化消息数据
        sysend=(Button)findViewById(R.id.sysend);
        wzsend=(Button)findViewById(R.id.wzsend);
        send=(Button)findViewById(R.id.send);
        nr=(EditText) findViewById(R.id.nr);
        msgRecyclerView=(RecyclerView)findViewById(R.id.msg_recycler_view);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);    //LinearLayoutLayout即线性布局，创建对象后把它设置到RecyclerView当中
        msgRecyclerView.setLayoutManager(layoutManager);

        adapter=new MsgAdapter(msgList);                                    //创建MsgAdapter的实例并将数据传入到MsgAdapter的构造函数中
        msgRecyclerView.setAdapter(adapter);
        sysend.setOnClickListener(new View.OnClickListener(){                 //发送按钮点击事件
            @Override
            public void onClick(View v){           //获取EditText中的内容
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
                dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
                dialog.setTitle("提示");
                // dismiss监听
                dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "录制完毕", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mains="!@#csq234";
                        dialog.dismiss();
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub

                    }
                });
                // 监听Key事件被传递给dialog
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });
                // 监听cancel事件
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub

                    }
                });


                dialog.setMessage("正在录制手势");
                mains="!@#csq123";
                dialog.show();

            }
        });
        wzsend.setOnClickListener(new View.OnClickListener(){                 //发送按钮点击事件
            @Override
            public void onClick(View v){           //获取EditText中的内容
                String h=nr.getText().toString();
                mains=h;
                if(!"".equals(h)){                                    //内容不为空则创建一个新的Msg对象，并把它添加到msgList列表中
                    Msg msg=new Msg(h,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);           //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                    msgRecyclerView.scrollToPosition(msgList.size()-1);     //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                    nr.setText("");
                    //调用EditText的setText()方法将输入的内容清空
                }
                else{
                Toast.makeText(getApplicationContext(), "输入不可为空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener(){                 //发送按钮点击事件
            @Override
            public void onClick(View v){           //获取EditText中的内容
                startRecognize();

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu); //通过getMenuInflater()方法得到MenuInflater对象，再调用它的inflate()方法就可以给当前活动创建菜单了，第一个参数：用于指定我们通过哪一个资源文件来创建菜单；第二个参数：用于指定我们的菜单项将添加到哪一个Menu对象当中。
        return true; // true：允许创建的菜单显示出来，false：创建的菜单将无法显示。
    }
    private Socket RequestSocket(String host,int port) throws UnknownHostException, IOException {
        Socket socket = new Socket(host, port);
        return socket;
    }
    /**
     *菜单的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.connect:
                View view = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.ip);
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("请输入连接的ip地址：")//设置对话框的标题
                        .setView(view)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                Thread thread = new Thread() {
                                    public void run() {
                                        HOST = editText.getText().toString();
                                        Message msg=new Message();
                                        msg.obj = HOST;
                                        handler.sendMessage(msg);
                                        // cancel和dismiss方法本质都是一样的，都是从屏幕中删除Dialog,唯一的区别是
                                        // 调用cancel方法会回调DialogInterface.OnCancelListener如果注册的话,dismiss方法不会回掉
                                        dialog.dismiss();

//                        dialog.dismiss();
                                    }
                                };
                                thread.start();
                                //file_name1 = "#"+file_name+file_seq+"\n";
                                //writer.write(file_name1);
                                //Log.d("SensorWrite",file_name1);
                                //writer.flush();

                            }
                        }).create();
                dialog.show();
                break;

            case R.id.disconnect:
                Toast.makeText(this, "你点击了断开！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return true;
    }
    private void initMsgs(){
        Msg msg1=new Msg("欢迎来到手语交互系统。",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
    }
    public void startRecognize() {
        try{
            Intent recognizeActivity =
                    //当用RecognizerIntent.ACTIOIN_RECOGNIZE_SPEECH的Action时，会报ActivityNotFoundException,所以需要捕捉异常，
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //用ACTION_WEB_SEARCH,当手机安装了语音助手后就可以打开了

            //传参
            //1:语音识别模式(语言模式、自由模式)
            recognizeActivity.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //2:提示语音开始
            recognizeActivity.putExtra(RecognizerIntent.EXTRA_PROMPT, "Common,让我们躁起来！");
            //3:开始语音识别
            startActivityForResult(recognizeActivity, 0x01);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "找不到语音设备", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x01) {
                ArrayList<String > voicesList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                StringBuilder stringBuilder = new StringBuilder();
                if(voicesList != null && voicesList.size() > 0){

                    for(int i=0;i<voicesList.size();i++){
                        stringBuilder.append(voicesList.get(i));
                    }
                    String content=stringBuilder.toString();
                    if(!"".equals(content)){                                    //内容不为空则创建一个新的Msg对象，并把它添加到msgList列表中
                        mains=content;
                        Msg msg=new Msg(content,Msg.TYPE_SENT);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size()-1);           //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                        msgRecyclerView.scrollToPosition(msgList.size()-1);     //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                        //调用EditText的setText()方法将输入的内容清空
                    }
                }else{
                    stringBuilder.append("请说话");
                }


            }
        }
    }
}
