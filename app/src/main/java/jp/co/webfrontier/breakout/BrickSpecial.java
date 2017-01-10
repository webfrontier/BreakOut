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

    private static final int BRICK_NORMAL_SPECIAL = 3;

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickSpecial(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brikeType = BrickType.Special;
        // ブロック強度初期化
        super.robustness = 1;
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

    @Override
    protected int getPoint() {
        return 0;
    }
}