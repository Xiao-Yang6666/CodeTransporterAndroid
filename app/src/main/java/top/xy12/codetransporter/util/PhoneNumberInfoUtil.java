package top.xy12.codetransporter.util;

import okhttp3.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XiaoYang
 * @date 2023/12/23 22:17
 */

public class PhoneNumberInfoUtil  {

    // 发送请求并获取电话号码的归属地
    public static void getCarrier(String phoneNumber, final CarrierCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + phoneNumber;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Referer", "https://tcc.taobao.com") // 设置 Referer 头部
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    String carrier = parseCarrier(responseData);
                    callback.onSuccess(carrier);
                } else {
                    callback.onFailure(new IOException("Unexpected response code " + response.code()));
                }
            }
        });
    }

    // 解析 carrier 字段的值
    private static String parseCarrier(String responseData) {
        Pattern pattern = Pattern.compile("carrier:'(.*?)'");
        Matcher matcher = pattern.matcher(responseData);
        if (matcher.find()) {
            return matcher.group(1); // 返回 carrier 的值
        }
        return "未知";
    }

    // 定义回调接口
    public interface CarrierCallback {
        void onSuccess(String carrier);
        void onFailure(Exception e);
    }
}
