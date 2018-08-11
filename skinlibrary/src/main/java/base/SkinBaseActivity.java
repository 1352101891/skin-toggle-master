package base;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinViewInflaterFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public  abstract  class SkinBaseActivity extends AppCompatActivity {

    private SkinViewInflaterFactory mSkinInflaterFactory;

    private String TAG="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG=getSimpleClassName();
        mSkinInflaterFactory = new SkinViewInflaterFactory();
        LayoutInflaterCompat.setFactory(getLayoutInflater(), mSkinInflaterFactory);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        InitMenus();
        InitViews();
        EventBus.getDefault().register(this);
     }

    protected abstract int getLayoutID();
    protected abstract void InitViews();
    protected abstract void InitMenus();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mSkinInflaterFactory.clean();
        super.onDestroy();
    }


    /**
     * 如果参数是空的，默认皮肤
     * @param sourceBo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void apply(SourceBo sourceBo) {
        //应用皮肤
        Log.e(TAG,"收到皮肤广播:"+sourceBo.getName());
        mSkinInflaterFactory.applySkin();
    }


    public String getSimpleClassName(){
        String name=this.getClass().getSimpleName();
        return name;
    }


    public String TAG() {
        return TAG;
    }

}
