package org.camunda.bpm.getstarted.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class WetterAnzeigenDelegate implements JavaDelegate {
	
	public void execute(DelegateExecution arg0) throws Exception {
	
		
		for(Map.Entry<String, Object> entry : arg0.getVariables().entrySet()) {
			System.out.println(entry.getKey()+" -> "+entry.getValue());
		}
		
		System.out.println();
		
		String city = String.valueOf(arg0.getVariable("city"));
		System.out.println(city);
		
		String requestURL ="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+city+"%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
		String result = getStringFromUrl(requestURL);
		
		int index = result.indexOf("forecast");
		index = result.indexOf("text", index + 1);
		//index = result.indexOf("hight", index + 1);
		//index = result.indexOf("low", index + 1);
		index = result.indexOf(":", index + 1);
		
		int idx1 = result.indexOf("\"",index+1);
		int idx2 = result.indexOf("\"",idx1+1);
		//int idx3 = result.indexOf("\"",idx2+1);
		//int idx4 = result.indexOf("\"",idx3+1);
		String forecast = result.substring(idx1+1,idx2); //idx3,idx4);
		System.out.println(forecast);
		arg0.setVariable("result", forecast);
	}
	
	private String getStringFromUrl(String urlToReadFrom) throws Exception {
		
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
       
        final SSLContext sc = SSLContext.getInstance("SSL"); 
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        
        URL url = new URL(urlToReadFrom);
    
        URLConnection con = url.openConnection();
        final Reader reader = new InputStreamReader(con.getInputStream());
        final BufferedReader br = new BufferedReader(reader);        
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {

        	sb.append(line);
        }        
        br.close();
        return sb.toString();
	}
}
