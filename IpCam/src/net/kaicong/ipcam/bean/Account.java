package net.kaicong.ipcam.bean;

import org.json.JSONObject;

/**
 * Created by LingYan on 2014/9/1.
 */
public class Account extends ErrorResponse {

    public int userID;//用户id
    public String userEmail;//邮箱
    public String nickName;//昵称
    public String userKey;

    public static Account getAccountInfo(JSONObject itemObject) {
        Account account = new Account();
        try {
            account.userID = itemObject.optInt("id");
            account.userEmail = itemObject.optString("name");
            account.userKey = itemObject.optString("key");
            account.nickName = itemObject.optString("nickname");
        } catch (Exception e) {

        }
        return account;
    }

}
