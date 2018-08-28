package com.deepaksharma.webaddicted.ui.hidden;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.deepaksharma.webaddicted.R;
import com.deepaksharma.webaddicted.databinding.CustomHiddenBinding;
import com.deepaksharma.webaddicted.db.entity.HiddenInfo;


import java.util.List;

/**
 * Created by Deepak Sharma on 06-05-2018.
 */


public class RecyclViewAdapter extends RecyclerView.Adapter<RecyclViewAdapter.RecyclerViewHolder> {
    Context mContext;
    List<HiddenInfo> mHiddenFiles;

    public RecyclViewAdapter(Context context, List<HiddenInfo> hiddenInfos) {
        this.mHiddenFiles = hiddenInfos;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomHiddenBinding caBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.custom_hidden,
                        parent, false);
        return new RecyclViewAdapter.RecyclerViewHolder(caBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclViewAdapter.RecyclerViewHolder holder, int position) {
        holder.binding.txtFileName.setText(mHiddenFiles.get(position).getFileName());
        holder.binding.txtFilePath.setText(mHiddenFiles.get(position).getFilePath());

    }

    @Override
    public int getItemCount() {
        int fileSize =0;
        if(mHiddenFiles!=null && mHiddenFiles.size() > 0)
            fileSize = mHiddenFiles.size();
        return fileSize;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CustomHiddenBinding binding;

        public RecyclerViewHolder(CustomHiddenBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

    }
}
