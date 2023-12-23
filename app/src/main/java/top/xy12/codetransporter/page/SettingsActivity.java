package top.xy12.codetransporter.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import top.xy12.codetransporter.R;
import top.xy12.codetransporter.conf.AppConf;

public class SettingsActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText mqttUrlEditText;
    private EditText mqttTopicEditText;

    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_MQTT_URL = "mqttUrl";
    private static final String KEY_MQTT_TOPIC = "mqttTopic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化视图
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        mqttUrlEditText = findViewById(R.id.mqttUrl);
        mqttTopicEditText = findViewById(R.id.mqttTopic);

        // 从SharedPreferences获取值，如果没有则使用默认值
        loadPreferences();

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this::onConfirmButtonClick);
    }

    private void onConfirmButtonClick(View view) {
        String phoneNumber = phoneNumberEditText.getText().toString();
        String mqttUrl = mqttUrlEditText.getText().toString();
        String mqttTopic = mqttTopicEditText.getText().toString();

        // 保存数据到SharedPreferences
        savePreferences(phoneNumber, mqttUrl, mqttTopic);

        // 显示成功提示
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
    }

    private void savePreferences(String phoneNumber, String mqttUrl, String mqttTopic) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_MQTT_URL, mqttUrl);
        editor.putString(KEY_MQTT_TOPIC, mqttTopic);

        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 从SharedPreferences获取值，如果没有则使用默认值
        String phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, AppConf.phoneNumber);
        String mqttUrl = sharedPreferences.getString(KEY_MQTT_URL, AppConf.mqttUrl);
        String mqttTopic = sharedPreferences.getString(KEY_MQTT_TOPIC, AppConf.mqttTopic);

        // 将加载的数据设置到相应的EditText中
        phoneNumberEditText.setText(phoneNumber);
        mqttUrlEditText.setText(mqttUrl);
        mqttTopicEditText.setText(mqttTopic);
    }
}
