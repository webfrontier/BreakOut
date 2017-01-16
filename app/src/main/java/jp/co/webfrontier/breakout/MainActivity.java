package jp.co.webfrontier.breakout;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * ブロック崩しアプリのメインアクティビティ
 * クリックイベントをハンドルするためにOnClickListenerインターフェースを実装します
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * ブロック列数
     */
    public static final int BRICK_COLS = 6;

    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "MainActivity";

    /**
     * ゲームの進行状態
     */
    public enum GameState {
        /**
         * 初期状態
         */
        INIT("初期状態", 0),
        /**
         * ゲームを開始できる状態
         */
        READY("開始可能", 1),
        /**
         * ゲーム実行中
         */
        RUNNING("実行中", 2),
        /**
         * ゲームの一時停止中
         */
        PAUSING("一時停止中", 3),
        /**
         * ゲームオーバー
         */
        GAMEOVER("ゲームオーバー", 4),
        /**
         * ゲームクリア
         */
        CLEAR("ゲームクリア", 5);

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
        private GameState(final String name, final int value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * ゲーム進行の状態値を取得する
         *
         * @return 状態値
         */
        int getValue()
        {
            return value;
        }
    }
    /**
     * ゲームの進行状態
     */
    private GameState state = GameState.INIT;

    /**
     * 描画更新頻度
     */
    private static final long REFRESH_INTERVAL = 1000 / 60; // 60fps = 16.6..ms

    /**
     * ゲームフィールドの領域
     */
    private Rect fieldRect = new Rect();
    /**
     * ボール残数の初期値
     */
    private static final int DEFAULT_REMAINING_BALLS = 5;

    /**
     * ブロックオブジェクトの配列
     */
    private Brick[] bricks = new Brick[BRICK_COLS];

    /**
     * パッドオブジェクト
     */
    private Pad pad = new Pad();

    /**
     * ボールオブジェクトのリスト
     */
    private ArrayList<Ball> balls = new ArrayList<>();

    /**
     * ボール残数
     */
    private int remainingBallCount;

    /**
     * 更新ハンドラクラス
     * 一定時間後にupdateメソッドを実行させる
     */
    class RefreshHandler extends Handler {
        /**
         * 一定時間待機する
         */
        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }

        /**
         * sleepから復帰したらViewの更新を行うためにBreakoutView#updateメソッドを呼び出す
         */
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.update();
        }
    };
    private RefreshHandler refreshHandler = new RefreshHandler();

    /**
     * ブロック崩しゲームのビュー
     */
    private BreakoutView breakoutView;

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
     * アプリの状態を復元する
     *
     * @param state 状態復元データ
     */
    public void restoreState(Bundle state) {
        // ゲームの状態を一時停止に移行
        setGameState(GameState.PAUSING);
    }

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
                setGameState(GameState.RUNNING);
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
     * スタートボタンが押下されたときに行う処理
     */
    public void OnStartButtonPushed() {
        switch(state) {
            case INIT:
                break;
            case READY:
                break;
            case RUNNING:
                setGameState(GameState.PAUSING);
                break;
            case PAUSING:
                setGameState(GameState.RUNNING);
                break;
            case GAMEOVER:
                setGameState(GameState.READY);
                break;
            default:
                break;
        }
    }

    /**
     * ブロック崩しゲームの処理
     */

    /**
     * 新しくゲーム開始する
     */
    private void initializeGame() {

        // パッドを初期位置に配置する
        // initializePad();

        // ボールの状態を初期化する
        initializeBall();

        // ボールをゲームフィールドに追加する
        // addBall(500, 500);


        // ブロックを生成し初期位置に配置する
        createBrick();
        initializeBrick();

        update();
    }

    /**
     * ゲームの進行状態を取得する(getter)
     *
     * @return 現在のゲームの進行状態
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * ゲームの進行状態を設定する(setter)
     * 状態の変化(ゲームの状態遷移)に応じた処理を行う
     *
     * @param newState 新しい状態
     */
    public void setGameState(GameState newState) {
        GameState currentState = getGameState(); // 現在の状態を覚えておく

        if(currentState == newState) {
            return;
        }

        Log.i(TAG, "ゲームの状態が変わったよ");
        Log.i(TAG, "今の状態: " + currentState + " 新しい状態: " + newState);
        this.state = newState;

        if(currentState == GameState.INIT) {
            if(newState == GameState.READY) {
                initializeGame();
            }
        } else if(currentState == GameState.READY) {
            if(newState == GameState.RUNNING) {
                update();
            }
        } else if(currentState == GameState.RUNNING) {
            if(newState == GameState.READY) {
            } else if(newState == GameState.PAUSING) {
            } else if(newState == GameState.GAMEOVER) {
            } else if(newState == GameState.CLEAR) {
            } else {
                update();
            }
        } else if(currentState == GameState.PAUSING) {
        } else if(currentState == GameState.GAMEOVER) {
        } else if(currentState == GameState.CLEAR) {
        } else {
        }
    }

    /**
     * Viewの大きさが変わったときに行う処理
     *
     */
    public void onBreakOutViewSizeChanged(Rect newFieldRect) {
        fieldRect = newFieldRect;
        if (state == GameState.INIT) {
            setGameState(GameState.READY);
        }
    }

    /**
     * ゲームの更新処理を行う
     * 設定したリフレッシュレートに従い、画面描画更新を行う。
     */
    private void update() {
        if (getGameState() != GameState.RUNNING) {
            // View#invalidateメソッドを呼び再描画を要求する
            breakoutView.invalidate();
            return;
        }

        // 以降はゲーム実行中(RUNNING)状態で行う更新処理

        pad.update(this.breakoutView);

        int xCrash; // ブロックとの当たり判定（X方向）
        int yCrash; // ブロックとの当たり判定（Y方向）

        // ボールごとに表示更新／当たり判定
        for(int i = balls.size()-1; i>=0; i--) {
            Ball ball = balls.get(i);
            ball.update(this.breakoutView);
        }

        // ボール残総数を返却
        int ballCnt = remainingBallCount + balls.size();
        if(ballCnt > 0) {
            // ボール残数あり
            if(getRemainingBricksCount() == 0) {
                // ブロックがなくなった状態
                // ゲームクリア
            } else {
                // ブロックがまだ残っている状態
                // ゲーム継続のため、一定時間待機した後に再度呼び出してもらう
                refreshHandler.sleep(REFRESH_INTERVAL);
            }
        } else if(ballCnt == 0) {
            // ボールの残数がなくなった状態
            // ゲームオーバー
        }
    }

    public Pad getPad() {
        return pad;
    }

    /**
     * パッドの大きさをゲームフィールドの大きさから調整する
     */
    private void adjustPad() {
        int padWidth = fieldRect.width() / 6;
        int padHeight = fieldRect.height() / 100;
        pad.setRect(new Rect(pad.left(), pad.top(), pad.left() + padWidth, pad.top() + padHeight));
    }

    /**
     * パッドを初期位置に配置する
     */
    public void initializePad() {
        adjustPad();
        int padX = (fieldRect.width() - pad.getWidth()) / 2;
        int padY = fieldRect.top + fieldRect.height() - 5 * pad.getHeight();
        pad.setRect(new Rect(padX, padY, padX + pad.getWidth(), padY + pad.getHeight()));
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    /**
     * 新しいボールをゲームフィールドへ追加する
     *
     * @return true 成功
     * @returm false 失敗
     */
    private boolean addBall(int x, int y) {
        boolean ret = false;

        // ボール残数があるときのみ、ボールを追加する
        if(remainingBallCount > 0) {
            balls.add(new Ball(x, y));
            remainingBallCount--;

            ret = true;
        }

        return ret;
    }

    /**
     * ボールの状態を初期化する
     */
    private void initializeBall() {
        // ボール残数初期化
        remainingBallCount = DEFAULT_REMAINING_BALLS;

        // ゲームフィールドにあるボールをクリア
        balls.clear();
    }

    /**
     * ゲームフィールド上のボール有無
     *
     * @return true:ボールあり／false ボールなし
     */
    public boolean isBallinField() {
        return (balls.size() > 0);
    }

    public Brick[] getBricks() {
        return bricks;
    }

    /**
     * ブロックを生成する
     */
    private void createBrick() {
        for (int col = 0; col < BRICK_COLS; col++) {
            bricks[col] = new BrickNormal();
        }
    }

    /**
     * ブロックを初期位置に配置する
     * ブロックの位置と大きさをゲームフィールドの大きさから調整する
     */
    public void initializeBrick() {
        // ゲームフィールドの大きさから1つあたりのブロックの大きさを設定する
        int brick_w = fieldRect.width() / MainActivity.BRICK_COLS;
        int brick_h = fieldRect.height() / 30;

        for (int col = 0; col < MainActivity.BRICK_COLS; col++) {
            bricks[col].setSize(brick_w, brick_h);
            bricks[col].move(col * brick_w, brick_h + breakoutView.STATUS_H + breakoutView.UPPER_SPACE);
        }
    }

    /**
     * 残ブロック数取得
     *
     * @return 残ブロック数
     */
    public int getRemainingBricksCount() {
        int count = 0;
        for(int col = 0; col < BRICK_COLS; col++) {
            ++count;
        }
        return count;
    }

    /**
     * ブロック当たり判定
     *
     * @param index インデックス
     * @return true:当たり／false:外れ
     */
    @org.jetbrains.annotations.Contract("null -> false")
    private boolean isHitBricks(SetXY index) {
        return false;
    }

    /**
     * インデックス重複除外リスト
     *
     * @param list 追加対象インデックスリスト
     * @param index 追加インデックス
     *
     * @return 更新後のインデックスリスト
     */
    private List<SetXY> optimumList(List<SetXY> list, SetXY index) {
        if(index == null) {
            return list;
        }

        boolean exist = false;
        for(SetXY xy : list) {
            if(xy.equals(index)) {
                exist = true;
                break;
            }
        }

        // リストに存在しない場合のみ追加
        if(!exist) {
            list.add(index);
        }

        return list;
    }

    /**
     * BLE機器接続有無
     *
     * @param connect BLE機器接続有無
     */
    public void setBLEConnected(boolean connect) {
        pad.setBLEConnected(connect);
        breakoutView.invalidate();
    }
}
