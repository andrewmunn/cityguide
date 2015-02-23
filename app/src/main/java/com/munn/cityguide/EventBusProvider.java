package com.munn.cityguide;

import com.squareup.otto.Bus;

/**
 * Provider of the Singleton event bus.
 */
public class EventBusProvider {

    private static Bus bus;

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus();
        }
        return bus;
    }
}
