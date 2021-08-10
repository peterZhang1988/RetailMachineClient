package com.example.retailmachineclient.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.Api;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.ui.MainActivity;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

//import androidx.recyclerview.widget.RecyclerView;

import static com.example.retailmachineclient.model.MessageEvent.EventType_DELETE_SUCCESS;


public class NormalRecyclerViewAdapter extends BaseAdapter {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<GoodInfoModel> datas = new ArrayList<GoodInfoModel>();
    private boolean isCanDel;

    public void update(List<GoodInfoModel> updates) {
        datas.clear();
        datas.addAll(updates);
        notifyDataSetChanged();
    }

    public NormalRecyclerViewAdapter(Context context, List<GoodInfoModel> updates,boolean isCanDel) {
        Logger.e("run NormalRecyclerViewAdapter start =");
        datas.clear();
        datas.addAll(updates);
        mContext = context;
        this.isCanDel = isCanDel;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup viewGroup) {
        NormalTextViewHolder holder;
        if (itemView == null) {
            holder = new NormalTextViewHolder();
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_shopping_bus, viewGroup, false);
            holder.iconIV = (ImageView) itemView.findViewById(R.id.car_image);
            holder.mNameTV = (TextView) itemView.findViewById(R.id.car_name);
            holder.mPriceTV = (TextView) itemView.findViewById(R.id.car_price);
            holder.deleteIV = (ImageView) itemView.findViewById(R.id.car_delete);
            itemView.setTag(holder);
        } else {
            holder = (NormalTextViewHolder) itemView.getTag();
        }

        GoodInfoModel model = datas.get(position);
        if (model != null && holder != null) {
//            holder.mNameTV.setText(model.getGoodsName1());
//            holder.mPriceTV.setText(BaseApplication.getContext().getString(R.string.money_type_text) + Utils.intToFloat(model.getPrice()));

            String url = ConstantUtils.APP_LOGIN_DOMAIN+model.getGoodsImage();
            Glide.with(mContext).load(url).into(holder.iconIV);

            holder.mPriceTV.setText(mContext.getString(R.string.money_type_text) +" "+ model.getPrice());

            String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
            if (current .equals(LanguageType.CHINESE.getLanguage())) {
                holder.mNameTV.setText(model.getGoodsName1()+" "+model.getContainerNum());
//                mHolder.messageTv.setText(model.getGoodsDescription1());
            } else if (current .equals(LanguageType.ENGLISH.getLanguage())) {
                holder.mNameTV.setText(model.getGoodsName2()+" "+model.getContainerNum());
//                mHolder.messageTv.setText(model.getGoodsDescription2());
            } else if(current .equals(LanguageType.FRENCH.getLanguage())) {
                holder.mNameTV.setText(model.getGoodsName3()+" "+model.getContainerNum());
//                mHolder.messageTv.setText(model.getGoodsDescription3());
            }

            if(isCanDel){
                holder.deleteIV.setVisibility(View.VISIBLE);
                holder.deleteIV.setTag(position);
                holder.deleteIV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int tagPosition = (int) view.getTag();
                        MessageEvent event = new MessageEvent(EventType_DELETE_SUCCESS);
                        event.setPosition(tagPosition);
                        EventBus.getDefault().post(event);
                    }
                });
            }else{
                holder.deleteIV.setVisibility(View.INVISIBLE);
            }

        }
        return itemView;
    }

    private class NormalTextViewHolder {
        ImageView iconIV;
        TextView mNameTV;
        TextView mPriceTV;
        ImageView deleteIV;
    }
}