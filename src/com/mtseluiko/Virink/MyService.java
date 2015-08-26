package com.mtseluiko.Virink;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
	private Timer mTimer;
	NotificationManager nm;
	public static Context cntxt;
	private int notifCount=0;
	public static VirinkLogin virinkLogin=null;
	private MyTimerTask mMyTimerTask;
	final String LOG_TAG = "myLogs";
	class MyTimerTask extends TimerTask {
		private Context c;

		public MyTimerTask(Context c) {
			this.c = c;
		}

		@Override
		public void run() {
			int k = 0;
			String text = "";
			String title="Virink";
			if (virinkLogin == null) {
				MyService.virinkLogin = new VirinkLogin(c);
			}
			if (!virinkLogin.isLogged()) {
				k = virinkLogin.login();
				if (k == 1 && !virinkLogin.authnotifSend) {
					title += ":Ошибка при авторизации";
					text = "Проверьте пароль";
					sendNotif(title, text,LoginActivity.class);
					virinkLogin.authnotifSend=true;
				}

				if (k == -1) {
					virinkLogin.authnotifSend=false;
				}

			}
			else {
				Log.d(LOG_TAG, "old="+ virinkLogin.notificationCount);
				notifCount = virinkLogin.checkNotifications();
				Log.d(LOG_TAG, "new="+notifCount);
				if (notifCount == -1) {
					//todo:инет
				} else if (notifCount!=0 && notifCount > virinkLogin.notificationCount) {
					virinkLogin.authnotifSend=false;
					String strnc = String.valueOf(notifCount);
					char last = strnc.charAt(strnc.length() - 1);
					switch (last) {
						case '1':
							text = " новое уведомление";
							break;
						case '2':
						case '3':
						case '4':
							text = " новых уведомления";
							break;
						default:
							text = " новых уведомлений";

					}
					if (strnc.endsWith("11")) text = " новых уведомлений";

					Log.d(LOG_TAG, String.valueOf(k));
					sendNotif(title, "Получено " + notifCount + text,VirinkNotifications.class);

				}
				virinkLogin.notificationCount=notifCount;
			}
			Log.d(LOG_TAG, "Тик " + virinkLogin.isLogged() + " " + k);

		}
	}

	public void onCreate() {
		super.onCreate();
		cntxt=this;
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand");
		mTimer = new Timer();
		mMyTimerTask = new MyTimerTask(this);
		mTimer.schedule(mMyTimerTask, 1000, 15000);
		int ttr = super.onStartCommand(intent, flags, startId);
		String ttr1 = "";
		if (ttr==START_NOT_STICKY){
			 ttr1 = "Start_not_sticky";
		}
		else if(ttr==START_STICKY) {
		 	ttr1="start_sticky";
		}
		else if(ttr==START_STICKY_COMPATIBILITY) {
			ttr1="startstickyccompability";
		}
		else {
			ttr1= String.valueOf(ttr);
		}
		Log.d(LOG_TAG, ttr1);
		return ttr;
	}

	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");
		super.onDestroy();

	}

	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "onBind");
		return null;
	}

	void sendNotif(String title,String text, Class activityClass) {
		Notification notif = new Notification(R.drawable.ic_notif, text,
				System.currentTimeMillis());
		notif.defaults=Notification.DEFAULT_ALL;
		Intent intent = new Intent(this, activityClass);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notif.setLatestEventInfo(this, title, text, pIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notif);
	}
}
