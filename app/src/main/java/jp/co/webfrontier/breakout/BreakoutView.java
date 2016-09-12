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
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * ブロック崩しViewクラス
 */
public class BreakoutView extends View {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BreakoutView";
    /**
     * ゲーム情報
     */
    private GameInfo gInfo;
    /**
     * 背景色
     */
    private static final int BG_COLOR = Color.BLACK;
    /**
     * ステータス領域背景色
     */
    private static final int STS_BG_COLOR = Color.WHITE;
    /**
     * ステータス表示領域の高さ
     */
    private static final int STATUS_H = 160;
    /**
     * ステータス表示領域
     */
    private static Rect STATUS_RECT;
    /**
     * 描画更新頻度
     */
    private static final long DELAY_MILLIS = 1000 / 60;
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
     * メッセージ表示
     *
     * @param msgId メッセージID
     */
    public void showMessage(int msgId) {
        TextView tv = (TextView)getRootView().findViewById(R.id.message);
        if(tv != null) {
            tv.setText(msgId);
            tv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * メッセージ非表示
     */
    public void hideMessage() {
        TextView tv = (TextView)getRootView().findViewById(R.id.message);
        if(tv != null) {
            tv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 残りボール数表示更新
     */
    public void refreshStockBallCount() {
        TextView tv = (TextView)getRootView().findViewById(R.id.stock_balls);
        if(tv != null) {
            Resources resource = getContext().getResources();
            CharSequence newMessage = resource.getText(R.string.stock_ball_count);
            tv.setText(newMessage + Integer.toString(gInfo.mStockBallCount));
        }
    }

    /**
     * 新しくゲーム開始
     */
    private void newGame() {
        gInfo.init();
        invalidate();
    }

    /**
     * モード設定
     *
     * @param newMode 次ゲームモード
     */
    public void setMode(int newMode) {
        int oldMode = gInfo.getMode();
        gInfo.setMode(newMode);

        if(newMode == GameInfo.RUNNING) {
            if(oldMode == GameInfo.READY) {
                newGame();
                showMessage(R.string.new_ball_help);
            } else if(oldMode == GameInfo.PAUSE) {
                if(!gInfo.isBallinField()) {
                    showMessage(R.string.new_ball_help);
                } else {
                    hideMessage();
                }
            }
            if (oldMode != GameInfo.RUNNING) {
                update();
            }
        } else {
            int msgId = 0;
            switch(newMode) {
                case GameInfo.PAUSE:
                    msgId = R.string.pause_message;
                    break;
                case GameInfo.READY:
                    msgId = R.string.ready_message;
                    break;
                case GameInfo.GAMEOVER:
                    msgId = R.string.game_over_message;
                    break;
                case GameInfo.CLEAR:
                    msgId = R.string.game_clear_message;
                    break;
            }
            showMessage(msgId);
        }
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
        // ステータス表示領域設定
        STATUS_RECT = new Rect(0, STATUS_H, w, h);
        // ゲーム情報生成
        gInfo = new GameInfo(w, h, STATUS_H);
        // ボール残数表示
        refreshStockBallCount();
        setMode(GameInfo.READY);
    }

    /**
     * 更新処理<br>
     * 設定したリフレッシュレートに従い、画面描画更新を行う。
     */
    public void update() {
        int ret = gInfo.update(this);
        if(ret > 0) {
            // ボール残数あり
            if(gInfo.getBricksCount() == 0) {
                // 残ブロック数が０のため、クリア
                setMode(GameInfo.CLEAR);
            } else {
                // ゲーム継続のため、sleep
                mFieldHandler.sleep(DELAY_MILLIS);
            }
        } else if(ret == 0) {
            // ボール残数なしのため、GameOver
            setMode(GameInfo.GAMEOVER);
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
        Paint paint = new Paint();
        canvas.drawColor(STS_BG_COLOR);
        canvas.drawRect(STATUS_RECT, paint);
        // ゲームフィールド領域描画
        gInfo.draw(canvas);
    }

    /**
     * 状態保存<br>
     * バックグランド移行時の状態保存を行う。
     *
     * @param state 格納領域
     * @return 状態保存後の格納領域
     */
    public Bundle saveState(Bundle state) {
        return gInfo.saveState(state);
    }

    /**
     * 状態復元<br>
     * バックグランドからの復元を行う。
     *
     * @param state 状態復元データ格納領域
     */
    public void restoreState(Bundle state) {
        // モードを一時停止に移行
        setMode(GameInfo.PAUSE);
        gInfo.restoreState(state);
    }

    /**
     * モード取得
     *
     * @return 現在のモード
     */
    public int getMode() {
        return gInfo.getMode();
    }

    /**
     * パッド中央位置設定
     *
     * @param x パッド中央設定X座標
     */
    public void setPadCx(float x) {
        gInfo.setPadCx(x);
    }

    /**
     * ボールをフィールドへ追加
     *
     * @return true:成功／false:失敗
     */
    public boolean addBall() {
        return gInfo.addBall();
    }
}
