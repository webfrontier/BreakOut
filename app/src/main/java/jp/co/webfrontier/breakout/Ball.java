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
     * ボールの最大速度（X方向）
     */
    private static final float MAX_SPEED_X = 5f;
    /**
     * ボールの最大速度（Y方向）
     */
    private static final float MAX_SPEED_Y = 8f;
    /**
     * ボールの速度変化率（X方向）
     */
    private static final float CHANGE_RATE_SPEED_X = 1.01f;
    /**
     * ボールの速度変化率（Y方向）
     */
    private static final float CHANGE_RATE_SPEED_Y = 1.2f;
    /**
     * デフォルトの半径
     */
    public static final int DEFAULT_RADIUS = 16;
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
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     */
    public Ball(int x, int y) {
        painter.setColor(color);
        painter.setAntiAlias(true);
        this.center.x = x;
        this.center.y = y;
        this.r = DEFAULT_RADIUS;
        this.rect.set(this.center.x - this.r, this.center.y - this.r, this.center.x + this.r, this.center.y + this.r);
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
        painter.setColor(color);
        painter.setAntiAlias(true);
        this.center.x = x;
        this.center.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.r = DEFAULT_RADIUS;
        this.rect.set(this.center.x - this.r, this.center.y - this.r, this.center.x + this.r, this.center.y + this.r);
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
        painter.setColor(color);
        painter.setAntiAlias(true);
        this.center.x = x;
        this.center.y = y;
        this.r = r;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.rect.set(this.center.x - this.r, this.center.y - this.r, this.center.x + this.r, this.center.y + this.r);
    }

    /**
     * ボールの中心座標を設定する(setter)
     *
     * @param x 中心座標(X座標)
     * @param y 中心座標(Y座標)
     */
    public void setCenter(int x, int y) {
        center.x = x;
        center.y = y;
        rect.set(center.x - r, center.y - r, center.x + r, center.y + r);
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
    public void setYSpeed(float ySpeed) { this.ySpeed = ySpeed; }

    /**
     * ボールの状態の更新を行う
     * Item#updateメソッドをオーバーライドして、ボール独自の更新処理を実装する
     * 速度など状況に応じて次のフレームで表示するボールの状態(位置、色、大きさなど)に更新する
     * この処理ではブロックおよびパッドでの反射は考慮しない
     */
    @Override
    public void update() {
        /**
         * B-06．パッドとボールを動かす
         * 当たり判定は考慮せずパッドとボールを動かす
         * フレームの更新(フレームレート60fps)/描画処理などの話をする
         */
        center.x += xSpeed;
        center.y += ySpeed;
        this.rect.set(center.x - r, center.y - r, center.x + r, center.y + r);
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
        canvas.drawCircle(x + center.x, y + center.y, r, painter);
    }

    /**
     * 他の表示要素との反射処理を行う
     *
     * @param item 反射対象の表示要素
     */
    public void reflect(Item item) {
        /** B-08．反射した後のボールの移動速度を変更する
         * 最大速度、速度変化率の利用
         *
         */
        // 当たる位置によりX方向の反射角を変える。
        xSpeed += (getCenter().x - item.getCenter().x) / 8;

        // X方向の速度変化
        // 最大速度の大きさ以下に抑える
        if(MAX_SPEED_X < Math.abs(xSpeed)) {
            if(xSpeed > 0) {
                xSpeed = MAX_SPEED_X;
            }else{
                xSpeed = -MAX_SPEED_X;
            }
        } else {
            xSpeed *= CHANGE_RATE_SPEED_X;
        }

        // Y方向の速度変化
        // 最大速度の大きさ以下に抑える
        if(MAX_SPEED_Y < Math.abs(ySpeed)) {
            if(ySpeed > 0) {
                ySpeed = MAX_SPEED_Y;
            } else {
                ySpeed = -MAX_SPEED_Y;
            }
        } else {
            ySpeed *= CHANGE_RATE_SPEED_Y;
        }
        boundY();
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