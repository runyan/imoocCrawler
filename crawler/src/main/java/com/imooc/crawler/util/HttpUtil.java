package com.imooc.crawler.util;

import java.util.Objects;

import lombok.Cleanup;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
		try {
			@Cleanup CloseableHttpClient httpClient = HttpClients.createDefault();
			@Cleanup CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		} 
		return responseContent;
	}

}
