package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
public class WeatherServiceImpl implements WeatherService  {
   @Resource
    RestTemplate restTemplate;

   @Resource
   StringRedisTemplate template;

   @Value("${spring.weather.key}")
   String key;

    /**
     *  从API获取天气数据
      * @param latitude
     * @param longitude
     * @return
     */
   @Override
    public WeatherVO fetchWeather(double latitude, double longitude) {
        return fetchFromCache(latitude, longitude);
    }

    /**
     *  从缓存中获取天气数据
     * @param latitude
     * @param longitude
     * @return
     */
    private WeatherVO fetchFromCache(double latitude, double longitude) {
        // 将压缩的字符串解压并转换为JSON对象
        JSONObject geo = this.decompressStringToJson(restTemplate.getForObject("https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key, byte[].class));
        // 如果geo对象为空，则返回null
        if (geo == null) return null;
        // 从geo对象中获取location对象
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        // 从location对象中获取id字段
        int id = location.getInteger("id");
        // 创建缓存键值
        String key = Const.FORUM_WEATHER_CACHE + id;
        // 从redis缓存中获取数据
        String cache = template.opsForValue().get(key);
        // 如果缓存不为空，则将其转换为WeatherVO对象并返回
        if (cache != null) {
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        }
        // 从API获取WeatherVO对象
        WeatherVO vo = fetchFromApi(id, location);
        // 如果获取的WeatherVO对象为空，则返回null
        if (vo == null) return null;
        // 将获取的WeatherVO对象存入redis缓存，有效期为1小时
        template.opsForValue().set(key, JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
        // 返回获取的WeatherVO对象
        return vo;
    }

    /**
     * 从API获取天气数据并解压
     * @param id
     * @param location
     * @return
     */
    private WeatherVO fetchFromApi(int id, JSONObject location) {
        // 创建一个新的WeatherVO对象
        WeatherVO vo = new WeatherVO();
        // 在WeatherVO对象中设置位置信息
        vo.setLocation(location);

        // 调用API获取当前天气数据并解压响应
        JSONObject now = this.decompressStringToJson(restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key, byte[].class));
        // 如果响应为空，返回null
        if (now == null) return null;
        // 在WeatherVO对象中设置当前天气数据
        vo.setNow(now.getJSONObject("now"));

        // 调用API获取每小时天气数据并解压响应
        JSONObject hourly = this.decompressStringToJson(restTemplate.getForObject("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key, byte[].class));
        // 如果响应为空，返回null
        if (hourly == null) return null;
        // 在WeatherVO对象中设置每小时天气数据，限制为前5小时
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));

        // 返回填充好数据的WeatherVO对象
        return vo;
    }
    /**
     * 将压缩的字节数组解压缩为JSON对象
     * @param data
     * @return
     */
    private JSONObject decompressStringToJson(byte[] data) {
        // 方法作用：将gzip压缩的字节数组解压缩为JSON对象
        ByteArrayOutputStream stream = new ByteArrayOutputStream(); // 创建一个字节流
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data)); // 创建一个gzip输入流，从给定的字节数组中读取数据
            byte[] buffer = new byte[1024]; // 创建一个缓冲区
            int read;
            while ((read = gzipInputStream.read(buffer)) != -1) { // 从gzip输入流中读取数据到缓冲区
                stream.write(buffer, 0, read); // 将缓冲区中的数据写入字节流
            }
            gzipInputStream.close(); // 关闭gzip输入流
            stream.close(); // 关闭字节流
            return JSONObject.parseObject(stream.toString()); // 将字节流转换为字符串，然后解析为JSON对象并返回
        } catch (Exception e) {
            return null; // 如果在解压缩过程中发生异常，则返回null
        }
    }
}
