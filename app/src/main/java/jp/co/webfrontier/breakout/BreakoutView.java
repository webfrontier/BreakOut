package jp.co.webfrontier.breakout;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.TextView;

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
     * ステータス領域背景色
     */
    public static final int STS_BG_COLOR = Color.WHITE;
    /**
     * パッドの色(BLE未接続状態)
     */
    public static final int BLE_DISCONNECTED_COLOR = Color.YELLOW;
    /**
     * パッドの色(BLE接続状態)
     */
    public static final int BLE_CONNECTED_COLOR = Color.BLUE;

    /**
     * ステータス表示領域の高さ
     */
    private static final int STATUS_H = 240;

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
     * 描画要素のリストを空にする
     */
    public void clearDrawingItems()
    {
        drawableItems.clear();
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

        // 残りボール数の表示
        showRemainingBallCount();

        // 残りブロック数の表示
        showRemainingBrickCount();
    }

    /**
     * 残り(ゲームフィールドに出ていない)のボール数の表示
     */
    private void showRemainingBallCount() {
        TextView tv = (TextView)getRootView().findViewById(R.id.remaining_balls);
        if(tv != null) {
            Resources resource = getContext().getResources();
            CharSequence newMessage = resource.getText(R.string.remaining_ball_count);
            tv.setText(newMessage + Integer.toString(game.getRemainingBallCount()));
        }
    }

    /**
     * 残りのブロック数を表示する
     */
    private void showRemainingBrickCount() {
        TextView tv = (TextView)getRootView().findViewById(R.id.remaining_bricks);
        if(tv != null) {
            Resources resource = getContext().getResources();
            CharSequence newMessage = resource.getText(R.string.remaining_brick_count);
            tv.setText(newMessage + Integer.toString(game.getRemainingBricksCount()));
        }
    }

    /**
     * ゲームの状態に応じたメッセージを表示する
     *
     */
    public void showStateMessage() {
        /** スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
         * ゲームの開始が可能になったらスタート画面を出す
         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
         * ブロックを全て消したらゲームクリア
         * 全てのボールがゲームフィールド外に出たらゲームオーバー
         */
        TextView tv = (TextView)getRootView().findViewById(R.id.game_state_message);
        if(tv != null) {
            Breakout.State state = game.getState();
            switch(state) {
                case READY:
                    tv.setText(R.string.game_ready_message);
                    break;
                case PAUSING:
                    tv.setText(R.string.game_pause_message);
                    break;
                case GAMEOVER:
                    tv.setText(R.string.game_over_message);
                    break;
                case CLEAR:
                    tv.setText(R.string.game_clear_message);
                    break;
                default:
                    break;
            }
            tv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ゲームの状態に応じたメッセージを非表示にする
     */
    public void hideStateMessage() {
        TextView tv = (TextView)getRootView().findViewById(R.id.game_state_message);
        if(tv != null) {
            tv.setVisibility(View.INVISIBLE);
        }
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
        // 1. ステータス領域を描画する
        // 色を設定する
        canvas.drawColor(STS_BG_COLOR);

        // 残りボール数の表示
        showRemainingBallCount();

        // 残りブロック数の表示
        showRemainingBrickCount();

        // 2. ゲームフィールドを描画する
        canvas.drawRect(displayRect.left, STATUS_H, displayRect.width(), displayRect.height(), painter);

        // ゲームフィールド内の描画要素を描画する
        for(final Item item : drawableItems) {
            // キャンバスの座標系に変換して描画する必要があるためオフセット値を渡す
            item.draw(canvas, displayRect.left, STATUS_H);
        }
    }

    /**
     * 現在のパッドの位置を取得する
     *
     * @return パッドの中心座標
     */
    public Point getPadPosition() {
        final Point c = game.getPadPosition();
        Point p = new Point(c.x, c.y);
        p.offset(displayRect.left, STATUS_H);

        return p;
    }

    /**
     * パッドの色を取得する
     *
     * @return パッドの色
     */
    public int getPadColor() {
        return game.getPadColor();
    }

    /**
     * パッドの色を設定する
     *
     * @param color パッドに設定する色
     */
    public void setPadColor(final int color) {
        game.setPadColor(color);
    }

    /**
     * パッドを移動させる
     * ゲームが実行状態出ない場合はパッドを移動させない
     * 移動後もパッドは必ずゲームフィールド内に全て表示される
     *
     * @param dx X方向の移動量
     * @param dy Y方向の移動量
     */
    public void movePad(final float dx, final float dy) {
        if(game.getState() != Breakout.State.RUNNING) {
            // ゲームが実行状態でない場合はパッドを動かさない
            return;
        }

        final Pad pad = game.getPad();
        float px = pad.getCenter().x;
        float py = pad.getCenter().y;

        game.movePad((int)px, (int)py);
    }
    
    /**
     * ゲームフィールドがタッチされたときの処理
     */
    public void onTouch(final float x, final float y) {
        if(game.getState() == Breakout.State.GAMEOVER
                || game.getState() == Breakout.State.CLEAR) {
            // ゲームオーバーかゲームクリアの状態でタッチされたら、開始可能状態に戻す
            game.setState(Breakout.State.READY);
        }
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
