package com.game.dhbc.screen;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.game.dhbc.databinding.StartscreenBinding;
import com.game.dhbc.utilities.Constants;
import com.game.dhbc.utilities.PreferenceManager;

public class MainActivityScreen extends AppCompatActivity {
    StartscreenBinding binding;
    public static MediaPlayer mediaPlayer, backgroundSound;
    PreferenceManager preferenceManager;
    public static Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

        binding = StartscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int resID = getResources().getIdentifier("wii", "raw", getPackageName());

        backgroundSound = MediaPlayer.create(getApplicationContext(),resID);
        backgroundSound.setLooping(true);
        backgroundSound.start();

        preferenceManager = new PreferenceManager(this);
        binding.kyluc.setText("RECORD: "+String.valueOf(preferenceManager.getkyluc(Constants.KEY_KYLUC)));
        binding.btnplay.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),PlayScreen.class);
            i.putExtra("playmode",1);
            startActivity(i);
        });
        binding.btn2Player.setOnClickListener(view -> {

            Intent i = new Intent(getApplicationContext(),PlayScreen.class);
            i.putExtra("playmode",2);
            startActivity(i);

        });
    }

    public static void showToast(String message, Context context) {
        // Cancel the previous toast if it is still visible
        if (mToast != null) {
            mToast.cancel();
        }

        // Show the new toast
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        binding.kyluc.setText(String.valueOf(preferenceManager.getkyluc(Constants.KEY_KYLUC)));
    }
}