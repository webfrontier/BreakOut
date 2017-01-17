package jp.co.webfrontier.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * ブロック崩しゲームのViewクラス
 */
public class BreakoutView extends View {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BreakoutView";

    /**
     * ステータス表示領域の高さ
     */
    public static final int STATUS_H = 240;
    /**
     * ステータス領域背景色
     */
    private static final int STS_BG_COLOR = Color.WHITE;
    /**
     * 画面の大きさ
     */
    private Rect displayRect = new Rect();
    /**
     * ペインター
     */
    private Paint painter = new Paint();

    /**
     * 描画要素のリスト
     * onDrawメソッドが呼ばれたときにこのリストにある要素が描画される
     */
    private ArrayList<Item> drawableItems = new ArrayList<>();

    /**
     * 描画更新頻度
     */
    private static final long REFRESH_INTERVAL = 1000 / 60; // 60fps = 16.6..ms
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
         * sleepから復帰したらゲームの更新を行うためにBreakout#updateメソッドを呼び出す
         */
        @Override
        public void handleMessage(Message msg) {
            BreakoutView.this.game.update();
            sleep(REFRESH_INTERVAL);
        }
    };
    private RefreshHandler refreshHandler = new RefreshHandler();

    /**
     * ブロック崩しゲームのインスタンス
     */
    private Breakout game = new Breakout(this);

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
        refreshHandler.sleep(0);
    }

    /**
     * 描画要素を追加する
     * 次のフレームからこの要素が描画される
     * @param i 描画要素
     */
    public void addDrawingItem(Item i) {
        drawableItems.add(i);
    }

    /**
     * 描画要素を削除する
     * 次のフレームからこの要素が描画されなくなる
     * @param i 描画要素
     */
    public void removeDrawingItem(Item i)
    {
        drawableItems.remove(i);
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

        // ゲームフィールドの領域変更を通知
        game.onGameFieldSizeChanged(new Rect(0, 0, displayRect.width(), displayRect.height() - STATUS_H));
    }

    /**
     * Viewの描画処理を行う
     * View#invalidateメソッドを呼び出すとシステムから呼ばれる
     * ステータス領域、ゲームフィールドを描画する
     * 表示要素は各表示要素ごとの描画処理を呼び出す
     *
     * @param canvas 描画キャンバス
     */
    @Override
    public void onDraw(Canvas canvas) {
        // 1. ステータス領域の色を設定する
        canvas.drawColor(STS_BG_COLOR);

        // 2. ゲームフィールドを描画する
        canvas.drawRect(displayRect.left, STATUS_H, displayRect.width(), displayRect.height(), painter);

        // 3. 描画要素を描画する
        for(final Item item : drawableItems) {
            // 座標系を変換して描画する必要があるため原点を渡す
            item.draw(canvas, displayRect.left, STATUS_H);
        }
    }

    /**
     * ゲームフィールドがタッチされたときの処理
     */
    public void onTouch(final float x, final float y) {
    }

    /**
     * スタートボタンが押下されたときの処理
     */
    public void onPushStartButton() {
        Breakout.State state = game.getState();
        switch(state){
            case READY:
                game.setState(Breakout.State.RUNNING);
                break;
            case RUNNING:
                game.setState(Breakout.State.PAUSING);
                break;
            case PAUSING:
                game.setState(Breakout.State.RUNNING);
                break;
            case GAMEOVER:
            case CLEAR:
                game.setState(Breakout.State.READY);
                break;
            default:
                break;
        }
    }
}
