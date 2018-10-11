package co.medicamecanica.dolutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

public class RestClient {

    public static final String LOGIN = "SERVER_LOGIN";
    public static final String PASSWORD = "SERVER_PASSWORD";
    public static final String TOKEN = "SERVER_TOKEN";

    static SharedPreferences preferences ;
    public static String URL="SERVER_URL";

    //String text = app_preferences.getString("name", "default");
    public static ClientResource BuildClientResource(String uri) {
        store(URL,uri);
        ClientResource cr = new ClientResource(uri+"/api/index.php");
        cr.setRequestEntityBuffering(true);
        cr.accept(MediaType.APPLICATION_JSON);
        return cr;
    }
    public static String fomratDate(Date dt){
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(dt);
    }
public static void store(String name, String string){
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(name,string);
    editor.commit();
}
    public static void conf(Context context) {
        if(preferences==null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Engine.getInstance().getRegisteredConverters()
                .add(new JacksonConverter());
    }

    public static String getURL() {
       return preferences.getString(URL,"");
    }
    public static void  prepareLogin(ClientResource cr,String login,String password) {

      //  String login = preferences.getString(LOGIN, "");
        //String pass = preferences.getString(PASSWORD, "");
        //ClientResource cr = BuildClientResource(mUrl)
        store(LOGIN,login);
        store(PASSWORD,password);
        cr.addSegment("login");
        cr.addQueryParameter("login", login);
        cr.addQueryParameter("password", password);


    }

    public static String getSucces() {
        return preferences.getString(URL,"");
    }

    public static void storeToken(String token) {
        store(TOKEN,token);
    }

    public static boolean setToken() {
        return preferences.getString(TOKEN,null)!=null;
    }
    public static Series<Header> getHeader(ClientResource clientr) {
        ConcurrentMap<String, Object> attrs = clientr.getRequest().getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HeaderConstants.ATTRIBUTE_HEADERS);
        if (headers == null) {
            headers = new Series<>(Header.class);
            Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, headers);
            if (prev != null) {
                headers = prev;
            }
        }
        return headers;
    }

    public static String getToken() {
        return preferences.getString(TOKEN,null);
    }
    public static String getLogin() {
        return preferences.getString(LOGIN,null);
    }

    public static void addToken(ClientResource cr, String token) {
        getHeader(cr).add("DOLAPIKEY", token);
    }

    public static String getPassword() {
        return preferences.getString(PASSWORD,null);
    }

    public static class getUserTask extends AsyncTask<Void, Void, Integer> {

        private String mLogin;
        private User user;

        private UserListener userListener;

        public getUserTask(String mRef,UserListener userListener) {
            this.userListener=userListener;
            this.mLogin = mRef;
        }
        @Override
        protected Integer doInBackground(Void... voids) {

            ClientResource cr = RestClient.BuildClientResource(RestClient.getURL());
            RestClient.addToken(cr,RestClient.getToken());
            cr.addSegment("users");
            cr.addQueryParameter("sqlfilters","login='"+ mLogin+"'");
            UserResource ur = cr.wrap(UserResource.class);
            try {
                User[] uss = ur.retrive();
                if(uss!=null) {
                    if (uss.length > 0) {
                        user = uss[0];
                        Log.i("user", user.toString());
                    } else {
                        Log.i("user", "not found");
                    }
                }
                return 200;

            }catch (ResourceException e){

                e.printStackTrace();
                Log.e("get",e.getResource().toString());
                return e.getResponse().getStatus().getCode();
            }

        }

        @Override
        protected void onPostExecute(Integer code) {
            userListener.onPostExecute(user,code);
        }
    }
}
