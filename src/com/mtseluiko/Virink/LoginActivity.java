package com.mtseluiko.Virink;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

import android.content.Intent;

public class LoginActivity extends Activity {
    ImageView iv;
    SharedPreferences sPref;
    TextView tvStatus;
    Button btnLogin;
    EditText etMail;
    EditText etPass;
    ProgressBar mProgressBar;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tvstatustext", tvStatus.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvStatus.setText(savedInstanceState.getString("tvstatustext"));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
       /* if (MyService.virinkLogin==null) {
            getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
        }
        else {
            if(MyService.virinkLogin.isLogged()){
                Intent intentpr = new Intent(getApplicationContext(), Profile.class);
                startActivity(intentpr);
            }
        }   */
        sPref = getSharedPreferences("loginset", MODE_PRIVATE);
        iv = (ImageView) findViewById(R.id.imageView);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        etMail = (EditText) findViewById(R.id.etMail);
        etPass = (EditText) findViewById(R.id.etPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Handler myHandler = new Handler(); // автоматически привязывается к текущему потоку.

        View.OnClickListener oclbtnUpdate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("");
                String mail = etMail.getText().toString();
                String password = etPass.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    tvStatus.setText("Введите mail/password");
                }
                else {
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("PHPSESSID", "");
                    ed.putString("hash", "");
                    ed.putString("mail", mail);
                    ed.putString("password", password);
                    ed.commit();
                    new LoginTask().execute();
                }

            }
        };
        btnLogin.setOnClickListener(oclbtnUpdate);
    }

    private class LoginTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int k=MyService.virinkLogin.login();
            if(MyService.virinkLogin!=null) Log.d("MY", String.valueOf(k)+" "+MyService.virinkLogin.vprofile.username);
            else  Log.d("MY", String.valueOf(k));
            return k;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result==-1) tvStatus.setText("Ошибка подключения");
            else if(result==1) tvStatus.setText("Неверный пароль");
            else if(result==0) {
                Intent intentpr = new Intent(getApplicationContext(), Profile.class);
                startActivity(intentpr);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }
    }


}
