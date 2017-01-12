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
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickSpecial(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brickType = BrickType.Special;
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        return Color.BLUE;
    }
}