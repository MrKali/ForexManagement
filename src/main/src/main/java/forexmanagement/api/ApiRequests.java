package forexmanagement.api;

import forexmanagement.Config;
import forexmanagement.Strategies;
import forexmanagement.models.CurrenciesListModel;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiRequests {

    static public String period1Hour = "1h";

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

    public static ArrayList<Float> getLast300Prices(String symbol, String period){
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("fcsapi.com")
                .addPathSegment("api")
                .addPathSegment("forex")
                .addPathSegment("history")
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("period", period)
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
                    ArrayList<Float> lastPrices = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject currency = new JSONObject(jsonArray.optString(i));

                        Float price = Float.valueOf(currency.getString("c"));

                        lastPrices.add(price);

                        if (i == jsonArray.length() - 1){
                            return lastPrices;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Strategies.Strategy1.Model getMa(String symbol){
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("fcsapi.com")
                .addPathSegment("api")
                .addPathSegment("forex")
                .addPathSegment("ma_avg")
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("period", "1h")
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
                    String SMA50 = jsonObject.optJSONObject("response")
                            .optJSONObject("ma_avg")
                            .optJSONObject("SMA")
                            .optJSONObject("MA50")
                            .optString("v");

                    String SMA100 = jsonObject.optJSONObject("response")
                            .optJSONObject("ma_avg")
                            .optJSONObject("SMA")
                            .optJSONObject("MA100")
                            .optString("v");

                    String SMA200 = jsonObject.optJSONObject("response")
                            .optJSONObject("ma_avg")
                            .optJSONObject("SMA")
                            .optJSONObject("MA200")
                            .optString("v");

                    String EMA10 = jsonObject.optJSONObject("response")
                            .optJSONObject("ma_avg")
                            .optJSONObject("EMA")
                            .optJSONObject("MA10")
                            .optString("v");

                    float SMA150 = (Float.parseFloat(SMA200) + Float.parseFloat(SMA100))/2;

                    return new Strategies.Strategy1.Model(Float.parseFloat(SMA50), SMA150, Float.parseFloat(EMA10));
                }else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
