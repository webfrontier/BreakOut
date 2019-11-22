package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
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
     * 初速度
     */
    public static final float INITIAL_SPEED = 5f;
    /**
     * 最初の角度(Y方向移動量1に対してのX方向移動量)
     */
    public static final float INITIAL_ANGLE = -0.5f;
    /**
     * 最初のY方向
     */
    public static final float INITIAL_Y_DIRECTION = 1f;

    /**
     * ボールの最大速度
     */
    private static final float MAX_SPEED = 12f;
    /**
     * ボールの最大角度(Y方向移動量1に対してのX方向移動量)
     */
    private static final float MAX_ANGLE = 4f;
    /**
     * ボールの速度変化率
     */
    private static final float CHANGE_RATE_SPEED = 1.5f;

    /**
     * デフォルトの半径
     */
    public static final int DEFAULT_RADIUS = 16;
    /**
     * 半径
     */
    private int r;
    /**
     * 速度
     */
    private float speed = INITIAL_SPEED;
    /**
     * 角度(Y方向移動量1に対してのX方向移動量)
     */
    private float angle = INITIAL_ANGLE;
    /**
     * Y方向
     */
    private float yDirection = INITIAL_Y_DIRECTION;
    /**
     * X移動量
     */
    private float xMovement = calcXMovement();
    /**
     * Y移動量
     */
    private float yMovement = calcYMovement();

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
     * @param speed ボールの速度
     * @param angle ボールの角度
     */
    public Ball(int x, int y, float speed, float angle) {
        painter.setColor(color);
        painter.setAntiAlias(true);
        this.center.x = x;
        this.center.y = y;
        this.speed = speed;
        this.angle = angle;
        this.r = DEFAULT_RADIUS;
        this.rect.set(this.center.x - this.r, this.center.y - this.r, this.center.x + this.r, this.center.y + this.r);
    }

    /**
     * コンストラクタ
     *
     * @param x ボールの位置(X座標)
     * @param y ボールの位置(Y座標)
     * @param speed ボールの速度
     * @param angle ボールの角度
     * @param r ボールの半径
     */
    public Ball(int x, int y, int r, float speed, float angle) {
        painter.setColor(color);
        painter.setAntiAlias(true);
        this.center.x = x;
        this.center.y = y;
        this.r = r;
        this.speed = speed;
        this.angle = angle;
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
     * ボールの速度を取得する(getter)
     *
     * @return ボールの速度
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * ボールの角度を取得する(getter)
     *
     * @return ボールの角度
     */
    public float getAngle() {
        return angle;
    }

    /**
     * ボールのY方向を取得する(getter)
     *
     * @return ボールのY方向
     */
    public float getYDirection() {
        return yDirection;
    }

    /**
     * ボールの状態の更新を行う
     * Item#updateメソッドをオーバーライドして、ボール独自の更新処理を実装する
     * 速度など状況に応じて次のフレームで表示するボールの状態(位置、色、大きさなど)に更新する
     * この処理ではブロックおよびパッドでの反射は考慮しない
     */
    @Override
    public void update() {
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
        /**
         * 反射した後のボールの移動速度を変更する
         * 最大速度、速度変化率の利用
         *
         */

        // 当たる位置によりX方向の反射角を変える
        float changeAngle = angle + (getCenter().x - item.getCenter().x) / 20;

        // 角度に制限をつける
        if(changeAngle > MAX_ANGLE) {
            changeAngle = MAX_ANGLE;
        } else if(angle < -MAX_ANGLE) {
            changeAngle = -MAX_ANGLE;
        }
        updateAngle(changeAngle);

        boundY();

        Log.d(TAG,"speed:" + speed + " angle:" + angle);
        Log.d(TAG,"xMovement:" + xMovement + " yMovement:" + yMovement);
    }

    /**
     * X方向の反射処理を行う
     */
    public void boundX() {
        updateAngle(-angle);
    }

    /**
     * Y方向の反射処理を行う
     */
    public void boundY() {
        updateYDirection(-yDirection);
    }

    /**
     * X方向の移動量算出する
     *
     *
     * @return x方向の移動量
     */
    private float calcXMovement() {
        return Math.abs(calcYMovement()) * angle;
    }

    /**
     * Y方向の移動量算出する
     *
     * @return y方向の移動量
     */
    private float calcYMovement() {
        float absAngle = Math.abs(angle);
        float yMovementSquare = speed * speed / ((absAngle * absAngle) + 1);
        return ((float) Math.sqrt(yMovementSquare))* yDirection;
    }

    /**
     * 角度を更新する
     *
     * @param angle
     */
    private void updateAngle(float angle) {
        if (this.angle != angle) {
            this.angle = angle;
            xMovement = calcXMovement();
            yMovement = calcYMovement();
        }
    }

    /**
     * 速度を更新する
     *
     * return y方向の移動量
     */
    private void updateSpeed(float speed) {
        if (this.speed != speed) {
            this.speed = speed;
            xMovement = calcXMovement();
            yMovement = calcYMovement();
        }
    }

    /**
     * Y方向を更新する
     *
     * @param yDirection Yの方向
     */
    private void updateYDirection(float yDirection) {
        if(this.yDirection != yDirection) {
            this.yDirection = yDirection;
            yMovement = calcYMovement();
        }
    }
}