package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

/**
 * ボールを表すクラス
 * 表示部品なのでDrawableItemインターフェースを実装する
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
     * 初期速度（X方向）
     * [Task 6] ボール速度（初期速度）
     */
    public static final float INITIAL_SPEED_X = -0.2f;
    /**
     * 初期速度（Y方向）
     * [Task 6] ボール速度（初期速度）
     */
    public static final float INITIAL_SPEED_Y = 3f;
    /**
     * デフォルトの色
     */
    public static final int DEFAULT_COLOR = Color.WHITE;
    /**
     * デフォルトの半径
     */
    public static final int DEFAULT_RADIUS = 16;

    /**
     * ゲームフィールドの領域
     */
    private static Rect fieldRect = new Rect();

    /**
     * ボールの中心座標
     */
    private PointF c;
    /**
     * ボールの半径
     */
    private int r;
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
    private Paint painter = new Paint();

    /**
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     */
    public Ball(float x, float y) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.r = DEFAULT_RADIUS;
    }

    /**
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     * @param xSpeed ボールの速度(X座標)
     * @param ySpeed ボールの速度(Y座標)
     */
    public Ball(float x, float y, float xSpeed, float ySpeed) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.r = DEFAULT_RADIUS;
    }

    /**
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     * @param xSpeed ボールの速度(X座標)
     * @param ySpeed ボールの速度(Y座標)
     * @param r ボールの半径
     */
    public Ball(float x, float y, float xSpeed, float ySpeed, int r) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.r = r;
    }

    /**
     * ボールの中心のX座標を取得する(getter)
     *
     * @return ボールの中心座標
     */
    public PointF getCenterPoint() { return c; }

    /**
     * ボールの中心座標を設定する(setter)
     *
     */
    public void setCenterPoint(float x, float y) {
        c.x = x;
        c.y = y;
    }

    /**
     * ボールの半径を取得する(getter)
     *
     * @return ボールの半径
     */
    public int getRadius() { return r; }

    /**
     * ボールの半径を設定する(setter)
     *
     * @param r 設定する半径
     */
    public void setRadius(int r) { this.r = r; }

    /**
     * ボールのX方向の速度を取得する(getter)
     *
     * @return ボールのX方向の速度
     */
    public float getXSpeed() {
        return xSpeed;
    }

    /**
     * ボールのY方向の速度を取得する(setter)
     *
     * @return ボールのY方向の速度
     */
    public float getYSpeed() {
        return ySpeed;
    }

    /**
     * ボールの描画領域を取得する
     * DrawableItemインターフェースの実装
     * @return 描画領域
     */
    @Override
    public Rect getRect() {
        // float -> intのキャストを行うため、1ずつ広くサイズを返却する。
        return new Rect(
                (int)this.c.x - r - 1,
                (int)this.c.y - r - 1,
                (int)this.c.x + r + 1,
                (int)this.c.y + r + 1
        );
    }

    /**
     * ボールの状態の更新を行う
     * 速度や当たり判定などの状況に応じて次のフレームでボールを表示する座標に更新する
     * 更新後はViewクラスのinvalidateメソッドを呼ぶことで再描画を要求すること
     *
     * @param view ボール描画オブジェクト
     */
    public void update(View view) {
        view.invalidate(getRect());
    }

    /**
     * ボールの描画処理を行う
     * DrawableItemインターフェースの実装
     * Viewクラスのinvalidateメソッドが呼ばれるとシステムからこのメソッドが呼ばれる
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(c.x+r, c.y+r, r, painter);
    }

    /**
     * ゲームフィールドの領域を設定する
     *
     * @param rect ゲームフィールドの領域
     */
    public static void onGameFieldChanged(Rect rect) {
        Ball.fieldRect.set(rect);
    }

    /**
     * パッドで反射された場合の処理を行う
     *
     * @param pad_cx パッドの中心座標
     */
    public void hitPad(float pad_cx) {
    }

    /**
     * X方向の反射処理を行う
     */
    public void boundX() {
        xSpeed = -xSpeed;
    }

    /**
     * Y方向の反射処理を行う
     */
    public void boundY() {
        ySpeed = -ySpeed;
    }

    /**
     * ボールが画面外に出たかを判定する
     *
     * @return true  ボールが画面外に出た
     * @return false ボールが画面内に存在する
     */
    public boolean isLost() {
        return false;
    }
}