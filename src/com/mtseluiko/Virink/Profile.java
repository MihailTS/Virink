package com.mtseluiko.Virink;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by Михаил on 11.08.2015.
 */
public class Profile extends Activity {
    ImageView ivavatar;
    ImageView ivbackground;
    TextView tvusername;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);/*
        if (MyService.virinkLogin==null) {  //todo: сделать асинхронный запрос с выводом через Пикассо
            getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
        }
        else {
            if(!MyService.virinkLogin.isLogged()){
                Intent intentpr = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentpr);
            }
        }*/
        ivavatar = (ImageView) findViewById(R.id.ivprAvatar);
        ivbackground = (ImageView) findViewById(R.id.ivprBackground);
        tvusername = (TextView) findViewById(R.id.tvprUsername);
        if (MyService.virinkLogin == null || MyService.virinkLogin.vprofile.avatarLink.isEmpty())
            new GetAvatarTask().execute();
        else {
            Picasso.with(getApplicationContext())
                    .load(MyService.virinkLogin.vprofile.avatarLink)
                    .error(R.drawable.ic_erravatar)
                    .placeholder(R.drawable.progress_image)
                    .into(ivavatar);
            if (!MyService.virinkLogin.vprofile.backgroundImageLink.isEmpty()) {
                Picasso.with(getApplicationContext())
                        .load(MyService.virinkLogin.vprofile.backgroundImageLink)
                        .into(ivbackground);
                tvusername.setText(MyService.virinkLogin.vprofile.username);
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        /*if (MyService.virinkLogin==null) {
            getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
        }
        else {
            if(!MyService.virinkLogin.isLogged()){
                Intent intentpr = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentpr);
            }
        }*/
    }
    private class GetAvatarTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            ivavatar.setImageResource(R.drawable.progress_image);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int k=0;
            if (MyService.virinkLogin==null) {  //todo: сделать асинхронный запрос с выводом через Пикассо
                getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
                /*try{Thread.sleep(1000);} catch(Exception e){}
                k=MyService.virinkLogin.login();*/
            }
            else {
                k=MyService.virinkLogin.checkLogged();
                /*if(k==1){
                    Intent intentpr = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentpr);
                }*/
            }
            while(MyService.virinkLogin==null || MyService.virinkLogin.vprofile.avatarLink==null ||MyService.virinkLogin.vprofile.avatarLink.isEmpty()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*try {
                Thread.sleep(200);
            } catch (InterruptedException e) { }*/
            return k;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==1){
                    Intent intentpr = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentpr);
            }
            if(!MyService.virinkLogin.vprofile.avatarLink.isEmpty()){
            Picasso.with(getApplicationContext())
                    .load(MyService.virinkLogin.vprofile.avatarLink)
                    .error(R.drawable.ic_erravatar)
                    .placeholder(R.drawable.progress_image)
                    .into(ivavatar);
        }
            if(!MyService.virinkLogin.vprofile.backgroundImageLink.isEmpty()) {
                Picasso.with(getApplicationContext())
                        .load(MyService.virinkLogin.vprofile.backgroundImageLink)
                        .into(ivbackground);
                tvusername.setText(MyService.virinkLogin.vprofile.username);
            }
            super.onPostExecute(result);
        }
    }
}
