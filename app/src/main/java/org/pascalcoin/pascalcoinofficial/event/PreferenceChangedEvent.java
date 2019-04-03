package org.pascalcoin.pascalcoinofficial.event;


import android.support.v7.preference.Preference;

public class PreferenceChangedEvent implements Event {
    private final Preference updatedPreference;
    private final String value;

    public PreferenceChangedEvent(Preference updatedPreference, String value) {
        this.updatedPreference=updatedPreference;
        this.value=value;
    }

    public Preference getUpdatedPreference() {
        return this.updatedPreference;
    }

    public String getValue() {
        return value;
    }
}
