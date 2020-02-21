package it.zerozero.bclock;

import android.hardware.Sensor;

class AppSynchronizer {

    private static final AppSynchronizer uniqueInstance = new AppSynchronizer();
    private Sensor sensor;

    static AppSynchronizer getInstance() {
        return uniqueInstance;
    }

    private AppSynchronizer() { /* empty constructor */}

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
