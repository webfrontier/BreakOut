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
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                    SensorEventListener, BlueNinjaListener{

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
         * スタートボタンを作成する
         * AndroidStudioのデザイン画面からボタンウィジェットを追加する
         * ボタンのラベルを文字列リソースとして定義し表示させる
         * MainActivity#onClickメソッドをリスナーとして登録する
         * MainActivity#onClickメソッドからゲームを開始する(ゲームをRUNNING状態にする)
         */
        Button btn = (Button)findViewById(R.id.start_btn);
        btn.setOnClickListener(this);

        /**
         * B-07:接続ボタンを作成する
         */
        Button bt_btn = (Button)findViewById(R.id.bt_btn);

        // デバイスがBLEに対応しているかを確認する.
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // BLEに対応している
            /**
             * B-07:接続ボタンを作成する
             * MainActivity#onClickメソッドをリスナーとして登録する
             */
            bt_btn.setOnClickListener(this);
        } else {
            // BLEに対応していない
            /**
             * B-07:接続ボタンを作成する
             * 対応していない時はボタンを不可にする
             */
            bt_btn.setEnabled(false);
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

        // 効果音リソースの解放
        SoundController.release();

        /** A-03. ゲーム開始からの経過時間(ゲーム内時間)を表示する
         * 時間表示用のUI部品(Chronometer)を配置する
         * ゲーム内時間を管理する
         * 開始/停止/一時停止/再開を行う
         */
        // ゲームの経過時間を表示するためのカウンタを一時停止
        breakoutView.pauseElapsedTimeCounter();
    }

    /**
     * 一時停止からの再開
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "アクティビティが再開したよ");

        /** A-03. ゲーム開始からの経過時間(ゲーム内時間)を表示する
         * 時間表示用のUI部品(Chronometer)を配置する
         * ゲーム内時間を管理する
         * 開始/停止/一時停止/再開を行う
         */
        // ゲームの経過時間を表示するためのカウンタの再開
        breakoutView.resumeElapsedTimeCounter();

        final List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0) {
            final Sensor s = sensors.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }

        // 効果音制御の初期化
        SoundController.initialize(this);
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
         * スタートボタンを作成する
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
             * B-07:接続ボタンを作成する
             * クリック時に対応するようにidを設定する。
             */
            case R.id.bt_btn:
                Log.d(TAG, "接続ボタンがクリックされたよ");
                /**
                 * B-08．BLEデバイスと接続してパッド操作を行う
                 * MainActivity#onClickメソッドからBT接続を行う
                 */
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

            /**
             * B-01．センサーの値をログ出力してセンサーの動きを確認する
             * スマホの動きと方向を確認する
             */
            Log.v(TAG, "加速度が変わったよ");
            Log.v(TAG, "X方向: " + gx + ", Y方向: " + gy + ", Z方向: " + gz);

            /**
             * B-08．BLEデバイスと接続してパッド操作を行う
             * BLEデバイスと接続したらスマホのセンサーによる移動をやめる。
             */
            if(!blueNinjaController.isConnected()) {
                final Point p = breakoutView.getPadPosition();
                // 水平方向にのみ移動させたい
                breakoutView.movePad(-5.0f * gx, 0);
            }
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
    public void onBLEConnectionStatusChanged(boolean connected) {
        /**
         * B-08．BLEデバイスと接続してパッド操作を行う
         * BLEの接続状態に応じてパッドの色を変更する.
         */
        if (connected) {
            // BLEデバイスと接続しました
            breakoutView.setPadColor(BreakoutView.BLE_CONNECTED_COLOR);
        } else {
            // BLEデバイスが切断されました
            breakoutView.setPadColor(BreakoutView.BLE_DISCONNECTED_COLOR);
        }
    }


    /**
     * BLE機器からのデータ受信
     *
     * @param dataObject 受信データ(Json)
     */
    @Override
    public void onBLEDataReceived(JSONObject dataObject) {
        /**
         * データ形式はJSON形式で{ ax: xx(-1〜1), ay: yy(-1〜1), az: zz(-1〜1) }(xx: X軸方向の加速度, yy: Y軸方向の加速度, zz: Z軸方向の加速度)
         */
        try {
            Log.d(TAG, "BlueNinjaからのデータ:" + dataObject.toString(2));

            // 通知されるデータからX軸方向の加速度を抽出
            final double ax = dataObject.getDouble("ax");
            // 通知されるデータからY軸方向の加速度を抽出
            final double ay = dataObject.getDouble("ay");
            // 通知されるデータからZ軸方向の加速度を抽出
            final double az = dataObject.getDouble("az");
            /**
             * B-08．BLEデバイスと接続してパッド操作を行う
             * BlueNinjaから受信した加速度センサーの値をもとにパッドを移動させる
             * パッドは水平に移動させたいので、Y座標は変えない
             */
            breakoutView.movePad((float)ax*50, 0);
        } catch(JSONException e) {
            Log.e(TAG, "BlueNinjaからのデータが不正です");
        }
    }


}
