package com.example.retailmachineclient.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.Api;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.TagModel;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.ui.MainActivity;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.Utils;

import org.greenrobot.eventbus.EventBus;

import DDRAIServiceProto.DDRAIServiceCmd;

import static com.example.retailmachineclient.model.MessageEvent.EventType_ADD_GOODS;
import static com.example.retailmachineclient.model.MessageEvent.EventType_ADD_GOODS_IN_DETAIL;

public class CustomDialog extends Dialog {
    public GoodMsgModel goodMsgModel;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, GoodMsgModel goodMsgModel) {
        super(context);
        this.goodMsgModel = goodMsgModel;
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        public GoodInfoModel goodMsgModel;
        private Context context;
        private int pageValue;

        public GoodInfoModel getGoodMsgModel() {
            return goodMsgModel;
        }

        public void setGoodMsgModel(GoodInfoModel goodMsgModel) {
            this.goodMsgModel = goodMsgModel;
        }

        public int getPageValue() {
            return pageValue;
        }

        public void setPageValue(int pageValue) {
            this.pageValue = pageValue;
        }

        public int getPagePostion() {
            return pagePosition;
        }

        public void setPagePostion(int pagePostion) {
            this.pagePosition = pagePostion;
        }

        private int pagePosition;

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, null);
//            final CustomDialog dialog = new CustomDialog(context, R.style.customDialogTheme);
            View layout = inflater.inflate(R.layout.layout_dialog_goods_detail, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Logger.e("点击:你点击了Dialog setOnTouchListener");
                    MainActivity.lastTouchTime = System.currentTimeMillis();
                    TaskUtils.sendPagePoint(TcpAiClient.getInstance(BaseApplication.getContext(), ClientMessageDispatcher.getInstance()), DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());

                    return false;
                }
            });

            if(ConstantUtils.IS_TEST){
//                ((ImageView) layout.findViewById(R.id.image)).setImageResource(R.mipmap.default_image);
            }else{
                String url = ConstantUtils.APP_LOGIN_DOMAIN+goodMsgModel.getGoodsImage();
                Logger.e("-------商品图片信息 url = " +url);
                Glide.with(context).load(url).into((ImageView) layout.findViewById(R.id.image));
            }
            String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
            if (current .equals(LanguageType.CHINESE.getLanguage())) {
                ((TextView) layout.findViewById(R.id.name)).setText(goodMsgModel.getGoodsName1() +" "+ goodMsgModel.getContainerNum());
                 ((TextView) layout.findViewById(R.id.description)).setText(goodMsgModel.getGoodsDescription1());
            } else if (current .equals(LanguageType.ENGLISH.getLanguage())) {
                ((TextView) layout.findViewById(R.id.name)).setText(goodMsgModel.getGoodsName2()+" "+ goodMsgModel.getContainerNum());
                 ((TextView) layout.findViewById(R.id.description)).setText(goodMsgModel.getGoodsDescription2());
            } else if(current .equals(LanguageType.FRENCH.getLanguage())) {
                ((TextView) layout.findViewById(R.id.name)).setText(goodMsgModel.getGoodsName3()+" "+ goodMsgModel.getContainerNum());
                ((TextView) layout.findViewById(R.id.description)).setText(goodMsgModel.getGoodsDescription3());
            }
            ((TextView) layout.findViewById(R.id.price)).setText(BaseApplication.getContext().getString(R.string.money_type_text) + goodMsgModel.getPrice());
            ((ImageView) layout.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Logger.e("点击:你点击了dialog cancel");
                    MainActivity.lastTouchTime = System.currentTimeMillis();
                    TaskUtils.sendPagePoint(TcpAiClient.getInstance(BaseApplication.getContext(), ClientMessageDispatcher.getInstance()), DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                    dialog.dismiss();

                }
            });

            RelativeLayout addLayout = (RelativeLayout) layout.findViewById(R.id.layout_add);
            addLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivity.lastTouchTime = System.currentTimeMillis();
                    TaskUtils.sendPagePoint(TcpAiClient.getInstance(BaseApplication.getContext(), ClientMessageDispatcher.getInstance()), DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());

                    TagModel tagModel = (TagModel) view.getTag();
                    MessageEvent event = new MessageEvent(EventType_ADD_GOODS_IN_DETAIL);
                    event.setType(EventType_ADD_GOODS_IN_DETAIL);
                    event.setPageIndex(tagModel.getPageValue());
                    event.setPagePosition(tagModel.getPagePosition());
                    EventBus.getDefault().post(event);
                }
            });
            TagModel tagModel = new TagModel();
            tagModel.setPageValue(pageValue);
            tagModel.setPagePosition(pagePosition);
            addLayout.setTag(tagModel);
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
