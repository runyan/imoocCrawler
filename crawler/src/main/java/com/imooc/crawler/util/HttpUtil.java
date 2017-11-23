package com.imooc.crawler.util;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import lombok.Cleanup;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient工具类
 * @author yanrun
 *
 */
public class HttpUtil {
	
	private RequestConfig requestConfig = RequestConfig.custom()  
            .setSocketTimeout(Constraints.TIME_OUT)  
            .setConnectTimeout(Constraints.TIME_OUT)  
            .setConnectionRequestTimeout(Constraints.TIME_OUT)  
            .build();  
      
    private static volatile HttpUtil instance = null; 
    
    private HttpUtil(){} 
    
    public static HttpUtil getInstance(){  
        if (Objects.isNull(instance)) {  
            synchronized (HttpUtil.class) {
				if(Objects.isNull(instance)) {
					instance = new HttpUtil();
				}
			}  
        }  
        return instance;  
    }  
	
	public String sendHttpGet(String url) {
		return sendHttpGet(new HttpGet(url));
	}

	private String sendHttpGet(HttpGet httpGet) {
		String responseContent = null;
		httpGet.setConfig(requestConfig);
		httpGet.setHeader("User-Agent", Constraints.USER_AGENT);
		httpGet.setHeader("Connection", "Keep-Alive");
		try {
			CloseableHttpClient httpClient = HttpClientPoolUtil.getInstance().getHttpClient();
			if(!Objects.isNull(httpClient)) {
				@Cleanup CloseableHttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				responseContent = EntityUtils.toString(entity, "UTF-8");
			}
		} catch(SocketTimeoutException e) {
			// 服务器请求超时
			System.err.append("服务器请求超时").println();
			return "";
		} catch(ConnectTimeoutException e) {
			// 服务器响应超时(已经请求了)
			System.err.append("服务器响应超时").println();
			return "";
		} catch(UnknownHostException e) {
			System.err.append("无网络连接或无法识别的主机").println();
			return "";
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
		return responseContent;
	}

}
