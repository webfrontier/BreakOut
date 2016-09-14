package jp.co.webfrontier.breakout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
     * BreakoutView
     */
    private BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        breakoutView = (BreakoutView)findViewById(R.id.breakout);
    }

    /**
     * ボタン押下ハンドラ
     *
     * @param v 表示描画オブジェクト
     */
    public void onClick(View v) {
        // メッセージ消去
        breakoutView.hideMessage();

        switch(v.getId())
        {
            case R.id.bt_btn: {
                // BT接続
                Intent intent = new Intent(this, BleActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.start_btn:
                // START
                switch(breakoutView.getMode()){
                    case GameInfo.READY:
                        breakoutView.setMode(GameInfo.RUNNING);
                        break;
                    case GameInfo.RUNNING:
                        breakoutView.setMode(GameInfo.PAUSE);
                        break;
                    case GameInfo.PAUSE:
                        breakoutView.setMode(GameInfo.RUNNING);
                        break;
                    case GameInfo.GAMEOVER:
                        breakoutView.setMode(GameInfo.READY);
                        break;
                    case GameInfo.CLEAR:
                        breakoutView.setMode(GameInfo.READY);
                        break;
                }
                break;
            case R.id.ball_btn:
                // ボール追加
                if(breakoutView.addBall()) {
                    // ボール残数再表示
                    breakoutView.refreshStockBallCount();
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
            breakoutView.setPadCx(event.getX());
        }

        return true;
    }
}
