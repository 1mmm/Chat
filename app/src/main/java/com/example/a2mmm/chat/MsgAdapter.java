package com.example.a2mmm.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(View view){
            super(view);
            leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
            leftMsg=(TextView)view.findViewById(R.id.left_msg);
            rightMsg=(TextView)view.findViewById(R.id.right_msg);
        }
    }
    public MsgAdapter(List<Msg> msgList){
        mMsgList=msgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){               //onCreateViewHolder()用于创建ViewHolder实例
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);                                                   //把加载出来的布局传到构造函数中，再返回
    }
    @Override
    public void onBindViewHolder(ViewHolder Holder,int position){                     //onBindViewHolder()用于对RecyclerView子项的数据进行赋值，会在每个子项被滚动到屏幕内的时候执行
        Msg msg=mMsgList.get(position);
        if(msg.getType()==Msg.TYPE_RECEIVED){                                         //增加对消息类的判断，如果这条消息是收到的，显示左边布局，是发出的，显示右边布局
            Holder.leftLayout.setVisibility(View.VISIBLE);
            Holder.rightLayout.setVisibility(View.GONE);
            Holder.leftMsg.setText(msg.getContent());
        }else if(msg.getType()==Msg.TYPE_SENT) {
            Holder.rightLayout.setVisibility(View.VISIBLE);
            Holder.leftLayout.setVisibility(View.GONE);
            Holder.rightMsg.setText(msg.getContent());
        }
    }
    @Override
    public int getItemCount(){
        return mMsgList.size();
    }
}