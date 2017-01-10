package jp.co.webfrontier.breakout;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * ブロック崩しメインアクティビティ
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "MainActivity";

    /**
     * BlueNinja BLE接続制御
     */
    private BlueNinjaController mBlueNinjaController = new BlueNinjaController(this);

    /**
     * BreakoutView
     */
    private BreakoutView mBreakoutView;

    /**
     * アプリ生成時に呼び出し
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d(TAG, "onCreate()");

        mBlueNinjaController.init();

        // ボタンリスナー登録
        Button btn;
        btn = (Button)findViewById(R.id.start_btn);
        btn.setOnClickListener(this);
        btn = (Button)findViewById(R.id.ball_btn);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.bt_btn);
        // デバイスがBLEに対応しているかを確認する.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // BLEに対応していない旨のToastやダイアログを表示する.
            // BLE非対応のため、無効化
            btn.setEnabled(false);
        } else {
            btn.setOnClickListener(this);
        }

        mBreakoutView = (BreakoutView)findViewById(R.id.breakout);
    }

    /**
     * 他のアプリが移動（一時停止）
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause()");

        // [Task 23] 効果音追加
        // 音データリリース
        SoundController.release();

        // BLE接続を切断
        mBlueNinjaController.disconnectBle();

        // [Task 17] スタートから一定時間経つとボールのスピードが上がる
        // ゲームの経過時間を表示するためのカウンタを一時停止
        mBreakoutView.pauseElapsedTimeCounter();
    }

    /**
     * 一時停止からの再開
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        // [Task 23] 効果音追加
        SoundController.initialize(this);

        // BLEを再度接続

        // [Task 17] スタートから一定時間経つとボールのスピードが上がる
        // ゲームの経過時間を表示するためのカウンタの再開
        mBreakoutView.resumeElapsedTimeCounter();
    }

    /**
     * ボタン押下ハンドラ
     *
     * @param v 表示描画オブジェクト
     */
    public void onClick(View v) {
        // メッセージ消去
        mBreakoutView.hideMessage();

        switch(v.getId())
        {
            case R.id.bt_btn: {
                // BT接続
                if(mBlueNinjaController.isConnected()) {
                    mBlueNinjaController.disconnectBle();
                } else {
                    // デバイス名を入力し接続する場合は"showDialog()"を有効にする
//                    mBlueNinjaController.showDialog();
                    mBlueNinjaController.connectBle(); // [Task 13] BLE接続
                }
            }
                break;
            case R.id.start_btn:
                // START
                mBreakoutView.pushStart();
                break;
            case R.id.ball_btn:
                // ボール追加
                if(mBreakoutView.addBall()) {
                    // ボール残数再表示
                    mBreakoutView.refreshStockBallCount();
                }
                break;
        }
    }

    /**
     * タッチイベントハンドラ
     *
     * @param event タッチイベント
     *
     * @return true 処理成功
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_MOVE) {
            // タッチ（移動）操作
            if(!mBlueNinjaController.isConnected()) {
                // BLE機器未接続の場合はタッチによるパッド操作を行う
                mBreakoutView.setPadCx(event.getX());
            }
        }

        return true;
    }
}
