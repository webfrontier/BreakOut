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
 * ブロック崩しViewクラス
 */
public class BreakoutView extends View {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BreakoutView";
    /**
     * モード定義
     */
    public static final int MODE_READY    = 0;   // スタート画面
    public static final int MODE_RUNNING  = 1;   // 実行中
    public static final int MODE_PAUSE    = 2;   // 一時停止中
    public static final int MODE_GAMEOVER = 3;   // GameOver
    public static final int MODE_CLEAR    = 4;   // クリア
    /**
     * ゲームモード（状態）
     */
    private int mode;
    /**
     * ブロック列数
     */
    public static final int BRICK_COL = 6; // [Task 12] ブロック行列追加
    /**
     * ステータス領域背景色
     */
    private static final int STS_BG_COLOR = Color.WHITE;
    /**
     * ステータス表示領域(基本部分)の高さ
     */
    private static final int STATUS_BASE_H = 160;
    /**
     * ブロックの上のスペースの高さ
     */
    private static final int UPPER_SPACE = 100;
    /**
     * 描画更新頻度
     */
    private static final long DELAY_MILLIS = 1000 / 60; // 60fps
    /**
     * フィールドサイズ（画面サイズ）
     */
    private int disp_w; // 幅
    private int disp_h; // 高さ

    private Paint mPaint = new Paint();
    /**
     * ブロック情報
     */
    public Brick[] mBricks = new Brick[BRICK_COL];
    /**
     * パッド情報
     */
    private Pad mPad = new Pad();
    /**
     * ボール残数
     */
    public int mStockBallCount;
    /**
     * ボール情報
     */
    private ArrayList<Ball> mBalls = new ArrayList<>();

    /**
     * 描画更新ハンドラ
     */
    private RefreshHandler mFieldHandler = new RefreshHandler();

    // 一定時間待機後Updateを実行させる。 Updateは再度Sleepを呼ぶ
    class RefreshHandler extends Handler {
        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }

        @Override
        public void handleMessage(Message msg) {
            BreakoutView.this.update();
        }
    };

    /**
     * ステータス表示領域の高さ
     */
    private static final int STATUS_H = STATUS_BASE_H;

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
    }

    /**
     * 新しくゲーム開始
     */
    private void newGame() {
        // ボール描画領域設定
        Ball.setFieldRect(new Rect(0, STATUS_H, disp_w, disp_h - STATUS_H));
        // ボール残数初期化
        mStockBallCount = 5;
        // ボール情報クリア
        mBalls.clear();
        // ブロックサイズ算出
        int brick_w = disp_w / BRICK_COL;
        int brick_h = disp_h / 30;
        // ブロックサイズ設定
        Brick.Initialize(brick_w, brick_h);

        for(int col = 0; col < BRICK_COL; col++) {
            mBricks[col] = new BrickNormal(col * brick_w, brick_h + STATUS_H + UPPER_SPACE);
        }

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
     * モード設定
     *
     * @param newMode 次ゲームモード
     */
    public void setMode(int newMode) {
        int oldMode = getMode();
        Log.i(TAG, "setmode(" + oldMode + " -> " + newMode + ")");
        this.mode = newMode;

        if(newMode == MODE_RUNNING) {
            if(oldMode == MODE_READY) {
                newGame();
            } else if(oldMode == MODE_PAUSE) {
            }
            if (oldMode != MODE_RUNNING) {
                update();
            }
        } else {
            int msgId = 0;
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
        return (mBalls.size() > 0);
    }

    /**
     * パッド中央位置設定
     *
     * @param x パッド中央設定X座標
     */
    public void setPadCx(float x) {
        mPad.setPadCx(x);
    }

    /**
     * タッチ位置設定
     *
     * @param d パッド移動変化値
     */
    public void setPadDelta(double d) {
        mPad.setPadDelta(d);
    }

    /**
     * BLE機器接続有無
     *
     * @param connect BLE機器接続有無
     */
    public void setBleConnect(boolean connect) {
        mPad.setmBleConnect(connect);
        invalidate();
    }

    /**
     * ボールをフィールドへ追加
     *
     * @return true:成功／false:失敗
     */
    public boolean addBall() {
        boolean ret = false;

        // 「ゲーム実行中」かつボール残数があるときのみ、フィールドへボールを追加
        if(mode == MODE_RUNNING && mStockBallCount > 0) {
            mBalls.add(new Ball(disp_w / 2, disp_h / 2));
            mStockBallCount--;

            ret = true;
        }

        return ret;
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
    public int getBricksCount() {
        int count = 0;
        for(int col = 0; col < BRICK_COL; col++) {
                    ++count;
        }
        return count;
    }

    private static final int BALL_PARAM_SIZE = 4;

    /**
     * 状態復元処理
     *
     * @param state 状態復元データ
     */
    public void restoreState(Bundle state) {
        // モードを一時停止に移行
        setMode(MODE_PAUSE);
        mode = state.getInt("mode");
        mBalls = flaotsToBalls(state.getFloatArray("balls"));
    }

    /**
     * 座標／速度の配列からボールリスト化
     *
     * @param floatArray 座標／速度の配列
     * @return ボールリスト
     */
    private ArrayList<Ball> flaotsToBalls(float[] floatArray) {
        ArrayList<Ball> balls = new ArrayList<>();
        int arrayCnt = floatArray.length;
        for(int index = 0; index < arrayCnt; index += BALL_PARAM_SIZE) {
            Ball ball = new Ball(floatArray[index], floatArray[index + 1], floatArray[index + 2], floatArray[index + 3]);
            balls.add(ball);
        }
        return balls;
    }

    /**
     * 状態保存処理
     *
     * @param state 格納領域
     * @return 状態格納後の格納領域
     */
    public Bundle saveState(Bundle state) {
        state.putInt("mode", mode);
        state.putFloatArray("balls", ballsToFloats(mBalls));
        return state;
    }

    /**
     * ボールリストから座標／速度の配列化
     *
     * @param ballArray ボールリスト
     * @return ボール座標／速度の配列
     */
    private float[] ballsToFloats(ArrayList<Ball> ballArray) {
        int size = ballArray.size();
        float[] rawArray = new float[size * BALL_PARAM_SIZE];
        for (int index = 0; index < size; index++) {
            Ball ball = ballArray.get(index);
            rawArray[BALL_PARAM_SIZE * index    ] = ball.getx();
            rawArray[BALL_PARAM_SIZE * index + 1] = ball.gety();
            rawArray[BALL_PARAM_SIZE * index + 2] = ball.getXSpeed();
            rawArray[BALL_PARAM_SIZE * index + 3] = ball.getYSpeed();
        }
        return rawArray;
    }

    /**
     * 画面サイズ変更通知<br>
     * コンストラクタ、初期化処理時には画面サイズ不定のため、
     *
     * @param w 新画面サイズ（幅）
     * @param h 新画面サイズ（高さ）
     * @param oldw 旧画面サイズ（幅）
     * @param oldh 旧画面サイズ（高さ）
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        disp_w = w;
        disp_h = h;
        // パッドへサイズ設定
        mPad.init(w, h);
        setMode(MODE_READY);

        newGame();
    }

    /**
     * 更新処理<br>
     * 設定したリフレッシュレートに従い、画面描画更新を行う。
     */
    public void update() {
        if(getMode() == MODE_RUNNING) {
            // パッド表示更新
            mPad.update(this);

            int xCrash; // ブロックとの当たり判定（X方向）
            int yCrash; // ブロックとの当たり判定（Y方向）
            // ボールごとに表示更新／当たり判定
            for(int i = mBalls.size()-1; i>=0; i--) {
                Ball ball = mBalls.get(i);
            }

            // ボール残総数を返却
            int ballCnt = mStockBallCount + mBalls.size();
            if(ballCnt > 0) {
                // ボール残数あり
                if(getBricksCount() == 0) {
                    // 残ブロック数が０のため、クリア
                    setMode(MODE_CLEAR); // [Task 10] クリア画面
                } else {
                    // ゲーム継続のため、sleep
                    mFieldHandler.sleep(DELAY_MILLIS);
                }
            } else if(ballCnt == 0) {
                // ボール残数なしのため、GameOver
                // [Task 11] ボールが全て下に落ちゲームオーバー
                setMode(MODE_GAMEOVER);
            }
        } else {
            invalidate();
        }
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    @Override
    public void onDraw(Canvas canvas) {
        // ステータス領域描画
        canvas.drawColor(STS_BG_COLOR);
        canvas.drawRect(new Rect(0, STATUS_H, disp_w, disp_h), mPaint);

        // ゲームフィールド領域描画
        // パッド描画
        // [Task 2] バーの表示

        // ボール描画
        for(Ball ball : mBalls) {
        }

        // ブロック描画
        for(int col = 0; col < BRICK_COL; col++) {
            mBricks[col].draw(canvas);
        }
    }

    /**
     * スタートボタン押下
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
