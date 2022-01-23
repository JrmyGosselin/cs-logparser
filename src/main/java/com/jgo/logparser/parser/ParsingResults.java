package com.jgo.logparser.parser;

import com.jgo.logparser.model.Event;

import java.math.BigInteger;

// This object is not really necessary to the evaluation in itself, I just use it for testing purposes
public class ParsingResults {
    BigInteger total = BigInteger.ZERO;
    BigInteger alerts = BigInteger.ZERO;

    public void addEvent(Event event) {
        total = total.add(BigInteger.ONE);
        if(event.isAlert()) {
            alerts = alerts.add(BigInteger.ONE);
        }
    }

    public BigInteger getTotal() {
        return total;
    }

    public BigInteger getAlerts() {
        return alerts;
    }
}
