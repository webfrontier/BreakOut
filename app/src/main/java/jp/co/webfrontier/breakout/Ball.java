package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * ボール
 */
public class Ball implements DrawableItem {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Ball";
    /**
     * ボール位置定義
     */
    private static final int LEFT_TOP   = 0;    // 左上
    private static final int RIGHT_TOP  = 1;    // 右上
    private static final int LEFT_DOWN  = 2;    // 左下
    private static final int RIGHT_DOWN = 3;    // 右下
    /**
     * ボール表示領域
     */
    private static Rect field;
    /**
     * 初期速度（X方向）
     */
    private static final float INITIAL_SPEED_X = -0.2f; // [Task 6] ボール速度（初期速度）
    /**
     * 初期速度（Y方向）
     */
    private static final float INITIAL_SPEED_Y = 3f; // [Task 6] ボール速度（初期速度）
    /**
     * 最大速度（X方向）
     */
    private static final float MAX_SPEED_X     = 5f; // [Task 6] ボール速度（最大速度）
    /**
     * 最大速度（Y方向）
     */
    private static final float MAX_SPEED_Y     = 8f; // [Task 6] ボール速度（最大速度）
    /**
     * 速度変化率（X方向）
     */
    private static final float CHANGE_RATE_SPEED_X = 1.01f; // [Task 6] ボール速度（速度変化率）
    /**
     * 速度変化率（Y方向）
     */
    private static final float CHANGE_RATE_SPEED_Y = 1.2f; // [Task 6] ボール速度（速度変化率）
    /**
     * ボールの色
     */
    private int BALL_COLOR = Color.WHITE;
    /**
     * ボールサイズ
     */
    private static int SIZE = 32;
    /**
     * ボール位置（左）
     */
    private float x;
    /**
     * ボール位置（上）
     */
    private float y;
    /**
     * ボール速度（X方向）
     */
    private float xSpeed = INITIAL_SPEED_X;
    /**
     * ボール速度（Y方向）
     */
    private float ySpeed = INITIAL_SPEED_Y;
    /**
     * パッドに当たった回数
     */
    private int hitCount = 0;
    /**
     * ペインター
     */
    private Paint mBallPaint = new Paint();

    /**
     * コンストラクタ
     *
     * @param x ボール位置X座標
     * @param y ボール位置Y座標
     */
    public Ball(float x, float y) {
        mBallPaint.setColor(BALL_COLOR);
        mBallPaint.setAntiAlias(true);
        this.x = x;
        this.y = y;
    }

    /**
     * コンストラクタ
     *
     * @param x ボール位置X座標
     * @param y ボール位置Y座標
     * @param xSpeed X方向速度
     * @param ySpeed Y方向速度
     */
    public Ball(float x, float y, float xSpeed, float ySpeed) {
        mBallPaint.setColor(BALL_COLOR);
        mBallPaint.setAntiAlias(true);
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        int halfSize = SIZE / 2;
        canvas.drawCircle(x+halfSize, y+halfSize, halfSize, mBallPaint);
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
     * ボール位置（左）X座標取得
     *
     * @return ボール位置（左）X座標
     */
    public float getx() {
        return x;
    }

    /**
     * ボール位置（上）Y座標取得
     *
     * @return ボール位置（上）Y座標
     */
    public float gety() {
        return y;
    }

    /**
     * ボール位置（右）X座標取得
     *
     * @return ボール位置（右）X座標
     */
    public float getlx() {
        return x + SIZE;
    }

    /**
     * ボール位置（下）Y座標取得
     *
     * @return ボール位置（下）Y座標
     */
    public float getly() {
        return y + SIZE;
    }

    /**
     * ボール位置（中央）X座標取得
     *
     * @return ボール位置（中央）X座標
     */
    public float getcx() {
        return x + SIZE / 2;
    }

    /**
     * ボール位置（中央）Y座標取得
     *
     * @return ボール位置（中央）Y座標
     */
    public float getcy() {
        return y + SIZE / 2;
    }

    /**
     * X方向速度取得
     *
     * @return X方向速度
     */
    public float getXSpeed() {
        return xSpeed;
    }

    /**
     * Y方向速度取得
     *
     * @return Y方向速度
     */
    public float getYSpeed() {
        return ySpeed;
    }

    /**
     * 更新処理<br>
     * 速度から次の描画座標を算出し、ボールの再描画を行う。<br>
     * この処理ではブロックでの反射は考慮せず、壁での反射のみ速度へ反映する。<br>
     * 速度に応じて次に表示する座標に更新する。
     *
     * @param view ボール描画オブジェクト
     */
    public void update(View view) {
        view.invalidate(getRect());

        // [Task 8] 壁との当たり判定
        x += xSpeed;
        // X方向判定
        if(x < Ball.field.left) {
            // 壁（左）で反射
            x = Ball.field.left * 2 - x;
            // 速度反転
            boundX();
        } else if(getlx() > Ball.field.right) {
            // 壁（右）で反射
            x = Ball.field.right * 2 - getlx() - SIZE;
            // 速度反転
            boundX();
        }

        y += ySpeed;
        // Y方向判定
        if(y + ySpeed < Ball.field.top) {
            // 壁（上）で反射
            y = Ball.field.top * 2 - y;
            // 速度反転
            boundY();
        }

        view.invalidate(getRect());
    }

    /**
     * パッドでの反射
     *
     * @param pad_cx パッド中央位置
     */
    public void hitPad(float pad_cx) {
        ++hitCount;
        // パッドの当たる位置によりX方向の反射角を変える。
        xSpeed += (getcx() - pad_cx) / 8;

        // X方向
        if(MAX_SPEED_X < Math.abs(xSpeed)) {
            if(xSpeed > 0) {
                xSpeed = MAX_SPEED_X;
            }else{
                xSpeed = -MAX_SPEED_X;
            }
        } else {
            xSpeed *= CHANGE_RATE_SPEED_X;
        }

        // Y方向
        if(MAX_SPEED_Y < Math.abs(ySpeed)) {
            if(ySpeed > 0) {
                ySpeed = MAX_SPEED_Y;
            } else {
                ySpeed = -MAX_SPEED_Y;
            }
        } else {
            ySpeed *= -CHANGE_RATE_SPEED_Y;
        }
    }

    /**
     * 速度アップ
     */
    public void speedUp() {
        // ボールの色変化
        BALL_COLOR = Color.RED;
        mBallPaint.setColor(BALL_COLOR);

        xSpeed *= CHANGE_RATE_SPEED_X * 5;
        ySpeed *= CHANGE_RATE_SPEED_Y * 5;

        // X方向
        if(MAX_SPEED_X < Math.abs(xSpeed)) {
            if(xSpeed > 0) {
                xSpeed = MAX_SPEED_X;
            }else{
                xSpeed = -MAX_SPEED_X;
            }
        }

        // Y方向
        if(MAX_SPEED_Y < Math.abs(ySpeed)) {
            if(ySpeed > 0) {
                ySpeed = MAX_SPEED_Y;
            } else {
                ySpeed = -MAX_SPEED_Y;
            }
        }
    }

    /**
     * Y方向反射
     */
    public void boundX() {
        xSpeed = -xSpeed;
    }

    /**
     * X方向反射
     */
    public void boundY() {
        ySpeed = -ySpeed;
    }

    /**
     * ボール消失判定
     *
     * @return true  画面外に消失
     * @return false 画面内に存在
     */
    public boolean isLost() {
        return !Ball.field.contains((int)getcx(), (int)getcy());
    }

    /**
     * ボール描画領域設定
     *
     * @param rect ボール描画領域
     */
    public static void setFieldRect(Rect rect) {
        Ball.field = rect;
    }
}