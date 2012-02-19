package cz.harvester.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Binder;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import cz.harvester.api.ApiParams.ApiGetAvailCardsParam;
import cz.harvester.api.ApiParams.ApiLoginParam;
import cz.harvester.api.ApiParams.ApiParam;
import cz.harvester.api.ApiParams.ApiRegisterParam;
import cz.harvester.api.ApiParams.ApiSendCardsParam;
import cz.harvester.api.ApiResults.ApiGetAvailCardsResult;
import cz.harvester.api.ApiResults.ApiLoginResult;
import cz.harvester.api.ApiResults.ApiRegisterResult;
import cz.harvester.api.ApiResults.ApiResult;
import cz.harvester.api.ApiResults.ApiSendCardsResult;

public class ApiFunc extends Binder {
	private static final String TAG = "harvester.ApiFunc"; 
	
	private static final ImmutableList<Integer> MILIS_BETWEEN_CONN_ATTEMPT = ImmutableList.of(0, 1000);
	private static final ImmutableList<Integer> MILIS_TIMOUT_CONN = ImmutableList.of(13000, 25000);
	private static final ImmutableList<Integer> MILIS_TIMOUT_SO = ImmutableList.of(15000, 30000);
	
	private final Context appContext;
	
	
	public ApiFunc(Context appContext) {
		this.appContext = appContext;
	}
	
	public ApiRegisterResult register(ApiRegisterParam param) {
		try {
			return new ApiRegisterResult(param, queryWcfService(param, 0));
		}
		catch (Exception ex) {
			Log.e(TAG, "login", ex);
			return new ApiRegisterResult(param, ApiResult.FATAL_ERROR, ex.getMessage());
		}
	}
	
	public ApiLoginResult login(ApiLoginParam param) {
		try {
			return new ApiLoginResult(param, queryWcfService(param, 0));
		}
		catch (Exception ex) {
			Log.e(TAG, "login", ex);
			return new ApiLoginResult(param, ApiResult.FATAL_ERROR, ex.getMessage());
		}
	}
	
	public ApiGetAvailCardsResult getAvailCards(ApiGetAvailCardsParam param) {
		try {
			return new ApiGetAvailCardsResult(param, queryWcfService(param, 0));
		}
		catch (Exception ex) {
			Log.e(TAG, "login", ex);
			return new ApiGetAvailCardsResult(param, ApiResult.FATAL_ERROR, ex.getMessage());
		}
	}
	
	public ApiSendCardsResult sendCards(ApiSendCardsParam param) {
		try {
			return new ApiSendCardsResult(param, queryWcfService(param, 0));
		}
		catch (Exception ex) {
			Log.e(TAG, "login", ex);
			return new ApiSendCardsResult(param, ApiResult.FATAL_ERROR, ex.getMessage());
		}
	}
	
	
	private JSONObject queryWcfService(ApiParam param, int attemptNumber) throws IOException, JSONException {
		Exception exception;
		boolean unkownHostEx = false;
		
		try {
			HttpRequestBase request = new HttpGet(param.getUri());
			request.setHeader("Content-Type", "application/json");
	        request.setHeader("Accept-encoding", "gzip, deflate");
	        
	        	        
            HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			HttpConnectionParams.setConnectionTimeout(httpParameters, MILIS_TIMOUT_CONN.get(attemptNumber));
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, MILIS_TIMOUT_SO.get(attemptNumber));
	        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	        
	        Log.i(TAG, "queryWcfService: httpClient.execute(request) conn timeout: " + MILIS_TIMOUT_CONN.get(attemptNumber) +
	        		" so timeout: " + MILIS_TIMOUT_SO.get(attemptNumber));
	        HttpResponse response = httpClient.execute(request);
	        
	        if (response == null) {
	        	Log.e(TAG, "queryWcfService: response == null");
	        	throw new IOException("Invalid response from server: response == null");
	        }
	        else {
	        	Log.i(TAG, "queryWcfService: response != null");
	        }
	        
	        StatusLine status = response.getStatusLine();
	        if (status.getStatusCode() != 200){
	            throw new IOException("Invalid response from server: " + status.toString());
	        }
	        
	        HttpEntity responseEntity = response.getEntity();
	        if (responseEntity == null) {
	        	Log.e(TAG, "Invalid response from server: responseEntity == null, " + status.toString());
	        	throw new IOException("Invalid response from server: responseEntity == null, " + status.toString()); 
	        }
			
            Header contentEncoding = responseEntity.getContentEncoding();
            if (contentEncoding != null && contentEncoding.getValue().contains("gzip")) {
            	responseEntity = new GzipEntityWrapper(responseEntity);
            }	        
	        return new JSONObject(new String(EntityUtils.toString(responseEntity, HTTP.UTF_8)));
		}
		catch (UnknownHostException ex) {
			exception = ex;
			Log.e(TAG, "queryWcfService - UnknownHostException", ex);
			unkownHostEx = true;
		}
		catch (JSONException ex) {
			exception = ex;
			Log.e(TAG, "queryWcfService - JSONException", ex);
		}
		catch (IOException ex) {
			exception = ex;
			Log.e(TAG, "queryWcfService - IOException", ex);
		}
		
		if (attemptNumber < MILIS_BETWEEN_CONN_ATTEMPT.size()) {
			try {
				Thread.sleep(unkownHostEx ? 15000 : MILIS_BETWEEN_CONN_ATTEMPT.get(attemptNumber));
			}
			catch (InterruptedException iex) { }
			return queryWcfService(param, ++attemptNumber);
		}
		else {
			Log.e(TAG, "queryWcfService - too many attempts failed (" + attemptNumber + ")");
			if (exception instanceof IOException)
				throw (IOException)exception;
			else
				throw (JSONException)exception;
		}
	}

	
	private final class GzipEntityWrapper extends HttpEntityWrapper {
	    public GzipEntityWrapper(final HttpEntity entity) {
	        super(entity);
	    }

	    public InputStream getContent()
	        throws IOException, IllegalStateException {
	        return new GZIPInputStream(wrappedEntity.getContent());
	    }

	    public long getContentLength() {
	        return -1;
	    }
	}
}
	

