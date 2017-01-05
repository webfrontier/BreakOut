package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * パッド
 */
public class Pad implements DrawableItem {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Pad";
    /**
     * パッドの色(BLE未接続)
     */
    private static final int PAD_COLOR = Color.YELLOW;
    /**
     * パッドの色(BLE接続)
     */
    private static final int BLEPAD_COLOR = Color.BLUE;
    /**
     * 画面の幅
     */
    private int disp_w;
    /**
     * パッドの高さ
     */
    public static int HEIGHT;
    /**
     * パッドの幅
     */
    public static int WIDTH;
    /**
     * パッド位置（左）
     */
    public float x;
    /**
     * パッド位置（上）
     */
    public float y;
    /**
     * パッド指定位置
     */
    private float mSetCx;
    /**
     * ボールを打ち返した回数
     */
    private int hitCount = 0;
    /**
     * BLE接続有無
     */
    private boolean mBleConnect = false;
    /**
     * ペインタ
     */
    private Paint mPadPaint = new Paint();

    /**
     * コンストラクタ
     */
    public Pad() {
        // ペインタへ色設定
        mPadPaint.setColor(PAD_COLOR);
    }

    /**
     * 初期化処理
     *
     * @param w 画面の幅
     * @param h 画面の高さ
     */
    public void init(int w, int h) {
        disp_w = w;

        // パッドサイズを画面サイズから算出
        Pad.HEIGHT = h / 100;
        Pad.WIDTH = w / 6;

        // パッド位置を画面サイズから算出
        x = (w - Pad.WIDTH) / 2;
        y = h / 100 * 90;

        // タッチ位置を画面中央で設定
        mSetCx = w / 2;
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + WIDTH, y + HEIGHT, mPadPaint);
    }

    /**
     * 描画領域取得
     *
     * @return 描画領域
     */
    @Override
    public Rect getRect() {
        // float -> intのキャストを行うため、1ずつ広くサイズを返却する。
        return new Rect(
                (int)this.x - 1,
                (int)this.y - 1,
                (int)this.getlx() + 1,
                (int)this.getly() + 1
        );
    }

    /**
     * タッチ位置設定
     *
     * @param d パッド移動変化値
     */
    public void setPadDelta(double d) {
        mSetCx += d;
        if(mSetCx < Pad.WIDTH / 2) {
            mSetCx = Pad.WIDTH / 2;
        } else if(mSetCx > disp_w - Pad.WIDTH / 2) {
            mSetCx = disp_w - Pad.WIDTH / 2;
        }
    }
    /**
     * タッチ位置設定
     *
     * @param x タッチX座標
     */
    public void setPadCx(float x) {
        mSetCx = x;
    }

    /**
     * パッド中央X座標取得
     *
     * @return パッド中央X座標
     */
    public float getcx() {
        return x + Pad.WIDTH / 2;
    }

    /**
     * パッド位置（右）X座標取得
     *
     * @return パッド位置（右）X座標
     */
    public float getlx() {
        return(x + WIDTH);
    }

    /**
     * パッド位置（下）Y座標取得
     *
     * @return パッド位置（下）Y座標
     */
    public float getly() {
        return(y + HEIGHT);
    }

    /**
     * 更新処理<br>
     * 指定位置からパッドの位置を算出し、パッドの再描画を行う。
     *
     * @param view パッド描画オブジェクト
     */
    public void update(View view) {
        view.invalidate(getRect());

        // [Task 3] パッド移動位置
        // 指定位置からパッド位置を算出
        x = mSetCx - Pad.WIDTH / 2;
        // 画面領域外の場合は補正する
        if(x < 0) {
            x = 0;
        }else if(getlx() > disp_w){
            x = disp_w - Pad.WIDTH;
        }

        view.invalidate(getRect());
    }

    /**
     * パッドとボールの当たり判定
     *
     * @param ball 判定対象ボールオブジェクト
     *
     * @return true  当たり
     * @return false 外れ
     */
    public boolean isBallHit(Ball ball) {
        // [Task 7] ボールとの当たり判定
        boolean ret = (y <= ball.getly() && getly() >= ball.gety() &&
                       x <= ball.getlx() && getlx() >= ball.getx());
        if(ret) {
            ++hitCount;
        }
        return ret;
    }

    /**
     * BLE接続状態設定
     *
     * @param connect BLE接続状態
     */
    public void setmBleConnect(boolean connect) {
        mBleConnect = connect;
        if(connect) {
            mPadPaint.setColor(BLEPAD_COLOR);
        } else {
            mPadPaint.setColor(PAD_COLOR);
        }
    }
}