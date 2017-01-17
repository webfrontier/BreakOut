package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * ボールを表すクラス
 * 表示部品なのでDrawableItemインターフェースを実装する
 */
public class Ball extends Item {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Ball";

    /**
     * 初速度（X方向）
     */
    public static final float INITIAL_SPEED_X = -0.2f;
    /**
     * 初速度（Y方向）
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
     * 中心の座標
     */
    private Point c = new Point();
    /**
     * 半径
     */
    private int r;
    /**
     * 速度（X方向）
     */
    private float xSpeed = INITIAL_SPEED_X;
    /**
     * 速度（Y方向）
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
    public Ball(int x, int y) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.r = DEFAULT_RADIUS;
        this.rect.set(this.c.x, this.c.y, this.c.x + this.r, this.c.y + this.r);
    }

    /**
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     * @param xSpeed ボールの速度(X座標)
     * @param ySpeed ボールの速度(Y座標)
     */
    public Ball(int x, int y, float xSpeed, float ySpeed) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.r = DEFAULT_RADIUS;
        this.rect.set(this.c.x, this.c.y, this.c.x + this.r, this.c.y + this.r);
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
    public Ball(int x, int y, int r, float xSpeed, float ySpeed) {
        painter.setColor(DEFAULT_COLOR);
        painter.setAntiAlias(true);
        this.c.x = x;
        this.c.y = y;
        this.r = r;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.rect.set(this.c.x, this.c.y, this.c.x + this.r, this.c.y + this.r);
    }

    /**
     * ボールの中心座標を取得する(getter)
     *
     * @return ボールの中心座標
     */
    public Point getCenter() { return c; }

    /**
     * ボールの中心座標を設定する(setter)
     *
     */
    public void setCenter(int x, int y) {
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
     * ボールのX方向の速度を設定する(setter)
     *
     * @param xSpeed ボールのX方向の速度
     */
    public void setXSpeed(float xSpeed) { this.xSpeed = xSpeed; }

    /**
     * ボールのY方向の速度を取得する(setter)
     *
     * @return ボールのY方向の速度
     */
    public float getYSpeed() {
        return ySpeed;
    }

    /**
     * ボールのY方向の速度を設定する(setter)
     *
     * @param ySpeed ボールのX方向の速度
     */
    public void setYSpeed(float ySpeed) { this.xSpeed = ySpeed; }

    public void setPosition(int x, int y) {
        c.x = x;
        c.y = y;
    }

    /**
     * ボールの状態の更新を行う
     * Item#updateメソッドをオーバーライドして、ボール独自の更新処理を実装する
     * 速度など状況に応じて次のフレームで表示するボールの状態(位置、色、大きさなど)に更新する
     * この処理ではブロックおよびパッドでの反射は考慮しない
     */
    @Override
    public void update() {
    }

    /**
     * ボールの描画処理を行う
     * ボールの描画を行う
     * Item#drawメソッドをオーバーライドして、ボール独自の描画処理を実装する
     *
     * @param canvas 描画するキャンバス
     * @param x 描画を開始する座標(X座標)
     * @param y 描画を開始する座標(Y座標)
     */
    @Override
    public void draw(Canvas canvas, int x, int y) {
        canvas.drawCircle(x + c.x + r, y + c.y + r, r, painter);
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
}