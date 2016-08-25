package com.bugsnag;

import org.junit.Test;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.delivery.OutputStreamDelivery;

public class ClientTest {
    @Test
    public void testSerialization() {
        Client client = new Client("3fd63394a0ec74ac916fbdf3110ed957");
        // client.setEndpoint("http://localhost:8000");
        // client.setDelivery(new HttpDelivery());
        client.setDelivery(new OutputStreamDelivery(System.out));

        client.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report
                    .addToTab("Account", "Name", "Acme Co")
                    .addToTab("Account", "password", "s3cr3t");
            }
        });

        client.notify(new RuntimeException("oops"), Severity.INFO);
    }
}
