package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * ゲームの表示要素の抽象クラス
 * 新しい表示要素を作成する場合はこのクラスを継承すること
 */
public abstract class Item {

    /**
     * 表示要素の更新を行う
     * 継承先のクラスでは表示要素に応じた更新処理を実装すること
     *
     */
    public void update() {}

    /**
     * 表示要素の描画を行う
     * 継承先のクラスでは表示要素に応じた描画処理を実装すること
     *
     * @param canvas 描画するキャンバス
     * @param x 描画を開始する座標(X座標)
     * @param y 描画を開始する座標(Y座標)
     */
    public void draw(Canvas canvas, int x, int y) {}

    /**
     * 衝突判定に使用する領域を取得する
     * 継承先のクラスでは表示要素に応じた処理を実装すること
     *
     * @return 衝突判定領域
     */
    public Rect getRect() { return rect; }

    /**
     * 衝突判定に使用する領域の中心座標を取得する
     *
     * @return 中心座標
     */
    public Point getCenter() { return center; }

    /**
     * 他の表示要素との衝突判定を行う
     *
     * @param target 判定対象となる表示要素
     *
     * @return true  衝突している
     * @return false 衝突していない
     */
    public boolean isCollided(Item target) {
        final Rect r = target.getRect();
        return rect.intersects(r.left, r.top, r.right, r.bottom);
    }

    /**
     * 表示要素の領域
     */
    protected Rect rect = new Rect();

    /**
     * 表示要素の中心座標
     */
    protected Point center = new Point();

    /**
     * ペインター
     */
    protected Paint painter = new Paint();
}