package com.example.johrei.bulkupdatertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.estimote.coresdk.common.exception.EstimoteException;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.recognition.packets.ConfigurableDevice;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.mgmtsdk.feature.bulk_updater.BulkUpdater;
import com.estimote.mgmtsdk.feature.bulk_updater.BulkUpdaterBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BulkUpdater bulkUpdater;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bulkUpdater = new BulkUpdaterBuilder(this)
                .withCloudFetchInterval(5, TimeUnit.SECONDS)
                .withRetryCount(3)
                .withTimeout(0)
                .build();
        beaconManager = new BeaconManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            bulkUpdater.start(new BulkUpdater.BulkUpdaterCallback() {
                @Override
                public void onDeviceStatusChange(ConfigurableDevice configurableDevice, BulkUpdater.Status status, String s) {
                    Log.d(TAG, configurableDevice.deviceId + ": " + status);
                }

                @Override
                public void onFinished() {
                    Log.d(TAG, "Finished.");
                }

                @Override
                public void onError(EstimoteException e) {
                    Log.e(TAG, "BulkUpdater error", e);
                }
            });
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.setConfigurableDevicesListener(new BeaconManager.ConfigurableDevicesListener() {
                        @Override
                        public void onConfigurableDevicesFound(List<ConfigurableDevice> list) {
                            Log.d(TAG, "Found " + list.size() + " devices");
                            bulkUpdater.onDevicesFound(list);
                        }
                    });
                    beaconManager.startConfigurableDevicesDiscovery();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bulkUpdater.destroy();
        beaconManager.stopConfigurableDevicesDiscovery();
    }
}
