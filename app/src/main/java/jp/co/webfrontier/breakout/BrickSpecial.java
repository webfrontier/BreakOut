package jp.co.webfrontier.breakout;

import android.graphics.Color;

/**
 * スペシャルブロック
 */
public class BrickSpecial extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickSpecial";

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public BrickSpecial(int x, int y) {
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
        return Color.BLUE;
    }
}