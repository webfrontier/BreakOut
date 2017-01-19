package jp.co.webfrontier.breakout;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * ブロック崩しアプリのメインアクティビティ
 * クリックイベントをハンドルするためにOnClickListenerインターフェースを実装します
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, BlueNinjaListener {

    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "MainActivity";

    /**
     * ブロック崩しゲームのビュー
     */
    private BreakoutView breakoutView;

    /**
     * (加速度)センサー管理
     */
    private SensorManager sensorManager;

    /**
     * BlueNinja BLE接続制御
     */
    private BlueNinjaController blueNinjaController = new BlueNinjaController(this);

    /**
     * Activityのライフタイム管理
     */

    /**
     * アプリ生成時に呼び出し
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d(TAG, "アクティビティが生成されたよ");

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        blueNinjaController.init();

        /**
         * B-05．スタートボタンを作成する
         * AndroidStudioのデザイン画面からボタンウィジェットを追加する
         * ボタンのラベルを文字列リソースとして定義し表示させる
         * MainActivity#onClickメソッドをリスナーとして登録する
         * MainActivity#onClickメソッドからゲームを開始する(ゲームをRUNNING状態にする)
         */
        Button btn = (Button)findViewById(R.id.start_btn);
        btn.setOnClickListener(this);
        /**
         * B-14．BLEデバイスと接続してパッド操作を行う
         * AndroidStudioのデザイン画面からボタンウィジェットを追加する
         * ボタンのラベルを文字列リソースとして定義し表示させる
         * MainActivity#onClickメソッドをリスナーとして登録する
         * MainActivity#onClickメソッドからBT接続を行う
         * BlueNinjaから受信した加速度センサーの値をもとにパッドを移動させる
         * データ形式はJSON形式で{ ax: xx(-1〜1), ay: yy(-1〜1), az: zz(-1〜1) }(xx: X軸方向の加速度, yy: Y軸方向の加速度, zz: Z軸方向の加速度)
         */
        btn = (Button) findViewById(R.id.bt_btn);
        // デバイスがBLEに対応しているかを確認する.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // BLEに対応していない旨のToastやダイアログを表示する.
            // BLE非対応のため、無効化
            btn.setEnabled(false);
        } else {
            btn.setOnClickListener(this);
        }

        breakoutView = (BreakoutView)findViewById(R.id.breakout);
    }

    /**
     * アプリが停止
     */
    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "アクティビティが停止したよ");

        sensorManager.unregisterListener(this);
    }

    /**
     * 他のアプリが移動（一時停止）
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "アクティビティが一時停止したよ");
    }

    /**
     * 一時停止からの再開
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "アクティビティが再開したよ");

        final List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0) {
            final Sensor s = sensors.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * アプリの状態を復元する
     *
     * @param state 状態復元データ
     */
    public void restoreState(Bundle state) {}

    /**
     * アプリの状態を保存する
     *
     * @param state 格納領域
     * @return 状態格納後の格納領域
     */
    public Bundle saveState(Bundle state) {
        return state;
    }

    /**
     * ユーザーインターフェースのイベントのハンドリング
     */

    /**
     * クリックイベントハンドラ
     * View.OnClickListenerインターフェースの実装
     * このメソッドはクリックイベントが発生したときに呼ばれます。
     *
     * @param v 表示描画オブジェクト
     */
    public void onClick(View v) {
        // ここにクリックイベントが発生した時に行う処理を書く
        Log.d(TAG, "ビューがクリックされたよ");

        /**
         * B-05．スタートボタンを作成する
         * AndroidStudioのデザイン画面からボタンウィジェットを追加する
         * ボタンのラベルを文字列リソースとして定義し表示させる
         * MainActivity#onClickメソッドをリスナーとして登録する
         * MainActivity#onClickメソッドからゲームを開始する(ゲームをRUNNING状態にする)
         */
        switch(v.getId()) {
            case R.id.start_btn:
                // スタートボタン
                breakoutView.onPushStartButton();
                break;
            /**
             * B-14．BLEデバイスと接続してパッド操作を行う
             * AndroidStudioのデザイン画面からボタンウィジェットを追加する
             * ボタンのラベルを文字列リソースとして定義し表示させる
             * MainActivity#onClickメソッドをリスナーとして登録する
             * MainActivity#onClickメソッドからBT接続を行う
             * BlueNinjaから受信した加速度センサーの値をもとにパッドを移動させる
             * データ形式はJSON形式で{ ax: xx(-1〜1), ay: yy(-1〜1), az: zz(-1〜1) }(xx: X軸方向の加速度, yy: Y軸方向の加速度, zz: Z軸方向の加速度)
             */
            case R.id.bt_btn:
                // BT接続
                if(blueNinjaController.isConnected()) {
                    blueNinjaController.disconnectBle();
                } else {
                    blueNinjaController.connectBle();
                }
                break;
            default:
                break;
        }
    }

    /**
     * タッチイベントハンドラ
     * このメソッドはタッチイベントが発生したときに呼ばれます。
     *
     * @param event タッチイベント
     *
     * @return true 処理成功
     */
    public boolean onTouchEvent(MotionEvent event) {
        // ここにタッチイベントが発生した時に行う処理を書く
        Log.d(TAG, "タッチされたよ");
        Log.d(TAG, "x座標:" + event.getX() + ", y座標:" + event.getY() + " で発生したよ");
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 押下された場合
                Log.d(TAG, "発生したアクションはACTION_DOWNだよ");
                break;
            case MotionEvent.ACTION_UP: // 指を持ち上げた場合
                Log.d(TAG, "発生したアクションはACTION_UPだよ");
                breakoutView.onTouch(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE: // 指を持ち上げずにスライドさせた場合
                Log.d(TAG, "発生したアクションはACTION_MOVEだよ");
                break;
            case MotionEvent.ACTION_CANCEL: // UP+DOWNの同時発生(＝キャンセル)の場合
                Log.d(TAG, "発生したアクションはACTION_CANCELだよ");
                break;
            case MotionEvent.ACTION_OUTSIDE: // ターゲットとするUIの範囲外で発生した場合
                Log.d(TAG, "発生したアクションはACTION_OUTSIDEだよ");
                break;
            default: // その他のアクション
                break;
        }

        return true;
    }

    /**
     * センサーからの変更通知を処理する
     *
     * @param sensorEvent センサーのイベント
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float gx = sensorEvent.values[0]; // X方向(スマホを水平にした時の横方向)の加速度(右:-9.8xxx 〜 左:9.8xxx)
            float gy = sensorEvent.values[1]; // Y方向(スマホを水平にした時の縦方向)の加速度(上:-9.8xxx 〜 下:9.8xxx)
            float gz = sensorEvent.values[2]; // Z方向(スマホを水平にした時の鉛直方向)の加速度(上:-9.8xxx 〜 下:9.8xxx)

            Log.d(TAG, "加速度が変わったよ");
            Log.d(TAG, "X方向: " + gx + ", Y方向: " + gy + ", Z方向: " + gz);

            final Point p = breakoutView.getPadPosition();
            // 水平方向にのみ移動させたい
            breakoutView.movePad(-5.0f*gx, 0);
        }
    }

    /**
     * センサーからの通知
     *
     * @param sensor センサー
     * @param i インデックス
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * BLE機器の接続状態の変更通知
     *
     * @param connected BLE機器接続状態
     */
    @Override
    public void onBLEConnectionStatusChanged(boolean connected) {}

    /**
     * BLE機器からのデータ受信
     *
     * @param dataObject 受信データ(Json)
     */
    @Override
    public void onBLEDataReceived(JSONObject dataObject) {
        /**
         * B-14．BLEデバイスと接続してパッド操作を行う
         * AndroidStudioのデザイン画面からボタンウィジェットを追加する
         * ボタンのラベルを文字列リソースとして定義し表示させる
         * MainActivity#onClickメソッドをリスナーとして登録する
         * MainActivity#onClickメソッドからBT接続を行う
         * BlueNinjaから受信した加速度センサーの値をもとにパッドを移動させる
         * データ形式はJSON形式で{ ax: xx(-1〜1), ay: yy(-1〜1), az: zz(-1〜1) }(xx: X軸方向の加速度, yy: Y軸方向の加速度, zz: Z軸方向の加速度)
         */
        try {
            // 通知されるデータからX軸方向の加速度を抽出
            final double ax = dataObject.getDouble("ax");
            // パッドの移動量を決める
            // パッドは水平に移動させたいので、Y座標は変えない
            breakoutView.movePad((float)ax*50, 0);
        } catch(JSONException e) {
            Log.d(TAG, "BlueNinjaからのデータが不正です");
        }
    }
}
