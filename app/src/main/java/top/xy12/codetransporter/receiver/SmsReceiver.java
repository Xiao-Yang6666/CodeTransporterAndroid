package top.xy12.codetransporter.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import top.xy12.codetransporter.conf.AppConf;
import top.xy12.codetransporter.service.MqttServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XiaoYang
 * @date 2023/12/20 21:04
 */
public class SmsReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                AppConf.loadFromSharedPreferences(context);
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = smsMessage.getMessageBody();

                    // 提取验证码
                    String verificationCode = extractSenderAndCode(messageBody);
                    // 发送验证码到mqtt服务器
                    publishCodeViaMqtt(context, verificationCode);
                }
            }
        }
    }

    private static String extractSenderAndCode(String message) {
        Log.d("SmsReceiver", "message: " + message);
        JSONObject jsonObject = new JSONObject();
        // 正则表达式，同时匹配"[]"内的内容和"码"后跟的4位或6位数字
        Pattern pattern = Pattern.compile("【(.*?)】.*?码\\D*(\\d{4}(?:\\d{2})?)");
        Matcher matcher = pattern.matcher(message);
        try {
            if (matcher.find()) {
                String sender = matcher.group(1); // "[]"内的文本
                String code = matcher.group(2);   // 验证码

                jsonObject.put("sender", sender);
                jsonObject.put("smsCode", code);
            }
            jsonObject.put("smsMsg", message);
            jsonObject.put("phoneNumber", AppConf.phoneNumber);
            jsonObject.put("type", "sms");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject.toString();
    }

    private void publishCodeViaMqtt(Context context, String code) {
        Log.d("SmsReceiver", "code: " + code);

        // 使用MqttService发布消息
        MqttServer.getMqttUtil(context).publishMessage(AppConf.mqttTopic, code);
    }
}
