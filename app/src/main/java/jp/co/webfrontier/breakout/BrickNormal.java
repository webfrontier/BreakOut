package jp.co.webfrontier.breakout;

import android.graphics.Color;

/**
 * ブロック（通常）
 */
public class BrickNormal extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickNormal";

    public BrickNormal() {
        super();

        // ブロックの種別を上書きする
        super.type = Type.NORMAL;
    }

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public BrickNormal(int x, int y) {
        super(x, y);

        // ブロックの種別を上書きする
        super.type = Type.NORMAL;
    }

    /**
     * ブロックの色を取得する(getter)
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        return Color.GRAY;
    }
}