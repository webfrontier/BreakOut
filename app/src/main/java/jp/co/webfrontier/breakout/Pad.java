package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

/**
 * 操作パッドを表すクラス
 * 表示部品なのでDrawableItemインターフェースを実装する
 */
public class Pad implements DrawableItem {
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
     * ゲームフィールドの領域
     */
    private static Rect fieldRect = new Rect();

    /**
     * パッドの左上(Left-Top)の座標
     */
    private PointF lt = new PointF();

    /**
     * パッドの中心(Center)座標
     */
    private PointF c = new PointF();

    /**
     * パッドの右下(Right-Bottom)の座標
     */
    private PointF rb = new PointF();
    /**
     * パッドの幅
     */
    private int w;
    /**
     * パッドの高さ
     */
    private int h;
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
        this.w = 0;
        this.h = 0;
        // ペインタへ色設定
        painter.setColor(BLE_DISCONNECTED_COLOR);
    }

    /**
     * パッドを描画するViewを設定する(setter)
     *
     * @param v ビュー
     */
    public void setView(View v) {
        this.v = v;
    }

    /**
     * パッドの左上(Left-Top)の座標を取得する(getter)
     *
     * @return パッドの左上の座標
     */
    public PointF getLTPoint() {
        PointF p = new PointF();
        p.set(lt);
        return p;
    }

    /**
     * パッドの中心座標
     *
     * @return パッドの中央座標を取得する(getter)
     */
    public PointF getCenter() {
        PointF p = new PointF();
        p.set(c);
        return p;
    }

    /**
     * パッドの右下(Right-Bottom)の座標を取得する(getter)
     *
     * @return パッドの右下の座標
     */
    public PointF getRBPoint() {
        PointF p = new PointF();
        p.set(rb);
        return p;
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
     * ゲームフィールドの領域変更を処理する
     *
     * @param rect ゲームフィールドの領域
     */
    public void onGameFieldChanged(Rect rect) {
        Pad.fieldRect.set(rect);

        // パッドの大きさと位置をゲームフィールドの大きさから算出する
        w = Pad.fieldRect.width() / 6;
        h = Pad.fieldRect.height() / 100;
    }

    /**
     * タッチ位置設定
     *
     * @param d パッド移動変化値
     */
    public void setPadDelta(double d) {
    }

    /**
     * パッドの描画領域を取得する
     * DrawableItemインターフェースの実装
     * @return 描画領域
     */
    @Override
    public Rect getRect() {
        // float -> intのキャストを行うため、1ずつ広くサイズを返却する。
        return new Rect(
                (int)this.lt.x - 1,
                (int)this.lt.y - 1,
                (int)this.rb.x + 1,
                (int)this.rb.y + 1
        );
    }

    /**
     * パッドの状態の更新を行う
     * 速度や当たり判定などの状況に応じて次のフレームでボールを表示する座標に更新する
     * 更新後はViewクラスのinvalidateメソッドを呼ぶことで再描画を要求すること
     *
     * @param view パッド描画オブジェクト
     */
    public void update(View view) {
        view.invalidate(getRect());
    }

    /**
     * パッドの描画処理を行う
     * DrawableItemインターフェースの実装
     * Viewクラスのinvalidateメソッドが呼ばれるとシステムからこのメソッドが呼ばれる
     *
     * @param canvas 描画キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(lt.x, lt.y, rb.x, rb.y, painter);
    }

    /**
     * パッドとボールの当たり判定を行う
     *
     * @param ball 判定対象のボール
     *
     * @return true  ボールに当たった
     * @return false ボールに当たってない
     */
    public boolean isBallHit(Ball ball) {
        return false;
    }
}