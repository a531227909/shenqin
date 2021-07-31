package com.labour.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

// 利用HttpClient进行post请求的工具类
public class HttpClientUtil {
	
	
	
	private static HttpClientUtil instance = null;
	
	private HttpClientUtil(){
		
	}
	public static synchronized HttpClientUtil getInstance(){
			if(instance==null){
				
				instance = new HttpClientUtil();
			}
			return instance;
	}
	
	public String doPost(String url,String charset){
		return doPost(url,null,charset,null);
	}
	
	public String doPost(String url,Map<String,String> map,String charset){
		return doPost(url, map, charset, null);
	}
	public String doPost(String url,Map<String,String> map,String charset,Map<String, String> headers){
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try{
			httpClient = new SSLClient();
			
			httpPost = new HttpPost(url);
			if(headers!=null){
				Iterator<String> it = headers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					httpPost.setHeader(key, headers.get(key));
				}
			}
			//设置参数
			if(map!=null){
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				Iterator iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String,String> elem = (Entry<String, String>) iterator.next();
					list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
				}
				if(list.size() > 0){
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
					httpPost.setEntity(entity);
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			if(response != null){
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null){
					result = EntityUtils.toString(resEntity,charset);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
			if(httpClient!=null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return result;
	}

	public String doPostbySSLv12(String url,Map<String,String> map,String charset,Map<String, String> headers){
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try{
			httpClient = new SSLv12Client();

			httpPost = new HttpPost(url);
			if(headers!=null){
				Iterator<String> it = headers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					httpPost.setHeader(key, headers.get(key));
				}
			}
			//设置参数
			if(map!=null){
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				Iterator iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String,String> elem = (Entry<String, String>) iterator.next();
					list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
				}
				if(list.size() > 0){
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
					httpPost.setEntity(entity);
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			if(response != null){
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null){
					result = EntityUtils.toString(resEntity,charset);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
			if(httpClient!=null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return result;
	}
	public String doPostByJson(String url,Map<String,String> map,String charset,Map<String, String> headers){
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try{
			httpClient = new SSLClient();
			
			httpPost = new HttpPost(url);
			if(headers!=null){
				Iterator<String> it = headers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					httpPost.setHeader(key, headers.get(key));
				}
			}
			//设置参数
			if(map!=null){
				JSONObject param = new JSONObject();
				Iterator iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String,String> elem = (Entry<String, String>) iterator.next();
					//list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
					param.put(elem.getKey(), elem.getValue());
				}
				if(param.size() > 0){
					 StringEntity se = new StringEntity(param.toString());
					httpPost.setEntity(se);
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			if(response != null){
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null){
					result = EntityUtils.toString(resEntity,charset);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
			if(httpClient!=null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return result;
	}
	
	
	
	/**
	 * JSON数据体POST请求
	 * @param url POST请求地址
	 * @param json json数据提
	 * @param charset 编码格式
	 * @return JSON数据体对象
	 */
	public  JSONObject doPost(String url,String json,String charset){
		 CloseableHttpClient httpclient = HttpClients.createDefault();
         HttpPost post = new HttpPost(url.trim());
         JSONObject response = null;
         try {
             StringEntity s = new StringEntity(json);
             s.setContentEncoding(charset);
             s.setContentType("application/json");//发送json数据需要设置contentType
             post.setEntity(s);
             HttpResponse res = httpclient.execute(post);
             if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                 HttpEntity entity = res.getEntity();
                 String result = EntityUtils.toString(entity);// 返回json格式：
                 response = JSONObject.fromObject(result);
             }
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
         return response;
     }

	public  JSONObject doSSLPostByJson(String url,String json,String charset){
		HttpClient httpclient = null;
		HttpPost post = null;
		JSONObject response = null;

		try {
			httpclient = new SSLClient();
			post = new HttpPost(url);

			StringEntity s = new StringEntity(json);
			s.setContentEncoding(charset);
			s.setContentType("application/json");//发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(entity);// 返回json格式：
				response = JSONObject.fromObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			if(post!=null){
				post.releaseConnection();
			}
			if(httpclient!=null){
				httpclient.getConnectionManager().shutdown();
			}
		}
		return response;
	}

	public  JSONObject doSSLv12PostByJson(String url,String json,String charset){
		HttpClient httpclient = null;
		HttpPost post = null;
		JSONObject response = null;

		try {
			httpclient = new SSLv12Client();
			post = new HttpPost(url);

			StringEntity s = new StringEntity(json);
			s.setContentEncoding(charset);
			s.setContentType("application/json");//发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(entity);// 返回json格式：
				response = JSONObject.fromObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			if(post!=null){
				post.releaseConnection();
			}
			if(httpclient!=null){
				httpclient.getConnectionManager().shutdown();
			}
		}
		return response;
	}

	/**
	 * JSON数据体POST请求
	 * @param url POST请求地址
	 * @param json json数据提
	 * @param charset 编码格式
	 * @return JSON数据体对象
	 */
	public  String doJsonPost(String url,String json,String charset){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url.trim());
		String result = null;
		try {
			StringEntity s = new StringEntity(json);
			s.setContentEncoding(charset);
			s.setContentType("application/json");//发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = res.getEntity();
				result = EntityUtils.toString(entity);// 返回json格式：
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public String doGet(String url,String charset){
		HttpClient httpClient = null;
		HttpGet httpPost = null;
		String result = null;
		try{
			httpClient = new SSLClient();
			httpPost = new HttpGet(url);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			HttpResponse response = httpClient.execute(httpPost);
			if(response != null){
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null){
					result = EntityUtils.toString(resEntity,charset);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(httpPost!=null){
				httpPost.releaseConnection();
			}
			if(httpClient!=null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		 HttpClientUtil.getInstance().doPost("http://pay.ruifuzhifu.com/api.php/Rpay/get_qr", "", "UTF-8");
	}
}
