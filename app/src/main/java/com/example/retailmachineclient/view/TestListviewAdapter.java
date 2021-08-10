package com.example.retailmachineclient.view;

import com.example.retailmachineclient.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.retailmachineclient.model.CardPayMsgModel;
import com.example.retailmachineclient.util.Logger;

import java.util.List;

public class TestListviewAdapter extends BaseAdapter {
    private List<CardPayMsgModel> dataList;
    Context mContext;
    int pageValue;

    public TestListviewAdapter(Context mContext,List<CardPayMsgModel> datas) {
        this.mContext = mContext;
        dataList = datas;
    }

    public void update(List<CardPayMsgModel> datas){
        this.dataList = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {
        ViewHolder mHolder;
        if (itemView == null) {
            mHolder = new ViewHolder();
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item, viewGroup, false);
            mHolder.name = (TextView) itemView.findViewById(R.id.tvname);
            mHolder.date = (TextView) itemView.findViewById(R.id.date);
            mHolder.card = (TextView) itemView.findViewById(R.id.card);
            itemView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) itemView.getTag();
        }

        CardPayMsgModel model = (CardPayMsgModel) dataList.get(i);
//        Logger.e("刷卡 选项："+model.getMsgType()+":"+model.getPayNum()+":" +model.getTimestamp()+" : "+ model.getMessage());
        if (model != null) {
            mHolder.name.setText("交易ID: " +model.getOrderNum());
            mHolder.date.setText("交易时间:"+model.getTransDate());
            mHolder.card.setText("交易卡号:"+model.getCardNum());
            Logger.e("刷卡 选项："+model.getIsDeleted()+":"+model.getCardNum()+":"+model.getPayNum()+":" +model.getTimestamp()+" : "+ model.getTransDate());
        }
        return itemView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView date;
        public TextView card;

    }
}