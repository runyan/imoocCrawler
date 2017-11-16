package com.imooc.crawler.util;

import java.io.IOException;

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
        if (null == instance) {  
            synchronized (HttpUtil.class) {
				if(null == instance) {
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
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;
		try {
			httpClient = HttpClients.createDefault();
			httpGet.setConfig(requestConfig);
			httpGet.setHeader("User-Agent", Constraints.USER_AGENT);
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != response) {
					response.close();
				}
				if(null != httpClient) {
					httpClient.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

}
