package base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skinlibrary.R;
import com.skinlibrary.entity.SourceBo;
import com.skinlibrary.skinManager.SkinResManager;
import com.skinlibrary.util.ConfigAccess;

import java.util.ArrayList;

import base.callback.itemClickListener;

/**
 * Created by lvqiu on 2018/8/11.
 */

public class SkinAdapter extends RecyclerView.Adapter<SkinViewHolder> {
    private int checkPos=0;
    private ArrayList<SourceBo> sourceBos;
    private Context context;
    private itemClickListener listener;

    public SkinAdapter(Context context,ArrayList<SourceBo> sourceBos) {
        this.sourceBos = sourceBos;
        this.context=context;
        findCheckPos();
    }

    public SkinAdapter(Context context) {
        this.context=context;
    }


    @Override
    public SkinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_skin,parent,false);
        SkinViewHolder viewHolder=new SkinViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SkinViewHolder holder, final int position) {
        SkinViewHolder viewHolder=holder;
        if (sourceBos!=null&& sourceBos.size()>0){
            viewHolder.cover.setImageBitmap(null);
            if (checkPos==position){
                viewHolder.cover.setImageResource(R.drawable.yes);
            }
            viewHolder.name.setText(sourceBos.get(position).getDisplayName());
            viewHolder.cover.setBackgroundResource(SkinResManager.getInstance().getResourceID(sourceBos.get(position).getCoverImage()));
            viewHolder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPos=position;
                    notifyDataSetChanged();
                    if (listener!=null){
                        listener.click(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (sourceBos!=null && sourceBos.size()>0){
            return sourceBos.size();
        }
        return 0;
    }

    public void updateItems(ArrayList<SourceBo> bos){
        sourceBos=bos;
        findCheckPos();
        notifyDataSetChanged();
    }

    public void updateItem(SourceBo bo){
        if (sourceBos==null){
            sourceBos=new ArrayList<>();
        }
        if (sourceBos!=null){
            sourceBos.add(bo);
        }
        findCheckPos();
        notifyDataSetChanged();
    }

    public void findCheckPos(){
        SourceBo temp=ConfigAccess.getDefaultSkin();
        if (sourceBos!=null && temp!=null){
            for (int i=0;i<sourceBos.size();i++) {
                if (sourceBos.get(i).getName().equals(temp.getName())){
                    checkPos=i;
                    return;
                }
            }
        }
    }

    public ArrayList<SourceBo> getSourceBos() {
        return sourceBos;
    }

    public void seItemtListener(itemClickListener listener) {
        this.listener = listener;
    }
}


class SkinViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public ImageView cover;
    public SkinViewHolder(View itemView) {
        super(itemView);
        name= (TextView) itemView.findViewById(R.id.name);
        cover= (ImageView) itemView.findViewById(R.id.cover);
    }
}