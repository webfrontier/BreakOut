package jp.co.webfrontier.breakout;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ブロック崩しゲームのクラス
 */

public class Breakout {

    /**
     * ブロック列数
     */
    public static final int BRICK_COLS = 6;

    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Breakout";

    /**
     * ゲームの状態
     */
    public enum State {
        /**
         * 初期状態
         */
        INIT("初期状態", 0),
        /**
         * ゲームを開始できる状態
         * ゲームフィールドの大きさが決まったらこの状態に遷移可能
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
        private State(final String name, final int value)
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
    private State state = State.INIT;

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
     * ゲームを表示するビュー
     */
    BreakoutView view;

    /**
     * コンストラクタ
     *
     * @param view ゲームを表示するビュー
     */
    public Breakout(BreakoutView view) {
        this.view = view;
    }

    /**
     * ブロック崩しゲームの処理
     */

    /**
     * 新しくゲーム開始する
     */
    private void start() {
        // ボールの状態を初期化する
        initializeBall();

        // ブロックを生成する
        createBrick();
        // 初期位置に配置する
        initializeBrick();
    }

    /**
     * ゲームの状態を取得する(getter)
     *
     * @return 現在のゲームの状態
     */
    public State getState() {
        return state;
    }

    /**
     * ゲームの進行状態を設定する(setter)
     * 状態の変化(ゲームの状態遷移)に応じた処理を行う
     *
     * @param newState 新しい状態
     */
    public void setState(State newState) {
        State currentState = getState(); // 現在の状態を覚えておく

        if(currentState == newState) {
            return;
        }

        Log.i(TAG, "ゲームの状態が変わったよ");
        Log.i(TAG, "今の状態: " + currentState + " 新しい状態: " + newState);
        this.state = newState;

        if(currentState == State.INIT) {
            if(newState == State.READY) {
                // 開始可能になったのでスタートする
                start();
            }
        } else if(currentState == State.READY) {
            if(newState == State.RUNNING) {
            }
        } else if(currentState == State.RUNNING) {
            if(newState == State.READY) {
            } else if(newState == State.PAUSING) {
            } else if(newState == State.GAMEOVER) {
            } else if(newState == State.CLEAR) {
            } else {
            }
        } else if(currentState == State.PAUSING) {
        } else if(currentState == State.GAMEOVER) {
        } else if(currentState == State.CLEAR) {
        } else {
        }
    }

    /**
     * ゲームフィールドの大きさが変わったときに行う処理
     * Viewの大きさが変わったときに通知される
     *
     */
    public void onViewSizeChanged() {
        if (state == State.INIT) {
            setState(State.READY);
        }
    }

    /**
     * 1フレーム分の更新処理を行う
     */
    public void update() {
        if (getState() != State.RUNNING) {
            // View#invalidateメソッドを呼び再描画を要求する
            view.invalidate();
            return;
        }

        // 以降はゲーム実行中(RUNNING)状態で行う更新処理

        pad.update();

        int xCrash; // ブロックとの当たり判定（X方向）
        int yCrash; // ブロックとの当たり判定（Y方向）

        // ボールごとに表示更新／当たり判定
        for(int i = balls.size()-1; i>=0; i--) {
            Ball ball = balls.get(i);
            ball.update();
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
        Rect fieldRect = view.getGameFieldRect();
        int padWidth = fieldRect.width() / 6;
        int padHeight = fieldRect.height() / 100;
        pad.setRect(new Rect(pad.left(), pad.top(), pad.left() + padWidth, pad.top() + padHeight));
    }

    /**
     * パッドを初期位置に配置する
     */
    public void initializePad() {
        adjustPad();

        Rect fieldRect = view.getGameFieldRect();
        int padX = (fieldRect.width() - pad.getWidth()) / 2;
        int padY = fieldRect.top + fieldRect.height() - 5 * pad.getHeight();
        pad.setRect(new Rect(padX, padY, padX + pad.getWidth(), padY + pad.getHeight()));
        view.addDrawingItem(pad);
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
            Ball ball = new Ball(x, y);
            balls.add(ball);
            view.addDrawingItem(ball);
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
        Rect fieldRect = view.getGameFieldRect();
        int brick_w = fieldRect.width() / BRICK_COLS;
        int brick_h = fieldRect.height() / 30;

        for (int col = 0; col < BRICK_COLS; col++) {
            bricks[col].setSize(brick_w, brick_h);
            bricks[col].move(col * brick_w, brick_h + view.STATUS_H + view.UPPER_SPACE);
            view.addDrawingItem(bricks[col]);
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
     * BLE機器接続有無
     *
     * @param connect BLE機器接続有無
     */
    public void setBLEConnected(boolean connect) {
        pad.setBLEConnected(connect);
        view.invalidate();
    }
}
