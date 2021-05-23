package com.example.demo.configuration;

import com.example.demo.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * 获取Rest请求的对象
 * <pre>
 * FileName:RestConfiguration.java
 * Copyright:
 * Company	TimaNetWork LTD.
 * @author:	LMQ
 * @version	V1.0
 * @createDate:	2017年12月18日 下午5:59:20
 */
@Configuration
public class RestConfiguration {
	@Bean
	@ConditionalOnMissingBean({ RestOperations.class, RestTemplate.class })
	public RestOperations restOperations() {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientUtils.acceptsUntrustedCertsHttpClient();
		} catch (Exception e) {
		}
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		// 使用 utf-8 编码集的 conver 替换默认的 conver（默认的 string conver 的编码集为
		// "ISO-8859-1"）
		List<HttpMessageConverter<?>> messageConverters = restTemplate
				.getMessageConverters();
		Iterator<HttpMessageConverter<?>> iterator = messageConverters
				.iterator();
		while (iterator.hasNext()) {
			HttpMessageConverter<?> converter = iterator.next();
			if (converter instanceof StringHttpMessageConverter) {
				iterator.remove();
			}
		}
		messageConverters.add(new StringHttpMessageConverter(Charset
				.forName("UTF-8")));
		return restTemplate;
	}
}
