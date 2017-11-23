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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient工具类
 * @author yanrun
 *
 */
public class HttpUtil {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private RequestConfig requestConfig = RequestConfig.custom()  
            .setSocketTimeout(Constraints.TIME_OUT)  
            .setConnectTimeout(Constraints.TIME_OUT)  
            .setConnectionRequestTimeout(Constraints.TIME_OUT)  
            .build();  
      
    private HttpUtil(){} 
    
    public static HttpUtil getInstance(){  
        return InstanceHolder.INSTANCE.getUtil();
    }  
    
    /**
     * 实现单例用的枚举
     * @author yanrun
     *
     */
    private enum InstanceHolder {
    	INSTANCE;
    	private HttpUtil util;
    	
    	private InstanceHolder() {
    		util = new HttpUtil();
    	}
    	
    	private HttpUtil getUtil() {
    		return util;
    	}
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
			LOGGER.error("服务器请求超时");
			return "";
		} catch(ConnectTimeoutException e) {
			// 服务器响应超时(已经请求了)
			LOGGER.error("服务器响应超时");
			return "";
		} catch(UnknownHostException e) {
			LOGGER.error("无网络连接或无法识别的主机");
			return "";
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return "";
		}
		return responseContent;
	}

}
