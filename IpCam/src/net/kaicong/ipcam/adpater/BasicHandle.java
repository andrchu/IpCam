
/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.kaicong.ipcam.adpater;

/**
 * android_query.jar包里不包含该类
 * 为了添加BaseAuth验证，在这里添加上
 * base64验证用户名密码
 */

import java.net.HttpURLConnection;

import org.apache.http.HttpRequest;

import android.net.Uri;
import com.androidquery.auth.AccountHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class BasicHandle extends AccountHandle {

    private String username;
    private String password;

    public BasicHandle(String username, String password) {

        this.username = username;
        this.password = password;

    }

    @Override
    public boolean authenticated() {
        return true;
    }

    @Override
    protected void auth() {

    }

    @Override
    public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
        return false;
    }

    @Override
    public boolean reauth(AbstractAjaxCallback<?, ?> cb) {
        return false;
    }

    @Override
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request) {

        String cred = username + ":" + password;
        byte[] data = cred.getBytes();

        String auth = "Basic " + new String(AQUtility.encode64(data, 0, data.length));

        Uri uri = Uri.parse(cb.getUrl());

        String host = uri.getHost();
        request.addHeader("Host", host);
        request.addHeader("Authorization", auth);

    }

    @Override
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn) {

        String cred = username + ":" + password;
        byte[] data = cred.getBytes();

        String auth = "Basic " + new String(AQUtility.encode64(data, 0, data.length));

        Uri uri = Uri.parse(cb.getUrl());

        String host = uri.getHost();
        conn.setRequestProperty("Host", host);
        conn.setRequestProperty("Authorization", auth);

    }


}
