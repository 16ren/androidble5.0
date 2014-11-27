package com.example.androidbld;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends Activity {
	// B4:99:4C:23:D4:F4
	private BluetoothLeScanner bluetoothLeScanner;
	private MyScanCallback myScanCallback;
	private static final String ADDRESS = "B4:99:4C:23:D4:F4"; // 所扫描的ibeacon的物理地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
		myScanCallback = new MyScanCallback();
		bluetoothLeScanner.startScan(myScanCallback);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bluetoothLeScanner.stopScan(myScanCallback);
	}

	class MyScanCallback extends ScanCallback {

		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			super.onScanResult(callbackType, result);

			// B4:99:4C:23:D4:F4
			// d26d197e-4a1c-44ae-b504-dd7768870564

			String address = result.getDevice().getAddress();
			if (address.equals(MainActivity.ADDRESS)) {
				String uuid = getUuid(result);
				int major = getMajor(result);
				int minor = getMinor(result);
				int power = getPower(result);

				Log.d("MyScanCallback", "uuid:" + uuid.toString());
				Log.d("MyScanCallback", "major:" + major);
				Log.d("MyScanCallback", "minor:" + minor);
				Log.d("MyScanCallback", "power:" + power);
			}
		}

		@Override
		public void onBatchScanResults(List<ScanResult> results) {
			// TODO Auto-generated method stub
			// super.onBatchScanResults(results);
			Log.d("MyScanCallback", "onBatchScanResults");
		}

		@Override
		public void onScanFailed(int errorCode) {
			// TODO Auto-generated method stub
			// super.onScanFailed(errorCode);
			Log.d("MyScanCallback", "onScanFailed");
		}

	}

	private String getUuid(ScanResult result) {
		if (result == null)
			return null;

		byte[] data = result.getScanRecord().getManufacturerSpecificData().get(76);

		int[] uuid = new int[16];
		for (int i = 0; i < 16; i++) {
			uuid[i] = data[i + 2];
			if (uuid[i] < 0)
				uuid[i] += 256;
		}
		// d26d197e-4a1c-44ae-b504-dd7768870564
		String address = new String();
		for (int i = 0; i < uuid.length; i++) {
			if (i == 4 || i == 6 || i == 8 || i == 10) {
				address = address + "-";
			}
			if (uuid[i] < 16) {
				address = address + "0" + Integer.toHexString(uuid[i]);
			} else {
				address = address + Integer.toHexString(uuid[i]);
			}
		}
		return address;
	}

	private int getPower(ScanResult result) {
		if (result == null)
			return -1;
		byte[] data = result.getScanRecord().getManufacturerSpecificData().get(76);
		int power = data[22];
		if (power < 0)
			power += 256;
		return power;
	}

	private int getMajor(ScanResult result) {
		if (result == null)
			return -1;
		byte[] data = result.getScanRecord().getManufacturerSpecificData().get(76);
		byte[] major = new byte[4];
		major[2] = data[18];
		major[3] = data[19];
		int k = byteArrayToInt(major);
		return k;
	}

	private int getMinor(ScanResult result) {
		byte[] data = result.getScanRecord().getManufacturerSpecificData().get(76);
		byte[] minor = new byte[4];
		minor[2] = data[20];
		minor[3] = data[21];
		int k = byteArrayToInt(minor);
		return k;
	}

	private byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		// 由高位到低位
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	private int byteArrayToInt(byte[] bytes) {
		int value = 0;
		// 由高位到低位
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}
}
