package jp.co.webfrontier.breakout;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BLE(BlueNinja)接続制御クラス
 */
public class BlueNinjaController {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BleActivity";

    /**
     * BlueNinja接続先デバイス名
     */
    private static final String DEFAULT_DEVICE_NAME = "SINOBI";

    /**
     * UUID
     */
    // characteristics設定
    public static final String UUID_CLIENT_CHARACTERISTIC_CONFIG
                                                         = "00002902-0000-1000-8000-00805f9b34fb";
    // 9Axis Sensor: GYRO（ジャイロ）
    public static final String UUID_CHARACTERISTIC_GYRO = "d43a0201-0e5f-4a80-9182-5f82ff67e8f8";
    // 9Axis Sensor: ACCEL（加速度センサ）
    public static final String UUID_CHARACTERISTIC_ACCEL = "d43a0202-0e5f-4a80-9182-5f82ff67e8f8";
    // 9Axis Sensor: Magnetometer（地磁気センサ）
    public static final String UUID_CHARACTERISTIC_MAGM  = "d43a0203-0e5f-4a80-9182-5f82ff67e8f8";
    // Axis angle
    public static final String UUID_CHARACTERISTIC_ANGLE = "d43a0204-0e5f-4a80-9182-5f82ff67e8f8";
    // Temperature（気温）
    public static final String UUID_CHARACTERISTIC_TEMP  = "d43a0211-0e5f-4a80-9182-5f82ff67e8f8";
    // Airpressure（気圧センサー）
    public static final String UUID_CHARACTERISTIC_AIRP  = "d43a0212-0e5f-4a80-9182-5f82ff67e8f8";

    // BLE検索時間(10s)
    private static final long SCAN_PERIOD = 10000;

    // メッセージ種別
    private enum MsgType {
        BLE_STATUS_CHANGE,  // BLE状態変化
        MESSAGE_SEND,       // メッセージ送信
        INVALID;

        public static MsgType cast(int value) {
            MsgType[] enumArray = MsgType.values();
            for(MsgType type : enumArray) {
                if(type.ordinal() == value) {
                    return type;
                }
            }

            // 範囲外の値
            Log.e(TAG, "cast(INVALID)");
            return INVALID;
        }
    }

    /**
     * BLE接続状態
     */
    private enum BleStatus {
        DISCONNECTED,       // 切断
        SCANNING,           // 検索中
        SCAN_FAILED,        // 検索失敗
        DEVICE_FOUND,       // デバイス検出
        CONNECTING,         // 接続中
        CONNECTED,          // 接続済み
        SERVICE_NOT_FOUND,  // サービスなし
        SERVICE_FOUND,      // サービス検出
        INVALID;

        public static BleStatus cast(int value) {
            BleStatus[] enumArray = BleStatus.values();
            for(BleStatus status : enumArray) {
                if(status.ordinal() == value) {
                    return status;
                }
            }

            // 範囲外の値
            Log.e(TAG, "cast(INVALID)");
            return INVALID;
        }
    }
    /**
     * BLE接続状態
     */
    private BleStatus mStatus = BleStatus.DISCONNECTED;

    /**
     * メインアクティビティ
     */
    private MainActivity mMainActivity;
    /**
     * BLE関連
     */
    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothGatt mBluetoothGatt = null;

    /**
     * ブロック崩しアクティビティインスタンス登録
     *
     * @param activity ブロック崩しアクティビティインスタンス
     */
    public BlueNinjaController(MainActivity activity)
    {
        mMainActivity = activity;
    }

    /**
     * 初期化処理
     */
    public void init() {

        // 初期化
        mStatus = BleStatus.DISCONNECTED;
    }

    /**
     * 接続デバイス名入力ダイアログ表示
     */
    public void showDialog() {

        // カスタムビューを設定
        LayoutInflater inflater = (LayoutInflater) mMainActivity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.ble_dialog,
                (ViewGroup) mMainActivity.findViewById(R.id.ble_dialog));

        // アラートダイアログ を生成
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle("BLE Connect");
        ((EditText) layout.findViewById(R.id.bleDlg_name)).setText(DEFAULT_DEVICE_NAME);
        builder.setView(layout);
        builder.setPositiveButton("Connect", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Connect ボタンクリック処理
                EditText nameText
                        = (EditText) layout.findViewById(R.id.bleDlg_name);
                String name = nameText.getText().toString();

                connectBle(name);
            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理
                stopScan();
            }
        });

        // 表示
        builder.create().show();
    }

    /**
     * BLE接続判定
     *
     * @retval true  接続中
     * @retval false 非接続
     */
    public boolean isConnected() {
        return (mStatus == BleStatus.CONNECTED);
    }

    /**
     * BLE機器検索判定
     *
     * @retval true  検索中
     * @retval false 未検索
     */
    public boolean isScanning() {
        return (mStatus == BleStatus.SCANNING);
    }

    /**
     * BLE機器接続（デフォルト機器）
     */
    public void connectBle() {
        connectBle(DEFAULT_DEVICE_NAME);
    }

    /**
     * BLE機器接続（接続機器名指定）
     *
     * @param deviceName 接続機器名
     */
    public void connectBle(String deviceName) {
        Log.d(TAG, "connectBle(" + deviceName + ")");

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mMainActivity.getSystemService(Context.BLUETOOTH_SERVICE);

        // BLEサポートバージョン判定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            // 機器検索
            scanLeDevice(true, deviceName);
        } else {
            Log.w(TAG, "Unsupported API : getAdapter() [" + Build.VERSION.SDK_INT + "]");
        }
    }

    /**
     * BLE機器切断
     */
    public void disconnectBle() {
        Log.d(TAG, "disconnectBle()");

        if(mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    /**
     * 機器検索ハンドラ
     */
    private Handler mScanHandler = new Handler();

    /**
     * Device検索
     *
     * @param enable 検索有無
     * @param deviceName 検索機器名
     */
    private void scanLeDevice(final boolean enable, final String deviceName) {
        Log.d(TAG, "scanLeDevice(enable: " + enable + ", deviceName: " + deviceName + ")");

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_PERIOD);

            // 機器検索
            startScan(deviceName);
        } else {
            // 検索停止
            stopScan();
        }
    }

    /**
     * 機器検索
     *
     * @param deviceName 検索機器名
     */
    private void startScan(String deviceName) {
        Log.d(TAG, "startScan(" + deviceName + ")");

        if (deviceName == null) {
            // 全BLE機器検索
            mBluetoothLeScanner.startScan(mScanCallback);
        } else {
            // BLE機器名指定検索
            ScanFilter scanFilter =
                    new ScanFilter.Builder()
                            .setDeviceName(deviceName)
                            .build();
            List<ScanFilter> scanFilterList = new ArrayList<>();
            scanFilterList.add(scanFilter);

            ScanSettings scanSettings =
                    new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                            .build();

            mBluetoothLeScanner.startScan(scanFilterList, scanSettings, mScanCallback);
        }

        setStatus(BleStatus.SCANNING);
    }

    /**
     * BLE機器検索停止
     */
    private void stopScan() {
        Log.d(TAG, "stopScan()");
        if(isScanning()) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            mScanHandler.removeCallbacksAndMessages(null);
            setStatus(BleStatus.DISCONNECTED);
        }
    }

    /**
     * 機器検索結果応答コールバック
     */
    final ScanCallback mScanCallback = new ScanCallback() {
        /**
         * 機器検索結果応答
         *
         * @param callbackType 応答種別
         * @param result 検索結果
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            Log.d(TAG, "onScanResult(callbackType : " + callbackType + ", result : " + result);
            Log.d(TAG, "    name: " + bluetoothDevice.getName() + " address:" + bluetoothDevice.getAddress());

            // スキャン停止
            stopScan();

            // Bluetooth機器とGATT接続する
            mBluetoothGatt = bluetoothDevice.connectGatt(mMainActivity.getApplicationContext(), false, mGattCallback);
            if(mBluetoothGatt.connect()) {
                Log.d(TAG, "GATT connected. discoverServices");
            }
        }

        /**
         * バッチ処理応答
         *
         * @param results 検索結果リスト
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults");
        }

        /**
         * 検索失敗応答
         *
         * @param errorCode エラーコード
         */
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed(errorCode : " + errorCode + ")");
        }
    };

    /**
     * GATT接続応答コールバック
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * 接続状態変化応答
         *
         * @param gatt GATTインスタンス
         * @param status 旧状態
         * @param newState 新状態
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange(" + status + " -> " + newState + ")");
            if ((newState == BluetoothProfile.STATE_CONNECTED) || (newState == BluetoothProfile.STATE_CONNECTING)) {
                mBluetoothGatt = gatt;
                // サービス検索
                mBluetoothGatt.discoverServices();
            } else {
                gatt.close();

                // GATT通信から切断された
                mBluetoothGatt = null;

                setStatus(BleStatus.DISCONNECTED);

                showToast(R.string.msg_gatt_connect_fail);
            }
        }

        /**
         * サービス検索結果応答
         *
         * @param gatt GATTインスタンス
         * @param status 検索結果
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered(status : " + status + ")");

            List<BluetoothGattService> gattServices = gatt.getServices();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 検索成功
                setStatus(BleStatus.SERVICE_FOUND);
                for(BluetoothGattService gattService : gattServices) {
                    /* サービス */
//                    Log.d(TAG, "Service_UUID: " + gattService.getUuid());

                    /* キャラクテリスティックス */
                    List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
                    for(BluetoothGattCharacteristic characteristic : characteristics) {

                        // 通知プロパティ以外は無処理
                        if(!((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)
                                == BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                            continue;
                        }

                        // 加速度センサのみ通知有効とする
                        if(!characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTERISTIC_ACCEL))) {
                            continue;
                        }

                        // Characteristic の Notification 有効化
                        BluetoothGattDescriptor descriptor =
                                characteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);

                        // Notification を要求する
                        if (gatt.setCharacteristicNotification(characteristic, true)) {
                            // Characteristics通知設定が成功
                            setStatus(BleStatus.CONNECTED);
                            Log.d(TAG, "setCharacteristicNotification() Success");
                        } else {
                            // Characteristics通知設定が失敗
                            Log.d(TAG, "setCharacteristicNotification() Failed");
                        }
                    }
                }

                Log.d(TAG, "BLE Connectting Finish");
            }else{
                // サービスなし
                setStatus(BleStatus.SERVICE_NOT_FOUND);
            }
        }

        /**
         * キャラクテリスティックス変化応答
         *
         * @param gatt GATTインスタンス
         * @param characteristic キャラクテリスティックス
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //Log.d(TAG, "onCharacteristicChanged:" + characteristic.getUuid().toString());
            byte[] read_data = characteristic.getValue();
            String data = new String(read_data);
            UUID characteristic_uuid = characteristic.getUuid();

            if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_GYRO))) {
                // 9Axis Sensor: GYRO（ジャイロ）
            } else if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_ACCEL))) {
                // 9Axis Sensor: ACCEL（加速度センサ）
                try {
                    // JSON形式で通知されるデータからX軸成分を抽出
                    JSONObject json = new JSONObject(data);
                    double ax = json.getDouble("ax");
                    // パッドへデータ設定
                    ((BreakoutView) mMainActivity.findViewById(R.id.breakout)).setPadDelta(ax * 50);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            } else if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_MAGM))) {
                // 9Axis Sensor: Magnetometer（地磁気センサ）
            } else if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_ANGLE))) {
                // Axis angle
            } else if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_TEMP))) {
                // Temperature
            } else if(characteristic_uuid.equals(UUID.fromString(UUID_CHARACTERISTIC_AIRP))) {
                // Airpressure（気圧センサー）
            }
        }
    };

    /**
     * BLE状態変化設定
     *
     * @param status BLE状態
     */
    private void changeBleStatus(BleStatus status) {
        Log.d(TAG, "changeBleStatus(" + status.name() + ")");

        ((BreakoutView) mMainActivity.findViewById(R.id.breakout)).setBleConnect(status == BleStatus.CONNECTED);
    }

    /**
     * メッセージハンドラ
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(MsgType.cast(msg.what))
            {
                case BLE_STATUS_CHANGE:
                    /* Statusの更新 */
                    changeBleStatus(BleStatus.cast(msg.arg1));
                    break;
                case MESSAGE_SEND:
                    /* メッセージ表示 */
                    Toast.makeText(mMainActivity.getApplicationContext(), mMainActivity.getString(msg.arg1), Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    };

    /**
     * 状態設定
     *
     * @param status BLE状態
     */
    private void setStatus(BleStatus status) {
        Log.d(TAG, "setStatus(" + mStatus.name() + " -> " + status.name() + ")");
        mStatus = status;
        switch (mStatus) {
            case SCANNING:
            case CONNECTING:
            case DEVICE_FOUND:
            case SERVICE_FOUND:
            case SERVICE_NOT_FOUND:
            case SCAN_FAILED:
            case DISCONNECTED:
                break;
            default:
                break;
        }

        Message msg = new Message();
        msg.what = MsgType.BLE_STATUS_CHANGE.ordinal();
        msg.arg1 = status.ordinal();
        mHandler.sendMessage(msg);
    }

    /**
     * トースト表示
     *
     * @param msgId メッセージID
     */
    private void showToast(int msgId) {
        Message msg = new Message();
        msg.what = MsgType.MESSAGE_SEND.ordinal();
        msg.arg1 = msgId;
        mHandler.sendMessage(msg);
    }
}