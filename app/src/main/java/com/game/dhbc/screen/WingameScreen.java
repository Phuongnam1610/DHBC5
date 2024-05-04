package com.game.dhbc.screen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.game.dhbc.databinding.WingameBinding;


public class WingameScreen extends AppCompatActivity {
    WingameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=WingameBinding.inflate(getLayoutInflater());
        binding.ok.setOnClickListener(view -> {
            finish();
        });
            String whowin=getIntent().getStringExtra("whowin");
            if(whowin==null){
                whowin="YOUWIN";
            }
            binding.youwin.setText(whowin);

        setContentView(binding.getRoot());
    }
}