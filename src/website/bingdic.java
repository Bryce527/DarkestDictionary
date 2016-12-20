package website;

import java.io.*;
import java.net.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bingdic {
	public static void main(String[] args) {
		Scanner input =new Scanner(System.in);
		String word = input.nextLine();
		String requestUrl = "http://cn.bing.com/dict/search?q=" + word + "&go=%E6%90%9C%E7%B4%A2&qs=bs&form=Z9LH5";		
		String web_data = httpRequest(requestUrl);
		String meaning = MatchMeaning(web_data);
	}
	
	public String lookup(String word) {
		String requestUrl = "http://cn.bing.com/dict/search?q=" + word + "&go=%E6%90%9C%E7%B4%A2&qs=bs&form=Z9LH5";
		String web_data = httpRequest(requestUrl);
		String meaning = MatchMeaning(web_data);
		return meaning;
	}

	private static String httpRequest(String requestUrl) {
        StringBuffer buffer = null;  
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;
  
        try {  
            // 建立get请求
            URL url = new URL(requestUrl);  
            httpUrlConn = (HttpURLConnection) url.openConnection();  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setRequestMethod("GET");  
  
            // 获取输入流  
            inputStream = httpUrlConn.getInputStream();  
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            bufferedReader = new BufferedReader(inputStreamReader);  
  
            // 从输入流读取结果
            buffer = new StringBuffer();  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  finally {
            // 释放资源
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(httpUrlConn != null){
                httpUrlConn.disconnect();  
            }
        }
        return buffer.toString();  
	}
	
	private static String MatchMeaning(String web_data) {
		Pattern searchMeanPattern = Pattern.compile("(?m)content=\"必应词典为您提供(.*?)\" /><");
        Matcher m1 = searchMeanPattern.matcher(web_data); //m1是获取包含翻译的整个<div>的
        StringBuilder result = new StringBuilder();
        try {
            if (m1.find()) {
                String means = m1.group();//所有解释，包含网页标签
                Pattern getChinese = Pattern.compile("，(.*)；"); //(?m)代表按行匹配
                Matcher m2 = getChinese.matcher(means);
                while (m2.find()) {
                    result.append(m2.group(1)); 
                }
                return result.toString();
            } else {
                return null;
            }
        }
        catch(NullPointerException ex) {
        	System.out.println(ex);
        }
		return result.toString();
	}
}
