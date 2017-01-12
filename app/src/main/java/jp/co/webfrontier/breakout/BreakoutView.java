package jp.co.webfrontier.breakout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ブロック崩しゲームのViewクラス
 */
public class BreakoutView extends View {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BreakoutView";
    /**
     * モード定義
     */
    public static final int MODE_READY    = 0;   // スタート可能
    public static final int MODE_RUNNING  = 1;   // 実行中
    public static final int MODE_PAUSE    = 2;   // 一時停止中
    public static final int MODE_GAMEOVER = 3;   // ゲームオーバー
    public static final int MODE_CLEAR    = 4;   // クリア
    /**
     * ゲームモード（状態）
     */
    private int mode;
    /**
     * ブロック列数
     * [Task 12] ブロック行列追加
     */
    public static final int BRICK_COLS = 6;
    /**
     * ステータス領域背景色
     */
    private static final int STS_BG_COLOR = Color.WHITE;
    /**
     * ステータス表示領域(基本部分)の高さ
     */
    private static final int STATUS_H = 240;
    /**
     * ブロックの上のスペースの高さ
     */
    private static final int UPPER_SPACE = 100;
    /**
     * 描画更新頻度
     */
    private static final long REFRESH_INTERVAL = 1000 / 60; // 60fps = 16.6..ms
    /**
     * 画面の大きさ
     */
    private Rect displayRect = new Rect();
    /**
     * ゲームフィールドの大きさ
     */
    private Rect fieldRect = new Rect();

    private Paint painter = new Paint();
    /**
     * ブロック情報
     */
    public Brick[] bricks = new Brick[BRICK_COLS];
    /**
     * パッド情報
     */
    private Pad pad = new Pad();
    /**
     * ボール残数
     */
    public int remainingBallCount;
    /**
     * ボール情報
     */
    private ArrayList<Ball> balls = new ArrayList<>();
    /**
     * 更新/描画ハンドラクラス
     * 一定時間後にBreakoutView#updateメソッドを実行させる
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
            BreakoutView.this.update();
        }
    };
    private RefreshHandler refreshHandler = new RefreshHandler();

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public BreakoutView(Context context) {
        super(context);
        initialize();
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs 属性
     */
    public BreakoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs 属性
     * @param defStyle スタイル
     */
    public BreakoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * 初期化処理
     */
    private void initialize() {
        setFocusable(true);
        // これからの処理のためパッドに描画するViewを覚えさせる
        pad.setView(this);
    }

    /**
     * 新しくゲーム開始する
     */
    private void newGame() {
        // ボール残数初期化
        remainingBallCount= 5;

        // ボール情報クリア
        balls.clear();

        // 1つあたりのブロックの大きさを算出
        int brick_w = fieldRect.width() / BRICK_COLS;
        int brick_h = fieldRect.height() / 30;

        // ブロックの大きさを設定する
        Brick.setSize(brick_w, brick_h);

        for(int col = 0; col < BRICK_COLS; col++) {
            bricks[col] = new BrickNormal(col * brick_w, brick_h + STATUS_H + UPPER_SPACE);
        }

        // View#invalidateメソッドを呼び出して再描画を要求する
        invalidate();
    }

    /**
     * モード取得
     *
     * @return 現在のモード
     */
    public int getMode() {
        return mode;
    }

    /**
     * モードの設定
     * モードの変更(ゲームの状態遷移)に応じた処理を行う
     *
     * @param newMode 新しいモード
     */
    public void setMode(int newMode) {
        int currentMode = getMode(); // 現在のモードを覚えておく

        if(currentMode == newMode) {
            return;
        }

        Log.i(TAG, "ゲームのモードが変わったよ");
        Log.i(TAG, "今のモード: " + currentMode + " 新しいモード: " + newMode);
        this.mode = newMode;

        if(newMode == MODE_RUNNING) {
            if(currentMode == MODE_READY) {
                newGame();
            } else if(currentMode == MODE_PAUSE) {
            }
            if (currentMode != MODE_RUNNING) {
                update();
            }
        } else {
            switch(newMode) {
                case MODE_PAUSE:
                    break;
                case MODE_READY:
                    break;
                case MODE_GAMEOVER:
                    break;
                case MODE_CLEAR:
                    break;
            }
        }
    }

    /**
     * フィールド上のボール有無
     *
     * @return true:ボールあり／false ボールなし
     */
    public boolean isBallinField() {
        return (balls.size() > 0);
    }

    /**
     * パッド中央位置設定
     *
     * @param x パッド中央設定X座標
     */
    public void setPadCx(float x) {
        pad.setPadCx(x);
    }

    /**
     * タッチ位置設定
     *
     * @param d パッド移動変化値
     */
    public void setPadDelta(double d) {
        pad.setPadDelta(d);
    }

    /**
     * BLE機器接続有無
     *
     * @param connect BLE機器接続有無
     */
    public void setBLEConnected(boolean connect) {
        pad.setBLEConnected(connect);
        invalidate();
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
     * 破壊可能なブロック数取得
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
     * 状態復元処理
     *
     * @param state 状態復元データ
     */
    public void restoreState(Bundle state) {
        // モードを一時停止に移行
        setMode(MODE_PAUSE);
        mode = state.getInt("mode");
    }

    /**
     * 状態保存処理
     *
     * @param state 格納領域
     * @return 状態格納後の格納領域
     */
    public Bundle saveState(Bundle state) {
        state.putInt("mode", mode);
        return state;
    }

    /**
     * Viewのサイズが変更された場合にシステムから呼ばれるメソッド
     * コンストラクタ、初期化処理時にはViewのサイズが不定のため、View#onSizeChangedメソッドをオーバーライドして処理する
     *
     * @param w 新画面サイズ（幅）
     * @param h 新画面サイズ（高さ）
     * @param oldw 旧画面サイズ（幅）
     * @param oldh 旧画面サイズ（高さ）
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "Viewの大きさが変わったよ");

        // 表示領域/ゲームフィールドの大きさを更新
        Log.d(TAG, "いまの表示領域");
        Log.d(TAG, "x: " + displayRect.left + ", y: " + displayRect.top + ", width: " + displayRect.width() + ", height: " + displayRect.height());
        displayRect.set(0, 0, w, h);
        Log.d(TAG, "新しい表示領域");
        Log.d(TAG, "x: " + displayRect.left + ", y: " + displayRect.top + ", width: " + displayRect.width() + ", height: " + displayRect.height());

        Log.d(TAG, "いまのゲームフィールド領域");
        Log.d(TAG, "x: " + fieldRect.left + ", y: " + fieldRect.top + ", width: " + fieldRect.width() + ", height: " + fieldRect.height());
        fieldRect.set(0, STATUS_H, displayRect.width(), displayRect.height() - STATUS_H);
        Log.d(TAG, "新しいゲームフィールド領域");
        Log.d(TAG, "x: " + fieldRect.left + ", y: " + fieldRect.top + ", width: " + fieldRect.width() + ", height: " + fieldRect.height());

        // パッドへViewの領域の変更を通知
        pad.onGameFieldChanged(fieldRect);

        // ボールへゲームフィールドの領域を通知
        Ball.onGameFieldChanged(fieldRect);

        // ゲームを開始する
        setMode(MODE_READY);
        newGame();
    }

    /**
     * 更新処理
     * 設定したリフレッシュレートに従い、画面描画更新を行う。
     */
    public void update() {
        if(getMode() == MODE_RUNNING) {

            // パッドの状態を更新
            pad.update(this);

            int xCrash; // ブロックとの当たり判定（X方向）
            int yCrash; // ブロックとの当たり判定（Y方向）

            // ボールごとに表示更新／当たり判定
            for(int i = balls.size()-1; i>=0; i--) {
                Ball ball = balls.get(i);
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
        } else {
            // View#invalidateメソッドを呼び再描画を要求する
            invalidate();
        }
    }

    /**
     * Viewの描画処理を行う
     * View#invalidateメソッドを呼び出すとシステムから呼ばれる
     * 各表示部品ごとに描画を行う
     *
     * @param canvas 描画キャンバス
     */
    @Override
    public void onDraw(Canvas canvas) {
        // 1. ステータス領域を描画する
        canvas.drawColor(STS_BG_COLOR);
        canvas.drawRect(displayRect.left, STATUS_H, displayRect.width(), displayRect.height(), painter);

        // 2. ゲームフィールドを描画する
        // 2-1. パッドを描画する

        // 2-2. ボールを描画する
        for(Ball ball : balls) {
        }

        // 2-3. ブロックを描画する
        for(int col = 0; col < BRICK_COLS; col++) {
            bricks[col].draw(canvas);
        }
    }

    /**
     * スタートボタンを押下したときの処理
     */
    public void pushStart() {
        switch(getMode()){
            case MODE_READY:
                setMode(MODE_RUNNING);
                break;
            case MODE_RUNNING:
                setMode(MODE_PAUSE);
                break;
            case MODE_PAUSE:
                setMode(MODE_RUNNING);
                break;
            case MODE_GAMEOVER:
                setMode(MODE_READY);
                break;
            case MODE_CLEAR:
                setMode(MODE_READY);
                break;
        }
    }
}
