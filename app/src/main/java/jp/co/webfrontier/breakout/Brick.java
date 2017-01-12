package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

/**
 * ブロックを表す基底クラス
 * 表示部品なのでDrawableItemインターフェースを実装する
 */
abstract class Brick implements DrawableItem {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Brick";
    /**
     * ブロック間のスペース
     */
    public static final int SPACE = 5;
    /**
     * ブロックの幅
     */
    public static int WIDTH;
    /**
     * ブロックの高さ
     */
    public static int HEIGHT;

    /**
     * ブロックの種別
     */
    public enum Type {
        /**
         * ブロックがない
         */
        Blank(0),
        /**
         * 通常のブロック
         */
        Normal(1),
        /**
         * 破壊不可のブロック
         */
        Unbroken(2),
        /**
         * スペシャルブロック
         */
        Special(3);

        /**
         * 種別値
         */
        int value = 0;

        /**
         * コンストラクタ
         *
         * @param value 種別値
         */
        private Type(int value)
        {
            this.value = value;
        }

        /**
         * ブロックの種別を取得する
         *
         * @return 種別
         */
        int getValue()
        {
            return value;
        }
    }
    /**
     * ブロックの種別
     */
    protected Type type = Type.Blank;

    /**
     * ブロックの左上(Left-Top)の座標
     */
    private PointF lt = new PointF();

    /**
     * ブロックの中心(Center)座標
     */
    private PointF c = new PointF();

    /**
     * ブロックの右下(Right-Bottom)の座標
     */
    private PointF rb = new PointF();

    /**
     * ペインター
     */
    private Paint painter = new Paint();

    /**
     * ブロックの大きさを設定する
     *
     * @param w ブロックの幅
     * @param h ブロックの高さ
     */
    static public void setSize(int w, int h) {
        Brick.WIDTH = w;
        Brick.HEIGHT = h;
    }

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public Brick(int x,int y) {
        this.lt.x = x;
        this.lt.y = y;
        this.rb.x = x + WIDTH;
        this.rb.y = y + HEIGHT;
        painter.setColor(getColor());
    }

    /**
     * ブロックの描画処理を行う
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        if(isUnbroken()) {
            canvas.drawRect(lt.x, lt.y, rb.x - SPACE, rb.y - SPACE, painter);
        }
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
     * ブロック破壊
     *
     * @param view ブロック描画オブジェクト
     */
    public void crash(View view) {
        view.invalidate(getRect());
    }

    /**
     * ブロックが壊れていないかをチェックする
     *
     * @return true  未破壊
     * @return false 破壊済み
     */
    public boolean isUnbroken() {
        return true;
    }

    /**
     * ブロックの色を取得する(getter)
     *
     * @return ブロックの色
     */
    abstract protected int getColor();

    /**
     * ブロックの種別を取得する
     *
     * @return ブロックの種別
     */
    public Type getType()
    {
        return type;
    }
}