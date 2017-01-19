package jp.co.webfrontier.breakout;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

/**
 * ブロック崩しゲームのクラス
 */

public class Breakout {

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
        public int getValue()
        {
            return value;
        }
    }
    /**
     * ゲームの進行状態
     */
    private State state = State.INIT;

    /**
     * ゲームフィールドの大きさ
     */
    private Rect fieldRect = new Rect();

    /**
     * ゲームフィールドから上のブロックまでのスペース
     */
    private static final int BRICK_UPPER_SPACE = 100;

    /**
     * ブロックの行数
     */
    /** B-13．ブロックを複数行にする
     *  二次元配列を作る
     */
    public static final int BRICK_ROW = 5;

    /**
     * ブロックの列数
     */
    public static final int BRICK_COL = 6;

    /**
     * ブロックの配列
     */
    /** B-13．ブロックを複数行にする
     *  二次元配列を作る
     */
    private Brick[][] bricks = new Brick[BRICK_ROW][BRICK_COL];

    /**
     * パッド
     */
    private Pad pad = new Pad();

    /**
     * ゲームフィールドに出ているボールのリスト
     */
    private ArrayList<Ball> activeBalls = new ArrayList<>();

    /**
     * ゲームフィールドから出たボールのリスト
     * 削除処理用
     */
    private ArrayList<Ball> deactiveBalls = new ArrayList<>();

    /**
     * ボール残数の初期値
     */
    private static final int DEFAULT_REMAINING_BALLS = 5;

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
        Log.d(TAG, "ゲームを開始するよ。スタートボタンを押してね。");

        // 描画要素をクリアする
        view.clearDrawingItems();

        /**
         * B-02．パッドを表示させる
         * パッドを初期位置に表示させる
         * 移動はさせない(描画更新はなし)
         */
        initializePad();
        
        /**
         * B-03．ボールを表示させる
         * ボールをゲームフィールドの中央に表示させる
         * 座標系(原点と軸)の話をする
         * 移動はさせない(描画更新はなし)
         * パッドとの当たり判定もなし
         */
        // ボールの状態を初期化する
        initializeBall();
        addBall(fieldRect.width()/2, fieldRect.height()/2);

        /**
         * B-04．ブロックを表示させる
         * ブロックを生成し初期位置に配置する
         */
        createBrick();
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

        switch(currentState) {
            case INIT:
                // 初期状態
                switch(newState) {
                    case READY:
                        // 初期状態 -> 開始可能
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        // スタートする
                        start();
                        break;
                    default:
                        break;
                }
                break;
            case READY:
                // 開始可能
                /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                 * ゲームの開始が可能になったらスタート画面を出す
                 * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                 * ブロックを全て消したらゲームクリア
                 * 全てのボールがゲームフィールド外に出たらゲームオーバー
                 */
                switch(newState) {
                    case RUNNING:
                        // 開始可能 -> 実行中
                        view.hideStateMessage();
                        break;
                    default:
                        break;
                }
                break;
            case RUNNING:
                // 実行中
                switch(newState) {
                    case PAUSING:
                        // 実行中 -> 一時停止
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        break;
                    case GAMEOVER:
                        // 実行中 -> ゲームオーバー
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        break;
                    case CLEAR:
                        // 実行中 -> ゲームクリア
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        break;
                    default:
                        break;
                }
                break;
            case PAUSING:
                // 一時停止中
                /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                 * ゲームの開始が可能になったらスタート画面を出す
                 * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                 * ブロックを全て消したらゲームクリア
                 * 全てのボールがゲームフィールド外に出たらゲームオーバー
                 */
                switch(newState) {
                    case RUNNING:
                        // 一時停止 -> 実行中
                        view.hideStateMessage();
                        break;
                    default:
                        break;
                }
                break;
            case GAMEOVER:
                // ゲームオーバー
                switch(newState) {
                    case READY:
                        // ゲームオーバー -> 開始可能
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        // スタートする
                        start();
                        break;
                    default:
                        break;
                }
                break;
            case CLEAR:
                // ゲームクリア
                switch(newState) {
                    case READY:
                        // ゲームクリア -> 開始可能
                        /** B-12. スタート、クリア、ゲームオーバー、一時停止中でメッセージを表示する
                         * ゲームの開始が可能になったらスタート画面を出す
                         * ゲームの実行中にスタートボタンが押されたら一時停止、再度スタートボタンが押されたら再開
                         * ブロックを全て消したらゲームクリア
                         * 全てのボールがゲームフィールド外に出たらゲームオーバー
                         */
                        view.showStateMessage();
                        // スタートする
                        start();
                        break;
                    default:
                        break;
                }
                break;
            default:
                // 上記以外
                break;
        }
    }

    /**
     * ゲームフィールドの領域を取得する
     */
    public Rect getGameFieldRect() {
        return fieldRect;
    }

    /**
     * ゲームフィールドの大きさが変わったときに行う処理
     * Viewの大きさが変わったときに通知される
     *
     */
    public void onGameFieldSizeChanged(Rect rect) {
        Log.d(TAG, "いまのゲームフィールド領域");
        Log.d(TAG, "x: " + fieldRect.left + ", y: " + fieldRect.top + ", width: " + fieldRect.width() + ", height: " + fieldRect.height());
        fieldRect.set(rect);
        Log.d(TAG, "新しいゲームフィールド領域");
        Log.d(TAG, "x: " + fieldRect.left + ", y: " + fieldRect.top + ", width: " + fieldRect.width() + ", height: " + fieldRect.height());

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

        // パッドを更新する
        pad.update();

        // ボール削除用のリストをクリア
        deactiveBalls.clear();

        // ボールごとに更新／当たり判定
        for(int i = activeBalls.size()-1; i>=0; i--) {
            Ball ball = activeBalls.get(i);
            ball.update();

            /**
             * B-10．ブロックの破壊とボールの反射を行う
             *
             */
            /** B-13．ブロックを複数行にする
             *  二次元配列を作る
             */
            for(int row = 0; row < BRICK_ROW; row++) {
                for(int col = 0; col < BRICK_COL; col++) {
                    final Brick brick = bricks[row][col];
                    if(brick.isUnBroken() && ball.isCollided(brick)) {
                        // ブロックと衝突したのでブロックを破壊しボールを反射させる
                        brick.crash();
                        ball.reflect(brick);
                    }
                }
            }

            /**
             * B-07．パッドでボールを反射させる
             * パッドとボールの当たり判定を行う
             */
            if(ball.isCollided(pad)) {
                // パッドと衝突したのでボールを反射させる
                ball.reflect(pad);
            }

            /**
             * B-09．ゲームフィールドでのボールの跳ね返りとボールがゲームフィールド外へ出たかの判定をする
             * 上端はY方向、左右端はX方向で当たり判定を行いボールを反転させる
             * 下端の判定は厳密には行わずゲームフィールドとの交差判定で行う
             */
            final Rect ballRect = ball.getRect();
            // X方向の反射
            if(ballRect.left <= fieldRect.left) {
                // 左端での反射
                ball.setCenter(ball.getRadius(), ball.getCenter().y);
                ball.boundX();
            } else if(ballRect.right >= fieldRect.right) {
                // 右端での反射
                ball.setCenter(fieldRect.right - ball.getRadius(), ball.getCenter().y);
                ball.boundX();
            }

            // Y方向の反射
            if(ballRect.top <= fieldRect.top) {
                // 上端での反射
                ball.setCenter(ball.getCenter().x, ball.getRadius());
                // 速度反転
                ball.boundY();
            }

            if(!ball.getRect().intersects(fieldRect.left, fieldRect.top, fieldRect.right, fieldRect.bottom)) {
                // ボールがゲームフィールド外に出たら、後で消すために削除処理用のリストに登録
                deactiveBalls.add(ball);
            }
        }

        // ゲームフィールド外に出たボールを削除
        for(final Ball ball : deactiveBalls) {
            removeBall(ball);
        }
        deactiveBalls.clear();

        // 総ボール数をチェック
        int ballCount = remainingBallCount + activeBalls.size();
        if(ballCount > 0) {
            // ボール残数あり
            if(getRemainingBricksCount() == 0) {
                // ブロックがなくなった状態
                // ゲームクリア
                Log.d(TAG, "ゲームクリア！おめでとう！！");
                setState(State.CLEAR);
            } else {
                // ブロックがまだ残っている状態
                if(activeBalls.size() == 0) {
                    // ゲームフィールドにボールがなくなったので、新たなボールを払い出す
                    addBall(fieldRect.width()/2, fieldRect.height()/2);
                }
            }
        } else if(ballCount == 0) {
            // ボールの残数がなくなった状態
            // ゲームオーバー
            Log.d(TAG, "残念。ゲームオーバーだよ。");
            setState(State.GAMEOVER);
        }
        // View#invalidateメソッドを呼び再描画を要求する
        view.invalidate();
    }

    /**
     * パッドを取得する
     *
     * @return パッド
     */
    public Pad getPad() {
        return pad;
    }

    /**
     * パッドの色を設定する
     *
     * @return パッドの色
     */
    public int getPadColor() {
        return pad.getColor();
    }

    /**
     * パッドの色を設定する
     *
     * @param color パッドに設定する色
     */
    public void setPadColor(final int color) {
        pad.setColor(color);
    }

    /**
     * パッドの大きさをゲームフィールドの大きさから調整する
     */
    private void adjustPad() {
        int padWidth = fieldRect.width()/6;
        int padHeight = fieldRect.height()/100;
        pad.setRect(new Rect(pad.left(), pad.top(), pad.left() + padWidth, pad.top() + padHeight));
    }

    /**
     * パッドを初期位置に配置する
     */
    public void initializePad() {
        adjustPad();

        int padX = (fieldRect.width() - pad.getWidth())/2;
        int padY = fieldRect.height() - 10*pad.getHeight();
        pad.setRect(new Rect(padX, padY, padX + pad.getWidth(), padY + pad.getHeight()));
        view.addDrawingItem(pad);
    }

    /**
     * パッドの位置(中心座標)を取得する
     *
     * @return パッドの中心座標
     */
    public Point getPadPosition() {
        return pad.getCenter();
    }

    /**
     * パッドを移動させる
     *
     * @param x パッドの中心座標(X座標)
     * @param y パッドの中心座標(Y座標)
     */
    public void movePad(int x, int y) {
        pad.setCenter(x, y);
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
            activeBalls.add(ball);
            view.addDrawingItem(ball);
            remainingBallCount--;
            ret = true;
        }

        return ret;
    }

    /**
     * ボールをゲームフィールドから取り除く
     *
     * @param ball ゲームフィールドから削除するボール
     */
    private void removeBall(Ball ball) {
        activeBalls.remove(ball);
        view.removeDrawingItem(ball);
    }

     /**
     * ボールの状態を初期化する
     */
    private void initializeBall() {
        // ボール残数初期化
        remainingBallCount = DEFAULT_REMAINING_BALLS;

        // ゲームフィールドにあるボールをクリア
        activeBalls.clear();
    }

    /**
     * ブロックを生成する
     */
    private void createBrick() {
        /** B-13．ブロックを複数行にする
         *  二次元配列を作る
         */
        for(int row = 0; row < BRICK_ROW; row++) {
            for(int col = 0; col < BRICK_COL; col++) {
                bricks[row][col] = new BrickNormal();
            }
        }
    }

    /**
     * ブロックを初期位置に配置する
     * ブロックの位置と大きさをゲームフィールドの大きさから調整する
     */
    public void initializeBrick() {
        // ゲームフィールドの大きさから1つあたりのブロックの大きさを設定する
        int brick_w = fieldRect.width() / BRICK_COL;
        int brick_h = fieldRect.height() / 30;

        /** B-13．ブロックを複数行にする
         *  二次元配列を作る
         */
        for(int row = 0; row < BRICK_ROW; row++) {
            for(int col = 0; col < BRICK_COL; col++) {
                final Brick brick = bricks[row][col];
                brick.setSize(brick_w, brick_h);
                /** B-13．ブロックを複数行にする
                 *  複数行にした場合の表示位置を調整する
                 */
                brick.move(col * brick_w, row * brick_h + BRICK_UPPER_SPACE);
                view.addDrawingItem(brick);
            }
        }
    }

    /**
     * 残ボール数の取得
     *
     * @return 残ボール数
     */
    public int getRemainingBallCount() {
        return remainingBallCount;
    }

    /**
     * 残ブロック数取得
     *
     * @return 残ブロック数
     */
    public int getRemainingBricksCount() {
        int count = 0;
        /** B-13．ブロックを複数行にする
         *  二次元配列を作る
         */
        for(int row = 0; row < BRICK_ROW; row++) {
            for(int col = 0; col < BRICK_COL; col++) {
                final Brick brick = bricks[row][col];
                if(brick.isUnBroken()) {
                    ++count;
                }
            }
        }
        return count;
    }
}
