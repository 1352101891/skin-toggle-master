package base;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.skinlibrary.entity.ActionBo;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;
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
     * 请求加载皮肤资源，如果参数是空的，默认皮肤
     * @param bo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public final void applySKinPlugin(SourceBo bo){
        Log.e(TAG(),"正在设置皮肤！");
        SkinResManager.getInstance().applySKinPlugin(bo);
    }


    /**
     * 应用皮肤资源，
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public final  void apply(ActionBo bo) {
        //应用皮肤
        Log.e(TAG,"收到应用皮肤资源的广播");
        mSkinInflaterFactory.applySkin();
    }


    public final String getSimpleClassName(){
        String name=this.getClass().getSimpleName();
        return name;
    }


    public final String TAG() {
        return TAG;
    }

}
