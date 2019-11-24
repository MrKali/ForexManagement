package com.varela.forexmanagement.api;

import com.varela.forexmanagement.Config;
import com.varela.forexmanagement.api.models.CurrenciesListModel;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiRequests {
    public static ArrayList<CurrenciesListModel> getListOfCurrencies(){
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("fcsapi.com")
                .addPathSegment("api")
                .addPathSegment("forex")
                .addPathSegment("list")
                .addQueryParameter("type", "forex")
                .addQueryParameter("access_key", Config.apiKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONObject jsonObject = null;
            if (response.body() != null) {
                jsonObject = new JSONObject(response.body().string());
            }

            if (jsonObject != null) {
                if (jsonObject.optBoolean("status")){
                    JSONArray jsonArray = new JSONArray(jsonObject.optString("response"));
                    ArrayList<CurrenciesListModel> currenciesListModels = new ArrayList<CurrenciesListModel>();

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject currency = new JSONObject(jsonArray.optString(i));

                        CurrenciesListModel c = new CurrenciesListModel(
                                currency.optString("id"),
                                currency.optString("name"),
                                currency.optString("decimal"),
                                currency.optString("symbol"));

                        currenciesListModels.add(c);

                        if (i == jsonArray.length() - 1){
                            return currenciesListModels;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
