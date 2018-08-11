package base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lvqiu on 2018/8/11.
 */

public abstract class SkinBaseFragment extends Fragment {
    private String TAG="";
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        TAG=getSimpleClassName();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= getLayoutInflater().inflate(getResourceID(),container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public abstract int getResourceID();

    public final  <T extends View> T findViewById(int id){
        return (T) rootView.findViewById(id);
    }

    public abstract void InitViews();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public LayoutInflater getLayoutInflater(){
        if (getActivity()!=null){
            return getActivity().getLayoutInflater();
        }
        return null;
    }

    public String getSimpleClassName(){
        String name=this.getClass().getSimpleName();
        return name;
    }
    public String TAG() {
        return TAG;
    }

}
