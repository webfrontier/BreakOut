package jp.co.webfrontier.breakout;

import android.graphics.Color;

/**
 * ブロック（破壊不可） [Task 15] 壊れないブロック
 */
public class BrickUnbroken extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickUnbroken";

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickUnbroken(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brikeType = BrickType.Unbroken;
        // ブロック強度初期化
        super.robustness = UNBREAKABLE;
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        return Color.WHITE;
    }
}