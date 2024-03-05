package service;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferencesManager {
    private final String SP_NAME = "sp";
    private final int DEFAULT_VALUE = 0;
    private final String KEY_USER_TOKEN = "user_token";
    private final String KEY_USER_USERNAME = "user_username";
    private final String KEY_USER_ID = "user_id";

    private final SharedPreferences sharedPreferences;

    public SharePreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN,"");
    }

    public void setUserToken(String value){
        sharedPreferences.edit().putString(KEY_USER_TOKEN,value).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_USERNAME,"");
    }

    public void setUserName(String value){
        sharedPreferences.edit().putString(KEY_USER_USERNAME,value).apply();
    }

    public int getId(){
        return sharedPreferences.getInt(KEY_USER_ID,DEFAULT_VALUE);
    }
    public void setId(int value){
        sharedPreferences.edit().putInt(KEY_USER_ID,value).apply();
    }

    //Context.MODE_PRIVATE: 指定该SharedPreferences数据只能被本应用程序读、写；
    //Context.MODE_WORLD_READABLE:  指定该SharedPreferences数据能被其他应用程序读，但不能写；
    //Context.MODE_WORLD_WRITEABLE:  指定该SharedPreferences数据能被其他应用程序读；
    //Context.MODE_APPEND：该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件；
}
