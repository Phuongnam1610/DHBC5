package com.game.dhbc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.game.dhbc.R;
import com.game.dhbc.databinding.IcontextBinding;
import com.game.dhbc.listener.keywordlistener;

import java.util.List;

public class keywordadapter extends RecyclerView.Adapter<keywordadapter.keywordViewHolder> {
    List<String> listkeyword;
    private Context context;
    private final keywordlistener keywordlistener;

    public boolean animationcheck=false;

    public keywordadapter(List<String> listkeyword, Context context, com.game.dhbc.listener.keywordlistener keywordlistener) {
        this.listkeyword = listkeyword;
        this.context = context;
        this.keywordlistener = keywordlistener;
    }

    @NonNull
    @Override
    public keywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IcontextBinding icontextBinding = IcontextBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new keywordViewHolder(icontextBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull keywordViewHolder holder, int position) {
        holder.setData(listkeyword.get(position),position);
    }

    @Override
    public int getItemCount() {
        return listkeyword.size();
    }

    public class keywordViewHolder extends RecyclerView.ViewHolder {

        private final IcontextBinding binding;


        public keywordViewHolder(IcontextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }



        void setData( String keyword, int position) {

            binding.key.setText(keyword);
            binding.getRoot().setOnClickListener(v -> keywordlistener.onClickkeywordlistener(position));
            if (animationcheck ==true) {
                animationfalse();
            }
            else
            {
                binding.key.setTextColor(Color.parseColor("#000000"));
                binding.getRoot().clearAnimation();
            }
        }

        public void animationfalse(){

            for (int i = 0; i <getItemCount(); i++) {
                binding.key.setTextColor(Color.parseColor("#ffffff"));
                Animation animation;
                animation= AnimationUtils.loadAnimation(context, R.anim.shakeanimation);
                binding.getRoot().startAnimation(animation);



            }
        }

    }

}
