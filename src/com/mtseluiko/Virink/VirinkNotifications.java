package com.mtseluiko.Virink;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Created by Михаил on 04.08.2015.
 */
public class VirinkNotifications extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);


        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rlnotif);

        LayoutInflater ltInflater = getLayoutInflater();
        //TODO: добавить вывод данных
        new GetNotifTask().execute();
    }
    private class GetNotifTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int k=0;
            if(MyService.virinkLogin==null){
                getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
            }
            else if(MyService.virinkLogin.checkLogged()==0){

                MyService.virinkLogin.getNotifications();

            }
            return k;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }
}
