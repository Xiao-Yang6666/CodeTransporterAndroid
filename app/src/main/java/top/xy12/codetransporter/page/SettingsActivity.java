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
    private EditText mqttTopicCallEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化视图
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        mqttUrlEditText = findViewById(R.id.mqttUrl);
        mqttTopicEditText = findViewById(R.id.mqttTopic);
        mqttTopicCallEditText = findViewById(R.id.mqttTopicCall);

        // 从SharedPreferences获取值，如果没有则使用默认值
        loadPreferences(getApplicationContext());

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this::onConfirmButtonClick);
    }

    private void onConfirmButtonClick(View view) {
        String phoneNumber = phoneNumberEditText.getText().toString();
        String mqttUrl = mqttUrlEditText.getText().toString();
        String mqttTopic = mqttTopicEditText.getText().toString();
        String mqttTopicCall = mqttTopicCallEditText.getText().toString();

        // 保存数据到SharedPreferences
        savePreferences(phoneNumber, mqttUrl, mqttTopic, mqttTopicCall);

        // 显示成功提示
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
    }

    private void savePreferences(String phoneNumber, String mqttUrl, String mqttTopic, String mqttTopicCall) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConf.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(AppConf.KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(AppConf.KEY_MQTT_URL, mqttUrl);
        editor.putString(AppConf.KEY_MQTT_TOPIC, mqttTopic);
        editor.putString(AppConf.KEY_MQTT_TOPIC_CAll, mqttTopicCall);

        editor.apply();
    }

    private void loadPreferences(Context context) {
        // 加载缓存的数据
        AppConf.loadFromSharedPreferences(context);
        // 将加载的数据设置到相应的EditText中
        phoneNumberEditText.setText(AppConf.phoneNumber);
        mqttUrlEditText.setText(AppConf.mqttUrl);
        mqttTopicEditText.setText(AppConf.mqttTopic);
        mqttTopicCallEditText.setText(AppConf.mqttTopicCall);
    }
}
