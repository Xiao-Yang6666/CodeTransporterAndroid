package top.xy12.codetransporter.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import top.xy12.codetransporter.conf.AppConf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * @author XiaoYang
 * @date 2023/5/19 09:37
 */
public class MqttServer {
    private MqttClient mqttClient;
    private MqttConnectOptions options;

    private static MqttServer mqttServer;
    // 消息处理器
    private static Map<String, Handler> messageHandlers = new HashMap<>();

    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_MQTT_URL = "mqttUrl";

    /**
     * 单例模式获取对象
     * @param context 以为需要获取设备唯一id 所以需要一个上下文
     * @return mqttUtil对象
     */
    public synchronized static MqttServer getMqttUtil(Context context) {
        if (mqttServer == null) {
            mqttServer = new MqttServer(context);
        }

        return mqttServer;
    }

    private MqttServer(Context context) {
        try {
            // 获取设备id
            String clientId = MqttClient.generateClientId();
            // 准备连接参数
            mqttClient = new MqttClient(getMqttUrl(context), clientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setKeepAliveInterval(20);
            options.setConnectionTimeout(10);
            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "断链重新连接.");
                    // 重新链接
                    connect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 接收到消息
                    Log.d(TAG, "接收到消息: " + message);
                    processMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // 获取链接
        connect();
        // 启动保持连接线程
        startReconnect();
    }

    private String getMqttUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MQTT_URL, AppConf.mqttUrl);
    }

    /**
     * 接收消息处理器
     *
     * @param topic 主题
     * @param message 消息
     */
    public void processMessage(String topic, MqttMessage message) {
        if (messageHandlers.containsKey(topic)) {

            switch (topic) {
                case "aaa":
                    Log.e(TAG, "processMessage: " + message);
                    break;
                case "bbb":
                    Log.e(TAG, "processMessage: " + message);
                    break;
            }

        } else {
            Log.e(TAG, "No handler found for topic: " + topic);
        }
    }
    public void publishMessage(String topic, String payload) {
        MqttMessage message = new MqttMessage(payload.getBytes());

        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 订阅主题
     * @param topic 主题
     * @param qos 等级
     */
    public MqttServer subscribe(String topic, int qos, Handler handler) {
        try {
            if (!mqttClient.isConnected()) {
               connect();
            }
            if (mqttClient.isConnected()) {
                mqttClient.subscribe(topic, qos);
                // 消息处理器绑定
                messageHandlers.put(topic, handler);
                Log.d(TAG, "subscribe: 订阅成功.");
            } else {
                Log.e(TAG, "subscribe: 重试后仍然没有连接到服务器.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return mqttServer;
    }

    /**
     * 开始重新连接
     */
    private void startReconnect() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (!mqttClient.isConnected()) {
                connect();
            }
        }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
    }


    /**
     * 连接mqtt服务器
     */
    private void connect() {
        if (!mqttClient.isConnected()) {
            try {
                Log.d(TAG, "connect: 已连接");
                mqttClient.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
