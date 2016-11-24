package website;

import java.io.*;
import java.net.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class youdaodic {
	public static void main(String[] args) {
		Scanner input =new Scanner(System.in);
		System.out.println("Enter the word");
		String word = input.nextLine();
		String requestUrl = "http://dict.youdao.com/w/eng/" + word + "/#keyfrom=dict2.index.suggest";
		String web_data = httpRequest(requestUrl);
		String meaning = MatchMeaning(web_data);
		System.out.println(meaning);

	}
	
	public String lookup(String word) {
		System.out.println("Enter the word");
		String requestUrl = "http://dict.youdao.com/w/eng/" + word + "/#keyfrom=dict2.index.suggest";
		String web_data = httpRequest(requestUrl);
		String meaning = MatchMeaning(web_data);
		System.out.println(meaning);
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
		Pattern searchMeanPattern = Pattern.compile("(?s)<div class=\"trans-container\">.*?<ul>.*?</div>");
        Matcher m1 = searchMeanPattern.matcher(web_data); //m1是获取包含翻译的整个<div>的
        StringBuilder result = new StringBuilder();
        try {
            if (m1.find()) {
                String means = m1.group();//所有解释，包含网页标签
                Pattern getChinese = Pattern.compile("(?m)<li>(.*?)</li>"); //(?m)代表按行匹配
                Matcher m2 = getChinese.matcher(means);

                System.out.println("释义:");
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

