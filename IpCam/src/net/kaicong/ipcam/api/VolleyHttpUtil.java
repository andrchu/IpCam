package net.kaicong.ipcam.api;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.kaicong.ipcam.KCApplication;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LingYan on 15/3/23.
 */
public class VolleyHttpUtil {

	private IVolleyResponseListener iVolleyResponseListener;
	private static final int MY_SOCKET_TIMEOUT_MS = 60000;//超时60s

	public void doJsonObjectRequest(String url,
			final Map<String, String> mapParams, VolleyResponse volleyResponse) {
		iVolleyResponseListener = volleyResponse;
		if (iVolleyResponseListener != null) {
			iVolleyResponseListener.onStart();
		}
		Request<JSONObject> jsonObjectRequest = new NormalPostRequest(url,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject jsonObject) {
						if (iVolleyResponseListener != null) {
							iVolleyResponseListener.onSuccess(jsonObject);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						if (iVolleyResponseListener != null) {
							iVolleyResponseListener.onError(volleyError);
						}
					}
				}, mapParams);

		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
				MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		KCApplication.getInstance().addToRequestQueue(jsonObjectRequest);
	}

}
