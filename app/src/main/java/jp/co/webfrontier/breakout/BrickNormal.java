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

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickNormal(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brickType = BrickType.Normal;
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        return Color.GRAY;
    }
}