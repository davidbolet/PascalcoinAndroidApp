package org.pascalcoin.pascalcoinofficial.event;

import java.math.BigDecimal;

public class PascPriceEvent implements Event {
    final BigDecimal price;
    final String currency;

    public PascPriceEvent(BigDecimal price, String currency) {
        this.price =price;
        this.currency=currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
