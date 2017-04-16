package com.example.mac.mychatbase;

/**
 * Created by mac on 2016. 6. 13..
 */
public class ItemData {

    public static final int TYPE_COL_ONE = 1;
    public static final int TYPE_COL_TWO = 2;

    public int colId;
    public String messageData;

    public ItemData(int colId,String messageData){
        this.colId = colId;
        this.messageData = messageData;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public int getColId() {
        return colId;

    }

    public void setColId(int colId) {
        this.colId = colId;
    }
}
