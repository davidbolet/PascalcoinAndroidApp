package org.pascalcoin.pascalcoinofficial.model;

/**
 * Created by davidbolet on 2/1/18.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfo {


    @JsonProperty("pair")
    String pair;

    @JsonProperty("price_ask")
    BigDecimal priceAsk;

    @JsonProperty("price_bid")
    BigDecimal priceBid;

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public BigDecimal getPriceAsk() {
        return priceAsk;
    }

    public void setPriceAsk(BigDecimal priceAsk) {
        this.priceAsk = priceAsk;
    }

    public BigDecimal getPriceBid() {
        return priceBid;
    }
    public void setPriceBid(BigDecimal priceBid) {
        this.priceBid = priceBid;
    }

}
