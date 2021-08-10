package com.example.retailmachineclient.ui;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.TimeIntervalUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import android.widget.Button;
import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN_NAME;

public class LoginTestPageActivity extends BaseActivity  implements View.OnClickListener {

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.back)
    Button back;

    String passwordStr = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_layout_login_test;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        initViews();
        initDatas();
    }

    private void initViews() {
//        setLanguage(1);
        //初始化ViewPager
        findViewById(R.id.back).setOnClickListener(this);
        password  = (EditText)findViewById(R.id.password);
        findViewById(R.id.startLogin).setOnClickListener(this);
    }

    private void initDatas() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber();
                finish();
                break;

            case R.id.startLogin:
                passwordStr = password.getText().toString();
                if(passwordStr==null ||passwordStr.equals("")){
                    Logger.e("登录密码为空 ");
                    Toast.makeText(BaseApplication.getContext(), "登录密码为空 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwordStr.equals("321")){
                    startActivityFinish(SettingCategoryActivity.class);
                }else if(passwordStr.equals("321old")){
                    startActivityFinish(PayActivity.class);
                }else{
                    Toast.makeText(BaseApplication.getContext(), "登录密码不对 ", Toast.LENGTH_SHORT).show();
                    Logger.e("登录密码不对 ");
                }
                break;
        }
    }
}