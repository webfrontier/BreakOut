package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * ブロック基底クラス
 */
abstract class Brick implements DrawableItem {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "Brick";
    /**
     * ブロック強度（破壊不可）
     */
    public static final int UNBREAKABLE = -1;
    /**
     * ブロック強度<br>
     *     ブロックが壊れるまでのボールヒット回数
     */
    protected int robustness = 1;
    /**
     * ブロックX座標（左）
     */
    protected int x;
    /**
     * ブロックY座標（上）
     */
    protected int y;
    /**
     * ブロックX座標（右）
     */
    protected int lx;
    /**
     * ブロックY座標（下）
     */
    protected int ly;
    /**
     * ブロック幅
     */
    public static int WIDTH;
    /**
     * ブロック高さ
     */
    public static int HEIGHT;

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public Brick(int x,int y) {
        this.x = x;
        this.y = y;
        this.ly = y + HEIGHT;
        this.lx = x + WIDTH;
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        if(isUnbroken()) {
            Paint paint = new Paint();
            paint.setColor(getColor());
            canvas.drawRect(x, y, lx - 1, ly - 1, paint);
        }
    }

    /**
     * 描画領域取得
     *
     * @return 描画領域
     */
    @Override
    public Rect getRect() {
        return(new Rect(x,y,lx,ly));
    }

    /**
     * ブロック破壊
     *
     * @param view ブロック描画オブジェクト
     */
    public void crash(View view) {
        if(robustness > 0) {
            --robustness;
        }
        view.invalidate(getRect());
    }

    /**
     * ブロックが壊れていないか
     *
     * @return true  未破壊
     * @return false 破壊済み
     */
    public boolean isUnbroken() {
        return (robustness == Brick.UNBREAKABLE || robustness > 0);
    }

    /**
     * 破壊可能か
     *
     * @return true  未破壊
     * @return false 破壊済み／破壊不可
     */
    public boolean isBreakable() {
        return (robustness > 0);
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    abstract protected int getColor();

    /**
     * 静的定義初期化
     *
     * @param w ブロック幅
     * @param h ブロック高さ
     */
    static public void Initialize(int w, int h) {
        Brick.WIDTH = w;
        Brick.HEIGHT = h;
    }
}