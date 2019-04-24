package net.kaicong.ipcam.api;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by LingYan on 15/3/23.
 */
public interface IVolleyResponseListener {

    public void onStart();

    public void onSuccess(JSONObject result);

    public void onSuccess(String result);

    public void onError(VolleyError error);

}
