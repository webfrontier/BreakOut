package jp.co.webfrontier.breakout;

import android.graphics.Canvas;

/**
 * ブロックを表す基底クラス
 * 表示要素なのでItemクラスを継承する
 */
abstract class Brick extends Item {
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
        BLANK("なし", 0),
        /**
         * 通常のブロック
         */
        NORMAL("通常", 1),
        /**
         * 破壊不可のブロック
         */
        UNBROKEN("破壊不可", 2),
        /** S-02. ボーナスブロックの追加
         * ボーナスアイテムを出すブロック
         */
        BONUS("ボーナス", 3);

        /**
         * 種別名
         */
        private final String name;

        /**
         * 種別値
         */
        private final int value;

        /**
         * コンストラクタ
         *
         * @param value 種別値
         */
        private Type(final String name, final int value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * ブロックの種別名を取得する
         *
         * @return 種別名
         */
        String getName() { return name; }

        /**
         * ブロックの種別値を取得する
         *
         * @return 種別値
         */
        int getValue()
        {
            return value;
        }
    }
    /**
     * ブロックの種別
     */
    protected Type type = Type.BLANK;

    /**
     * 破壊されたかどうか
     */
    private boolean broken = false;

    /**
     * コンストラクタ
     *
     */
    public Brick() {}

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public Brick(int x,int y) {
        this.rect.set(x, y, x + WIDTH, y + HEIGHT);
        center.x = x + WIDTH/2;
        center.y = y + HEIGHT/2;
    }

    /**
     * ブロックを移動する
     *
     * @param x ゲームフィールド上のX座標
     * @param y ゲームフィールド上のY座標
     */
    public void move(int x, int y) {
        if(x < 0 || y < 0)
            return;

        rect.set(x, y, x + rect.width(), y + rect.height());
    }

    /**
     * ブロックの大きさを設定する
     *
     * @param w ブロックの幅
     * @param h ブロックの高さ
     */
    public void setSize(int w, int h) {
        if(w < 0 || h < 0)
            return;

        rect.set(rect.left, rect.top, rect.left + w, rect.top + h);
    }

    /**
     * ブロックの描画を行う
     * Item#drawメソッドをオーバーライドして、ブロック独自の描画処理を実装する
     *
     * @param canvas 描画するキャンバス
     * @param x 描画を開始する座標(X座標)
     * @param y 描画を開始する座標(Y座標)
     */
    @Override
    public void draw(Canvas canvas, int x, int y) {
        if(broken) {
            return;
        }
        canvas.drawRect(x + rect.left, y + rect.top, x + rect.right - SPACE, y + rect.bottom - SPACE, painter);
    }

    /**
     * ブロックを破壊する
     *
     */
    public void crash() {
        broken = true;
    }

    /**
     * ブロックが破壊されているか
     *
     * @return true  破壊されている
     * @return false 破壊されていない
     */
    public boolean isUnBroken()
    {
        return !broken;
    }

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