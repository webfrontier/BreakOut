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
    public static final int UNBREAKABLE = -1; // [Task 15] 壊れないブロック
    /**
     * ブロック間のスペース
     */
    public static final int BLOCK_SPACE = 5;
    /**
     * ブロック強度<br>
     *     ブロックが壊れるまでのボールヒット回数
     */
    protected int robustness = 1; // [Task 14] ブロック耐久性
    /**
     * ブロック種別
     */
    protected BrickType brikeType = BrickType.Blank;
    /**
     * ペインター
     */
    private Paint mBrickPaint = new Paint();
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
        mBrickPaint.setColor(getColor());
    }

    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    @Override
    public void draw(Canvas canvas) {
        if(isUnbroken()) {
            canvas.drawRect(x, y, lx - BLOCK_SPACE, ly - BLOCK_SPACE, mBrickPaint);
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
     * ブロック強度設定
     *
     * @param  robustness  強度
     */
    public void setRobustness(int robustness) {
        this.robustness = robustness;
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
     * ブロック種別取得
     *
     * @return ブロック種別
     */
    public BrickType getType()
    {
        return brikeType;
    }

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