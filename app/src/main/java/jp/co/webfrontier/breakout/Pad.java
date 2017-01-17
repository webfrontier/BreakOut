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
     * パッドの色(BLE未接続状態)
     */
    private static final int BLE_DISCONNECTED_COLOR = Color.YELLOW;
    /**
     * パッドの色(BLE接続状態)
     */
    private static final int BLE_CONNECTED_COLOR = Color.BLUE;

    /**
     * パッドの領域
     */
    private Rect rect = new Rect();
    /**
     * ボールを打ち返した回数
     */
    private int hitCount = 0;
    /**
     * パッドを描画するView
     */
    private View v;
    /**
     * BLEの接続状態
     */
    private boolean BLEConnected = false;
    /**
     * ペインタ
     */
    private Paint painter = new Paint();

    /**
     * コンストラクタ
     */
    public Pad() {
        this.rect.setEmpty();
        // ペインタへ色設定
        painter.setColor(BLE_DISCONNECTED_COLOR);
    }

    /**
     * BLEの接続状態を設定する(setter)
     *
     * @param connect BLE接続状態
     */
    public void setBLEConnected(boolean connect) {
        BLEConnected = connect;
        painter.setColor(BLEConnected ? BLE_CONNECTED_COLOR : BLE_DISCONNECTED_COLOR);
    }

    /**
     * タッチ位置設定
     *
     * @param x タッチX座標
     */
    public void setPadCx(float x) {
    }

    /**
     * タッチ位置設定
     *
     * @param d パッド移動変化値
     */
    public void setPadDelta(double d) {
    }

    public void setRect(Rect newRect) {
        rect.set(newRect);
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
     * 速度や当たり判定などの状況に応じて次のフレームでボールを表示する座標に更新する
     *
     */
    @Override
    public void update() {}

    /**
     * パッドの描画処理を行う
     * DrawableItemインターフェースの実装
     * Viewクラスのinvalidateメソッドが呼ばれるとシステムからこのメソッドが呼ばれる
     *
     * @param canvas 描画キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, painter);
    }
}