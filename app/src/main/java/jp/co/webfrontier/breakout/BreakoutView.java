package jp.co.webfrontier.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * ブロック崩しゲームのViewクラス
 */
public class BreakoutView extends View {
    /**
     * ステータス表示領域(基本部分)の高さ
     */
    public static final int STATUS_H = 240;
    /**
     * ブロックの上のスペースの高さ
     */
    public static final int UPPER_SPACE = 100;

    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BreakoutView";
    /**
     * ステータス領域背景色
     */
    private static final int STS_BG_COLOR = Color.WHITE;

    /**
     * 画面の大きさ
     */
    private Rect displayRect = new Rect();
    /**
     * ゲームフィールドの大きさ
     */
    private Rect fieldRect = new Rect();
    /**
     * ペインター
     */
    private Paint painter = new Paint();

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
        fieldRect.set(0, STATUS_H, displayRect.width(), displayRect.height());
        Log.d(TAG, "新しいゲームフィールド領域");
        Log.d(TAG, "x: " + fieldRect.left + ", y: " + fieldRect.top + ", width: " + fieldRect.width() + ", height: " + fieldRect.height());

        MainActivity activity = (MainActivity)getContext();
        activity.onBreakOutViewSizeChanged(fieldRect);
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

        MainActivity activity = (MainActivity)this.getContext();

        // 1. ステータス領域の色を設定する
        canvas.drawColor(STS_BG_COLOR);

        // 2. ゲームフィールドを描画する
        canvas.drawRect(displayRect.left, STATUS_H, displayRect.width(), displayRect.height(), painter);

        // 3. パッドを描画する
        /**
         * B-02．パッドを表示させる
         * ここでパッドを描画するメソッドを呼び出す
         */
        Pad pad = activity.getPad();
        pad.draw(canvas);

        // 4. ボールを描画する
        ArrayList<Ball> balls = activity.getBalls();
        for(Ball ball : balls) {
            /**
             * B-03．ボールを表示させる
             * ここでボールを描画するメソッドを呼び出す
             */
            ball.draw(canvas);
        }

        // 5. ブロックを描画する
        Brick[] bricks = activity.getBricks();
        for(int col = 0; col < MainActivity.BRICK_COLS; col++) {
            bricks[col].draw(canvas);
        }
    }

    public Rect getGameFieldRect() {
        return fieldRect;
    }
}
