package com.example.mac.mychatbase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mac on 2016. 6. 13..
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private List<ItemData> itemdatas;

    public MyAdapter(Context context, List<ItemData> items){
        itemdatas = items;
    }


    // onCreateViewHolder의 int viewType을 설정한다.
    @Override
    public int getItemViewType(int position) {
        return itemdatas.get(position).getColId();
    }

    // 1번 분기되어 필요한 layout을 셋한다.
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType){
            case ItemData.TYPE_COL_ONE:
                layout = R.layout.view_blue;
                break;
            case ItemData.TYPE_COL_TWO:
                layout = R.layout.view_red;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        return new ViewHolder(v);

    }

    //포지션의 해당값을 셋팅한다.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemData data = itemdatas.get(position);
        holder.setMessageText(data.getMessageData());
        //포지션의 파라미터를 이용한여 클래스의 메소드를 실행한다.
    }

    @Override
    public int getItemCount() {
        return itemdatas.size();
    }


    //viewholder 이너클래스를 생성한다.
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageText;

        public ViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message);
        }

        public void setMessageText(String message){
            if(message == null)return;
            messageText.setText(message);
        }
    }

}
