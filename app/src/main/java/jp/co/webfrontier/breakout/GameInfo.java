package jp.co.webfrontier.breakout;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

/**
 * ゲーム情報
 */
public class GameInfo {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "GameInfo";
    /**
     * ブロック行数
     */
    public static final int BRICK_ROW = 4;
    /**
     * ブロック列数
     */
    public static final int BRICK_COL = 8;
    /**
     * モード定義
     */
    public static final int READY    = 0;   // スタート画面
    public static final int RUNNING  = 1;   // 実行中
    public static final int PAUSE    = 2;   // 一時停止中
    public static final int GAMEOVER = 3;   // GameOver
    public static final int CLEAR    = 4;   // クリア
    /**
     * Viewに対するゲーム領域上部の高さ<br>
     * （ステータスバーの高さ）
     */
    private static int UPPER_SPACE;
    /**
     * フィールドサイズ（画面サイズ）
     */
    private int disp_w; // 幅
    private int disp_h; // 高さ
    /**
     * ゲームモード（状態）
     */
    private int mode;
    /**
     * ブロック情報
     */
    public Brick[][] mBricks = new Brick[BRICK_COL][BRICK_ROW];
    /**
     * パッド情報
     */
    private Pad mPad;
    /**
     * ボール残数
     */
    public int mStockBallCount;
    /**
     * ボール情報
     */
    private ArrayList<Ball> mBalls = new ArrayList<Ball>();

    /**
     * コンストラクタ
     *
     * @param width 描画領域の幅
     * @param height 描画領域の高さ
     * @param upper_space ゲーム領域の上の高さ
     */
    GameInfo(int width, int height, int upper_space) {
        disp_w = width;
        disp_h = height;
        UPPER_SPACE = upper_space;

        init();
    }

    /**
     * 初期化処理（外部用）
     */
    public void init() {
        init(disp_w, disp_h);
    }

    /**
     * 初期化処理（内部用）
     * @param w 描画領域の幅
     * @param h 描画領域の高さ
     */
    private void init(int w, int h) {
        // ボール描画領域設定
        Ball.setFieldRect(new Rect(0, UPPER_SPACE, disp_w, disp_h - UPPER_SPACE));
        // ボール残数初期化
        mStockBallCount = 5;
        // ボール情報クリア
        mBalls.clear();
        // パッド情報生成
        mPad = new Pad(w, h);
        // ブロックサイズ算出
        int brick_w = disp_w / BRICK_COL;
        int brick_h = disp_h / 50;
        // ブロックサイズ設定
        Brick.Initialize(brick_w, brick_h);
        for(int col = 0; col < BRICK_COL; col++) {
            for(int row = 0; row < BRICK_ROW; row++) {
                // ブロック情報生成
                mBricks[col][row] = new BrickNormal(col * brick_w, row * brick_h + UPPER_SPACE);
            }
        }
    }

    /**
     * モード設定
     *
     * @param mode 次のモード
     */
    public void setMode(int mode) {
        Log.i(TAG, "setmode(" + this.mode + " -> " + mode + ")");
        this.mode = mode;
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
     * 更新処理
     *
     * @param view 描画オブジェクト
     * @return 0以上:ボール残数／-1:ゲーム実行中以外
     */
    public int update(View view) {
        if(mode == GameInfo.RUNNING) {
            // パッド表示更新
            mPad.update(view);

            int xCrash; // ブロックとの当たり判定（X方向）
            int yCrash; // ブロックとの当たり判定（Y方向）
            // ボールごとに表示更新／当たり判定
            for(Ball ball : mBalls) {
                xCrash = 0;
                yCrash = 0;
                // ボール表示更新
                ball.update(view);

                // ブロックとボールの当たり判定（左上／右上／左下／右下）
                // 各点がどのブロックと当たっているか判定
                // 当たっていない場合はnull返却
                SetXY l_top    = calcBrickIndex(ball.getx(),  ball.gety());
                SetXY r_top    = calcBrickIndex(ball.getlx(), ball.gety());
                SetXY l_bottom = calcBrickIndex(ball.getx(),  ball.getly());
                SetXY r_bottom = calcBrickIndex(ball.getlx(), ball.getly());

                // X方向／Y方向それぞれの当たり判定箇所を集計
                // 相反する箇所の当たり判定は相殺する。
                if(isHitBricks(l_top)) {
                    xCrash++;
                    yCrash++;
                }
                if(isHitBricks(r_top)) {
                    xCrash--;
                    yCrash++;
                }
                if(isHitBricks(l_bottom)) {
                    xCrash++;
                    yCrash--;
                }
                if(isHitBricks(r_bottom)) {
                    xCrash--;
                    yCrash--;
                }
                // 1回の当たりで多重処理しないため、同一ブロックをまとめる。
                List<SetXY> hitBricks = new ArrayList<SetXY>();
                hitBricks = optimumList(hitBricks, l_top);
                hitBricks = optimumList(hitBricks, r_top);
                hitBricks = optimumList(hitBricks, l_bottom);
                hitBricks = optimumList(hitBricks, r_bottom);
                // 当たり判定対象ブロックの表示更新
                for(SetXY target : hitBricks) {
                    mBricks[target.col][target.row].crash(view);
                }

                // ブロックとの反射
                if(yCrash < 0 || yCrash > 0) {
                    ball.boundY();
                }
                if(xCrash < 0 || xCrash > 0) {
                    ball.boundX();
                }

                // パッドとの反射処理
                if(mPad.isBallHit(ball)) {
                    ball.hitPad(mPad.getcx());
                }

                // ボールロスト
                if(ball.isLost()) {
                    mBalls.remove(ball);
                }
            }

            // ボール残総数を返却
            return mStockBallCount + mBalls.size();
        } else {
            view.invalidate();
            return -1;
        }
    }

    /**
     * ボールをフィールドへ追加
     *
     * @return true:成功／false:失敗
     */
    public boolean addBall() {
        boolean ret = false;

        // 「ゲーム実行中」かつボール残数があるときのみ、フィールドへボールを追加
        if(mode == GameInfo.RUNNING && mStockBallCount > 0) {
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
        return (index != null && mBricks[index.col][index.row].isUnbroken());
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
            for(int row = 0; row < BRICK_ROW; row++) {
                // 破壊可能なブロック数のみ集計
                if(mBricks[col][row].isBreakable()) {
                    ++count;
                }
            }
        }
        return count;
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    public void draw(Canvas canvas) {
        // パッド描画
        mPad.draw(canvas);

        // ボール描画
        for(Ball ball : mBalls) {
            ball.draw(canvas);
        }

        // ブロック描画
        for(int col = 0; col < BRICK_COL; col++) {
            for(int row = 0; row < BRICK_ROW; row++) {
                mBricks[col][row].draw(canvas);
            }
        }
    }

    private static final int BALL_PARAM_SIZE = 4;

    /**
     * 状態復元処理
     *
     * @param state 状態復元データ
     */
    public void restoreState(Bundle state) {
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
        ArrayList<Ball> balls = new ArrayList<Ball>();
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
            Ball ball = (Ball) ballArray.get(index);
            rawArray[BALL_PARAM_SIZE * index    ] = ball.getx();
            rawArray[BALL_PARAM_SIZE * index + 1] = ball.gety();
            rawArray[BALL_PARAM_SIZE * index + 2] = ball.getXSpeed();
            rawArray[BALL_PARAM_SIZE * index + 3] = ball.getYSpeed();
        }
        return rawArray;
    }

    /**
     * ブロック表示インデックス番号算出<br>
     * 座標位置（左上）からブロック配列のインデックス番号を算出する。<br>
     * 存在しない場合はnullを返却する。
     *
     * @param x X座標
     * @param y Y座標
     * @return ブロック配列のインデックス番号
     */
    private SetXY calcBrickIndex(float x, float y) {
        // ブロック全体の表示領域
        Rect blockArea = new Rect(0, UPPER_SPACE, Brick.WIDTH * BRICK_COL, Brick.HEIGHT * BRICK_ROW + UPPER_SPACE);

        SetXY index = null;
        if(blockArea.contains((int)x, (int)y)) {
            index = new SetXY(
                    (int) ((x - blockArea.left) / Brick.WIDTH),
                    (int) ((y - blockArea.top) / Brick.HEIGHT));
            if(!mBricks[index.col][index.row].isUnbroken()) {
                // すでにブロックが破壊済み
                index = null;
            }
        }
        return index;
    }
}
