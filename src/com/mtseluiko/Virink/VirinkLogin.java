package com.mtseluiko.Virink;

/**
 * Created by Михаил on 09.08.2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class VirinkLogin {
    private  static boolean logged =false;
    public static VirinkProfile vprofile = new VirinkProfile();
    public static String mail="";
    public static String password="";
    public static String cPHPSESSID="";
    public static String cHASH="";
    public static int notificationCount=0;
    public static boolean authnotifSend=false;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor ed;
    private static Context context;
    private int code;

    public boolean isLogged(){
        return logged;
    }
    public VirinkLogin(Context context){
        this.context=context;
    }

    private static void getPref() {//сохраненные данные
        Log.d("URURU", context==null?"null":"все ахрэнэнно*НЕЙТИРИ-ФЕЙС*");
        sPref = context.getSharedPreferences("loginset", context.MODE_PRIVATE);
        ed = sPref.edit();
        mail = sPref.getString("mail", "");
        password = sPref.getString("password", "");
        cPHPSESSID = sPref.getString("PHPSESSID", "");
        cHASH = sPref.getString("hash", "");
    }
    private static void getUserData(String id) throws IOException {//данные о пользователе
        Element profilepage = Jsoup.connect("http://virink.com/"+id).header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .cookie("hash", cHASH).cookie("PHPSESSID", cPHPSESSID).get().body();
        Element data = profilepage.select("div#profile-param").first();
        Element uname =  profilepage.select("a.profile-user__name").first();
        Element avatar =  profilepage.select("img.lazy").first();
        Element BIlink = profilepage.select("div.profile-head__inner").first();
        Element urate = profilepage.select("span.profile-user__rating").first();
        vprofile.userid= id;
        if(data!=null)vprofile.power=data.attr("data-power");
        else vprofile.power="";
        if(avatar!=null)vprofile.avatarLink="http:"+profilepage.select("img.lazy").first().attr("src");
        else vprofile.avatarLink="";
        if(BIlink!=null){
            String t =  BIlink.attr("style");
            if(t.length()>0 && t.indexOf("//")>=0)vprofile.backgroundImageLink="http:"+t.substring(t.indexOf("//"),t.length()-1);
        }
        else vprofile.backgroundImageLink="";
        if(uname!=null) vprofile.username = uname.text();
        else vprofile.username="";
        if(urate!=null) vprofile.rate=urate.text().substring(8,urate.text().length());
        else vprofile.rate="";
    }

    public static int checkLogged(){
        if (!cPHPSESSID.isEmpty() && !cHASH.isEmpty()) {
            try {
                Element mainpage = Jsoup.connect("http://virink.com").header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .cookie("hash", cHASH).cookie("PHPSESSID", cPHPSESSID).get().body();
                Element data = mainpage.select("div#profile-param").first();
                if (data != null && data.attr("data-logged").equals("1")) {
                    getUserData(data.attr("data-id"));
                    logged = true;
                    return 0;
                }
                else return 1;
            } catch (IOException e) {
                return -1;
            }
        }
        else return 1;
    }
    public static int login() {
        getPref();
        int chl=0;
        Element mainpage = null;
        Element data = null;
        try {//попытка залогинится
            chl=checkLogged();
            if (chl==0 || chl==-1) return chl;
            //не удалось зайти с помощью cookies
            //попытка зайти с помощью логина-пароля
            if (!mail.isEmpty() && !password.isEmpty()) {
                Connection.Response res = Jsoup.connect("http://virink.com/index.php?r=login")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .referrer("http://virink.com/login")
                        .data("email", mail)
                        .data("password", password)
                        .method(Connection.Method.POST)
                        .execute();
                if (res.body().contains("0")) {
                    ed.putString("PHPSESSID", res.cookie("PHPSESSID"));
                    ed.putString("hash", res.cookie("hash"));
                    ed.commit();
                    cPHPSESSID = res.cookie("PHPSESSID");
                    cHASH = res.cookie("hash");

                    mainpage = Jsoup.connect("http://virink.com").header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                            .cookie("hash", cHASH).cookie("PHPSESSID", cPHPSESSID).get().body();
                    data = mainpage.select("div#profile-param").first();
                    getUserData(data.attr("data-id"));

                    logged = true;
                    return 0;
                }
            }  //не удалось зайти - удаляем сохраненные данные
            ed.putString("password", "");
            ed.putString("PHPSESSID", "");
            ed.putString("hash", "");
            ed.commit();
            password = "";
            cPHPSESSID = "";
            cHASH = "";
            logged=false;
            return 1; //неверный пароль
        } catch (IOException e) {
            logged=false;
            return -1;//инет
        }
    }
    public static String getNotifications(){
        return getNotifications(null);
    }
    public static String getNotifications(String id){
        Connection.Response doc=null;
        JSONArray notifJSONdata=null;
        if(id==null) {
            try {
                doc = Jsoup.connect("http://virink.com/index.php")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .referrer("http://virink.com/feedback")
                        .data("r", "getNotification")
                        .cookie("hash", cHASH).cookie("PHPSESSID", cPHPSESSID)
                        .method(Connection.Method.POST)
                        .execute();
                Document doc2 = Jsoup.connect("http://virink.com/feedback").header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .cookie("hash", cHASH).cookie("PHPSESSID",cPHPSESSID).get();
                String tttt = doc2.body().text();
            } catch (IOException e) {
                return null;
            }
        }
        else{
            try {
                doc = Jsoup.connect("http://virink.com/index.php")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .referrer("http://virink.com/feedback")
                        .data("r", "getNotification")
                        .data("id", id)
                        .cookie("hash", cHASH).cookie("PHPSESSID", cPHPSESSID)
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                return null;
            }
        }
        try {
            notifJSONdata = new JSONArray(doc.body());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Log.d("LOKTAR", notifJSONdata.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "0";
    }
    public static int checkNotifications() {
        int notifcount=0;
        if(!logged) return -1;
        try {
            Document doc2 = Jsoup.connect("http://virink.com").header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .cookie("hash", cHASH).cookie("PHPSESSID",cPHPSESSID).get();
            Element d2elem = doc2.select("span.notification").first();
            if (d2elem == null) {
                notifcount=0;
            } else {
                notifcount = Integer.parseInt(d2elem.text());
            }
        } catch (IOException e) {
            notifcount=-1;
        }
        return notifcount;
    }

    public static class VirinkProfile {
        String username="";
        String userid="";
        String avatarLink="";
        String power="";
        String rate="";
        String backgroundImageLink="";

    }
}
