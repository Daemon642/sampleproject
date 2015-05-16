package nassoleng.oracle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

// Paul import
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
//

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ManageComputeUtil {
    public ManageComputeUtil() {
        super();
    }
    
    public static final Client getClient() throws NoSuchAlgorithmException, KeyManagementException {
        Client client = null;

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());

        HTTPSProperties prop = new HTTPSProperties(hv, sc);

        DefaultClientConfig dcc = new DefaultClientConfig();
        dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);

        client = Client.create(dcc);

        return client;
    }
    
    public static final CloseableHttpClient getClient (String username, String password, String domain, String zone) {
        CloseableHttpClient httpclient;

        String json =
            new String("{\n" + "    \"password\" : \"" + password + "\",\n" +
                       "    \"user\" : \"/Compute-" + domain + "/" + username + "\"\n" + "}");

        httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api-" + zone + ".compute.us2.oraclecloud.com/authenticate/");

        try {
            StringEntity entity = new StringEntity(json);
            httppost.setEntity(entity);
            httppost.setHeader("Content-Type", "application/oracle-compute-v3+json");
            httppost.setHeader("Accept", "application/oracle-compute-v3+json");
            CloseableHttpResponse response = httpclient.execute(httppost);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return httpclient;
    }
    
    public static final JSONObject getJSONObject (HttpEntity responseEntity) {
        JSONObject resultObj = null;
        String jsonString = "";
        
        try {
            if (responseEntity != null) {
                BufferedReader rd;
                    rd = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                System.out.println(result);
                jsonString = result.toString();
            }
    
            if (!jsonString.equals("")) {
                try {
                    resultObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                }
            }
        } catch (IOException e) {
        }
        return resultObj;
    }
}
