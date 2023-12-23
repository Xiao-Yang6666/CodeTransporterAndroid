package top.xy12.codetransporter.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import top.xy12.codetransporter.conf.AppConf;
import top.xy12.codetransporter.service.MqttServer;
import top.xy12.codetransporter.util.PhoneNumberInfoUtil;



/**
 * @author XiaoYang
 * @date 2023/12/23 22:05
 */
public class CallReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (incomingNumber != null && !incomingNumber.isEmpty()) {
                AppConf.loadFromSharedPreferences(context);
                PhoneNumberInfoUtil.getCarrier(incomingNumber, new PhoneNumberInfoUtil.CarrierCallback() {
                    @Override
                    public void onSuccess(String carrier) {
                        // 处理获取到的 carrier 信息
                        Log.d("CallReceiver", "来电号码: " + incomingNumber);
                        Log.d("CallReceiver", "来电归宿地: " + carrier);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("incomingPhoneNumber", incomingNumber);
                        jsonObject.put("phoneNumberLocation", carrier);
                        publishCallViaMqtt(context, jsonObject.toJSONString());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("CallReceiver", "无法获取运营商", e);
                    }
                });
            }
        }
    }

    private void publishCallViaMqtt(Context context, String call) {
        Log.d("CallReceiver", "mqttTopicCall: " + AppConf.mqttTopicCall);
        Log.d("CallReceiver", "call: " + call);

        // 使用MqttService发布消息
        MqttServer.getMqttUtil(context).publishMessage(AppConf.mqttTopicCall, call);
    }
}
