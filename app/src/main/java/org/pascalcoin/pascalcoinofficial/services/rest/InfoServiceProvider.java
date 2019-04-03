package org.pascalcoin.pascalcoinofficial.services.rest;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.pascalcoin.pascalcoinofficial.event.PascPriceEvent;
import org.pascalcoin.pascalcoinofficial.event.PriceInfoEvent;
import org.pascalcoin.pascalcoinofficial.model.PriceInfo;

import java.math.BigDecimal;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by davidbolet on 2/1/18.
 */

public class InfoServiceProvider {


    public static final String BASE_URL = "https://pascalcoin.app/manager/";
    private final static String TAG = InfoServiceProvider.class.getSimpleName();
    private CoinInfoService coinInfoService;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit retrofit;
    public static final String API_BASE_URL = BASE_URL+ "api/rest/";

    private static InfoServiceProvider _instance;
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create());

    public static InfoServiceProvider getInstance() {
        if (_instance==null)
            _instance=new InfoServiceProvider();
        return _instance;
    }

    private InfoServiceProvider()
    {
        builder.baseUrl(API_BASE_URL).client(httpClient.build());
        retrofit = builder.build();
        this.coinInfoService =retrofit.create(CoinInfoService.class);
    }

    public void getPrice(final String currencyId, final String moneyCurrency) {

        final Call<PriceInfo> infoData = coinInfoService.getPriceInfo(currencyId, moneyCurrency);

        infoData.enqueue(new Callback<PriceInfo>() {
            @Override
            public void onResponse(Call<PriceInfo> call, Response<PriceInfo> response) {
                PriceInfo price = response.body();
                if (price!=null) {
                    Log.e(TAG, "Price for  = " + currencyId+ " is " + price.getPriceAsk());
                    EventBus.getDefault().post(new PriceInfoEvent(price));
                }
                else
                {
                    Log.e(TAG,"onFailure: Unable to get price info ");
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get price info"));
                }

            }

            @Override
            public void onFailure(Call<PriceInfo> call, Throwable t) {
                Log.e(TAG,"onFailure: Unable to get price info "+t.getMessage());
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get price info "));
            }
        });

    }

    public void getPascPrice(final String currency) {
        final Call<BigDecimal>  pascPrice = coinInfoService.getPascPrice(currency);

        pascPrice.enqueue(new Callback<BigDecimal>() {
            @Override
            public void onResponse(Call<BigDecimal> call, Response<BigDecimal> response) {
                BigDecimal pascPrice = response.body();
                if (pascPrice!=null) {
                    Log.e(TAG, String.format("Pasc price in %s id %.4f ",currency,pascPrice.doubleValue()));
                    EventBus.getDefault().post(new PascPriceEvent(pascPrice, currency));
                }
                else
                {
                    Log.e(TAG,"onFailure: Unable to get PASC price ");
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get PASC price", response.code()));
                }

            }

            @Override
            public void onFailure(Call<BigDecimal> call, Throwable t) {
                Log.e(TAG,"onFailure: Unable to get PASC price "+t.getMessage());
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get PASC price"));
            }
        });
    }

}
