package top.xy12.codetransporter.conf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author XiaoYang
 * @date 2023/12/21 12:20
 */
public class AppConf {
    public static String phoneNumber = "";
    public static String mqttUrl = "";
    public static String mqttTopic = "";
    public static String mqttTopicCall = "";


    public static final String PREF_NAME = "MyPreferences";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_MQTT_URL = "mqttUrl";
    public static final String KEY_MQTT_TOPIC = "mqttTopic";
    public static final String KEY_MQTT_TOPIC_CAll = "mqttTopicCall";

    public static void loadFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, "");
        mqttUrl = sharedPreferences.getString(KEY_MQTT_URL, "tcp://124.222.11.237:1883");
        mqttTopic = sharedPreferences.getString(KEY_MQTT_TOPIC, "sms/topic");
        mqttTopicCall = sharedPreferences.getString(KEY_MQTT_TOPIC_CAll, "call/topic");
    }
}
