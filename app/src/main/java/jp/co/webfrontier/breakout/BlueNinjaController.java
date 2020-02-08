package jp.co.webfrontier.breakout;

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
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    private static final String TAG = "BlueNinjaController";

    /**
     * B-08．BLEデバイスと接続してパッド操作を行う
     * 各機器ごとの名前に変更する
     *
     */
    // 接続先のBlueNinjaデバイス名
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
        INVALID("不正", -1),
        BLE_STATUS_CHANGE("BLE状態変化", 0),
        MESSAGE_SEND("メッセージ送信", 1);

        private final String name;
        /**
         * 状態値
         */
        private final int value;

        /**
         * コンストラクタ
         *
         * @param name 状態名
         * @param value 状態の値
         */
        private MsgType(final String name, final int value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * メッセージの値を取得する
         *
         * @return 状態値
         */
        public int getValue()
        {
            return value;
        }

        /**
         * Enumを取得する
         *
         * @param value Enumの数値
         * @return Enum
         */
        public static MsgType getEnum(final int value) {
            MsgType[] types = MsgType.values();
            for (MsgType type : types) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return MsgType.INVALID;
        }
    }

    /**
     * BLE接続状態
     */
    private enum BLEStatus {
        INVALID("無効", -1),
        DISCONNECTED("切断", 0),
        SCANNING("検索中", 1),
        SCAN_FAILED("検索失敗", 2),
        DEVICE_FOUND("デバイス検出", 3),
        CONNECTING("接続中", 4),
        CONNECTED("接続済み", 5),
        SERVICE_NOT_FOUND("サービスなし", 6),
        SERVICE_FOUND("サービス検出", 7);

        private final String name;
        /**
         * 状態値
         */
        private final int value;

        /**
         * コンストラクタ
         *
         * @param name 状態名
         * @param value 状態の値
         */
        private BLEStatus(final String name, final int value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * 状態値を取得する
         *
         * @return 状態値
         */
        public int getValue()
        {
            return value;
        }

        /**
         * Enumを取得する
         *
         * @param value Enumの数値
         * @return 状態値
         */
        public static BLEStatus getEnum(final int value) {
            BLEStatus[] statuses = BLEStatus.values();
            for (BLEStatus status : statuses) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            return BLEStatus.INVALID;
        }
    }
    /**
     * BLE接続状態
     */
    private BLEStatus status = BLEStatus.INVALID;

    /**
     * メインアクティビティ
     */
    private MainActivity activity;
    /**
     * BLE関連
     */
    private BluetoothLeScanner bluetoothLeScanner = null;
    private BluetoothGatt bluetoothGatt = null;

    /**
     * コンストラクタ
     *
     * @param activity アクティビティインスタンス
     */
    public BlueNinjaController(MainActivity activity)
    {
        this.activity = activity;
    }

    /**
     * 初期化処理
     */
    public void init() {
        // 初期化
        status = BLEStatus.DISCONNECTED;
    }

    /**
     * BLE接続判定
     *
     * @retval true  接続中
     * @retval false 非接続
     */
    public boolean isConnected() {
        return (status == BLEStatus.CONNECTED);
    }

    /**
     * BLE機器検索判定
     *
     * @retval true  検索中
     * @retval false 未検索
     */
    public boolean isScanning() {
        return (status == BLEStatus.SCANNING);
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
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

        // BLEサポートバージョン判定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

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

        if(bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    /**
     * 機器検索ハンドラ
     */
    private Handler scanHandler = new Handler();

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
            scanHandler.postDelayed(new Runnable() {
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
            bluetoothLeScanner.startScan(scanCallback);
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

            bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback);
        }

        setStatus(BLEStatus.SCANNING);
    }

    /**
     * BLE機器検索停止
     */
    private void stopScan() {
        Log.d(TAG, "stopScan()");
        if(isScanning()) {
            bluetoothLeScanner.stopScan(scanCallback);
            scanHandler.removeCallbacksAndMessages(null);
            setStatus(BLEStatus.DISCONNECTED);
        }
    }

    /**
     * 機器検索結果応答コールバック
     */
    final ScanCallback scanCallback = new ScanCallback() {
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
            bluetoothGatt = bluetoothDevice.connectGatt(activity.getApplicationContext(), false, gattCallback);
            if(bluetoothGatt.connect()) {
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
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
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
                bluetoothGatt = gatt;
                // サービス検索
                bluetoothGatt.discoverServices();
            } else {
                gatt.close();

                // GATT通信から切断された
                bluetoothGatt = null;

                setStatus(BLEStatus.DISCONNECTED);

                //トーストを表示する
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
                setStatus(BLEStatus.SERVICE_FOUND);
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
                            setStatus(BLEStatus.CONNECTED);
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
                setStatus(BLEStatus.SERVICE_NOT_FOUND);
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
                    // Activityへ通知
                    activity.onBLEDataReceived(json);
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
    private void changeBleStatus(BLEStatus status) {
        Log.d(TAG, "changeBleStatus(" + status.name() + ")");
        activity.onBLEConnectionStatusChanged(status == BLEStatus.CONNECTED);
    }

    /**
     * メッセージハンドラ
     */
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final MsgType type = MsgType.getEnum(msg.what);
            switch(type) {
                case BLE_STATUS_CHANGE:
                    // ステータスの更新
                    final BLEStatus status = BLEStatus.getEnum(msg.arg1);
                    changeBleStatus(status);
                    break;
                case MESSAGE_SEND:
                    // メッセージの表示
                    Toast.makeText(activity.getApplicationContext(), activity.getString(msg.arg1), Toast.LENGTH_LONG)
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
    private void setStatus(BLEStatus status) {
        Log.d(TAG, "setStatus(" + status.name() + " -> " + status.name() + ")");
        this.status = status;
        switch (status) {
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
        msg.what = MsgType.BLE_STATUS_CHANGE.getValue();
        msg.arg1 = status.getValue();
        messageHandler.sendMessage(msg);
    }

    /**
     * トーストを表示する
     *
     * @param msgId メッセージID
     */
    private void showToast(int msgId) {
        Message msg = new Message();
        msg.what = MsgType.MESSAGE_SEND.getValue();
        msg.arg1 = msgId;
        messageHandler.sendMessage(msg);
    }
}