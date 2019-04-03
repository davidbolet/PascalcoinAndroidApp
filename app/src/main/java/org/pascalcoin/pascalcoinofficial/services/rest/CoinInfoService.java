package org.pascalcoin.pascalcoinofficial.services.rest;


import org.pascalcoin.pascalcoinofficial.model.PriceInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by davidbolet on 2/1/18.
 */

public interface CoinInfoService {

    @GET("info/price/{currencyId}")
    Call<PriceInfo>  getPriceInfo(@Path("currencyId") String currencyId, @Query("moneyCurrency") String moneyCurrency);

    @GET("info/price/{currencyId}/{period}")
    Call<List<PriceInfo>>  getPricesInfo(@Path("currencyId") String currencyId, @Path("period") String period, @Query("refCurrency") String refCurrency, @Query("start") Date start, @Query("end") Date end);

    @GET("info/price/pascPrice")
    Call<BigDecimal>  getPascPrice(@Query("currency") String currency);

}
