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
import com.example.retailmachineclient.model.TagModel;
import com.example.retailmachineclient.ui.MainActivity;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<GoodInfoModel> dataList;
    Context mContext;
    int pageValue;

    public GridViewAdapter(Context mContext,List<GoodInfoModel> datas, int page) {
        this.mContext = mContext;
        pageValue = page;
        dataList = new ArrayList<GoodInfoModel>();
        //start end分别代表要显示的数组在总数据List中的开始和结束位置
        int start = page * MainActivity.item_grid_num;
        int end = start + MainActivity.item_grid_num;
        while ((start < datas.size()) && (start < end)) {
            dataList.add(datas.get(start));
            start++;
        }
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
                    .inflate(R.layout.layout_goods_msg, viewGroup, false);
            mHolder.messageTv = (TextView) itemView.findViewById(R.id.message);
            mHolder.priceTv = (TextView) itemView.findViewById(R.id.price);
            mHolder.numTv = (TextView) itemView.findViewById(R.id.number);
            mHolder.nameTv = (TextView) itemView.findViewById(R.id.name);
            mHolder.imageView = (ImageView) itemView.findViewById(R.id.image);
            mHolder.imageViewNoGood = (ImageView) itemView.findViewById(R.id.image_no);
            mHolder.tvNoGood = (TextView) itemView.findViewById(R.id.image_no_tx);

            mHolder.layoutBuy = (RelativeLayout) itemView.findViewById(R.id.layout_buy);
            itemView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) itemView.getTag();
        }

        GoodInfoModel model = (GoodInfoModel) dataList.get(i);
        if (model != null) {
//            if(ConstantUtils.IS_TEST){
//                mHolder.imageView.setImageResource(R.mipmap.default_image);
//            }else{
//            String url = Api.ImageHost+model.getGoodsImage();
//            Glide.with(mContext).load(url).into(mHolder.imageView);
//            }
            String url = ConstantUtils.APP_LOGIN_DOMAIN+model.getGoodsImage();
            Glide.with(mContext).load(url).into(mHolder.imageView);
            String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
            if (current .equals(LanguageType.CHINESE.getLanguage())) {
                mHolder.nameTv.setText(model.getGoodsName1()+" "+model.getContainerNum());
                mHolder.numTv.setText(mContext.getString(R.string.surplus_text) +" "+model.getGoodsInventory());
                if(model.getGoodsInventory()==0){
                    mHolder.imageViewNoGood.setVisibility(View.VISIBLE);
//                    mHolder.imageViewNoGood.getBackground().setAlpha(220);
                    mHolder.tvNoGood.setText(mContext.getString(R.string.no_good));
                    mHolder.tvNoGood.setVisibility(View.VISIBLE);
                    mHolder.numTv.setVisibility(View.INVISIBLE);
                }else{
                    mHolder.imageViewNoGood.setVisibility(View.GONE);
                    mHolder.tvNoGood.setVisibility(View.GONE);
                    mHolder.numTv.setVisibility(View.VISIBLE);
                }
            } else if (current .equals(LanguageType.ENGLISH.getLanguage())) {
                mHolder.nameTv.setText(model.getGoodsName2()+" "+model.getContainerNum());
                mHolder.numTv.setText(mContext.getString(R.string.en_surplus_text) +" "+model.getGoodsInventory());
                if(model.getGoodsInventory()==0){
                    mHolder.imageViewNoGood.setVisibility(View.VISIBLE);
//                    mHolder.imageViewNoGood.getBackground().setAlpha(250);
                    mHolder.tvNoGood.setText(mContext.getString(R.string.en_no_good));
                    mHolder.tvNoGood.setVisibility(View.VISIBLE);
                    mHolder.numTv.setVisibility(View.INVISIBLE);
                }else{
                    mHolder.imageViewNoGood.setVisibility(View.GONE);
                    mHolder.tvNoGood.setVisibility(View.GONE);
                    mHolder.numTv.setVisibility(View.VISIBLE);
                }
            } else if(current .equals(LanguageType.FRENCH.getLanguage())) {
                mHolder.nameTv.setText(model.getGoodsName3()+" "+model.getContainerNum());
                mHolder.numTv.setText(mContext.getString(R.string.surplus_text) +" "+model.getGoodsInventory());
//                mHolder.messageTv.setText(model.getGoodsDescription3());
            }


            mHolder.priceTv.setText(" " +model.getPrice());
        }
        return itemView;
    }

    private class ViewHolder {
        public TextView priceTv;
        public TextView messageTv;
        public TextView numTv;
        public TextView nameTv;
        public ImageView imageView;
        public ImageView imageViewNoGood;
        public TextView tvNoGood;
        public RelativeLayout layoutBuy;
    }
}