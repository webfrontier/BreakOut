package jp.co.webfrontier.breakout;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * ブロック崩しアプリのメインアクティビティ
 * クリックイベントをハンドルするためにOnClickListenerインターフェースを実装します
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "MainActivity";

    /**
     * BlueNinja BLE接続制御
     */
    private BlueNinjaController blueNinjaController = new BlueNinjaController(this);

    /**
     * BreakoutView
     */
    private BreakoutView breakoutView;

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

        blueNinjaController.init();

        breakoutView = (BreakoutView)findViewById(R.id.breakout);
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
    }

    /**
     * クリックイベントハンドラ
     * View.OnClickListenerインターフェースの実装
     * このメソッドはクリックイベントが発生したときに呼ばれます。
     *
     * @param v 表示描画オブジェクト
     */
    public void onClick(View v) {
        // ここにクリックイベントが発生した時に行う処理を書く
        Log.d(TAG, "アクティビティがクリックされたよ");
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
        Log.d(TAG, "アクティビティがタッチされたよ");
        Log.d(TAG, "x座標:" + event.getX() + ", y座標:" + event.getY() + " で発生したよ");
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 押下された場合
                Log.d(TAG, "発生したアクションはACTION_DOWNだよ");
                break;
            case MotionEvent.ACTION_UP: // 指を持ち上げた場合
                Log.d(TAG, "発生したアクションはACTION_UPだよ");
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
}
