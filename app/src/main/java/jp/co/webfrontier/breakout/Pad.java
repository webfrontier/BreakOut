package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * 操作パッドを表すクラス
 * 表示要素なのでItemクラスを継承する
 */
public class Pad extends Item {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Pad";

    /**
     * コンストラクタ
     */
    public Pad() {
        // ペインターへ色設定
        color = Color.YELLOW;
        painter.setColor(color);
    }

    /**
     * パッドの中心座標を設定する(setter)
     *
     * @param cx 新しい中心座標(X座標)
     * @param cy 新しい中心座標(Y座標)
     */
    public void setCenter(int cx, int cy) {
        int dx = cx - center.x;
        int dy = cy - center.y;
        rect.set(rect.left + dx, rect.top + dy, rect.right + dx, rect.bottom + dy);
        center.x = cx;
        center.y = cy;
    }

    public void setRect(Rect newRect) {
        rect.set(newRect);
        center.x = rect.left + rect.width()/2;
        center.y = rect.top + rect.height()/2;
    }

    public int top() {
        return rect.top;
    }

    public int left() {
        return rect.left;
    }

    public int getWidth() {
        return rect.width();
    }

    public int getHeight() {
        return rect.height();
    }

    /**
     * パッドの状態の更新を行う
     * Item#updateメソッドをオーバーライドして、パッド独自の更新処理を実装する
     * 速度など状況に応じて次のフレームで表示するパッドの状態(位置、色、大きさなど)に更新する
     * この処理ではボールとの反射は考慮しない
     */
    @Override
    public void update() {

    }

    /**
     * パッドの描画処理を行う
     * Item#drawメソッドをオーバーライドして、パッド独自の描画処理を実装する
     *
     * @param canvas 描画するキャンバス
     * @param x 描画を開始する座標(X座標)
     * @param y 描画を開始する座標(Y座標)
     */
    @Override
    public void draw(Canvas canvas, int x, int y) {
        canvas.drawRect(x + rect.left, y + rect.top, x + rect.right, y + rect.bottom, painter);
    }
}