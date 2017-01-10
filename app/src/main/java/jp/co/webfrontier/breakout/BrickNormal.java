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

    private static final int BRICK_NORMAL_POINT = 1;

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickNormal(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brikeType = BrickType.Normal;
        // ブロック強度初期化
        super.robustness = 1; // [Task 14] ブロック耐久性
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        switch(super.robustness) {
            case 2:
                return Color.RED;
            case 1:
                return Color.GRAY;
            case 0:
            default:
                return Color.BLACK;
        }
    }

    @Override
    protected int getPoint() {
        return BRICK_NORMAL_POINT;
    }
}