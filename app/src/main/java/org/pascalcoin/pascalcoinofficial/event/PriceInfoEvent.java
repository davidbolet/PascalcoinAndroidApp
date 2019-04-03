package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.PriceInfo;

/**
 * Created by davidbolet on 2/1/18.
 */

public class PriceInfoEvent implements Event {
    private final PriceInfo priceInfo;

    public PriceInfoEvent(PriceInfo priceInfo) { this.priceInfo=priceInfo; }

    public PriceInfo getPriceInfo() {
        return priceInfo;
    }
}
