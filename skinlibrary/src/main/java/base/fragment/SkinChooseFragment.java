package base.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.skinlibrary.R;
import com.skinlibrary.skinManager.SkinResManager;

import org.greenrobot.eventbus.EventBus;

import base.adapter.SkinAdapter;
import base.callback.itemClickListener;

/**
 * Created by lvqiu on 2018/8/11.
 */

public class SkinChooseFragment extends SkinBaseFragment{
    private RecyclerView mRecyclerView;

    private SkinAdapter adapter;

    @Override
    public int getResourceID() {
        return R.layout.fragment_skinchoose;
    }

    @Override
    public void InitViews() {
        mRecyclerView= findViewById(R.id.skin_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter=new SkinAdapter(getContext());
        mRecyclerView.setAdapter(adapter);
        adapter.seItemtListener(new itemClickListener() {
            @Override
            public void click(int pos) {
                EventBus.getDefault().post(adapter.getSourceBos().get(pos));
            }
        });
        loadItems();
    }

    public void loadItems(){
        adapter.updateItems(SkinResManager.getSourceBos());
    }


}
