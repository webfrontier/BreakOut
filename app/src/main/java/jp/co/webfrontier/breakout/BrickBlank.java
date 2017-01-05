package jp.co.webfrontier.breakout;

import android.graphics.Color;

/**
 * ブロック（なし）
 */
public class BrickBlank extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickBlank";

    /**
     * コンストラクタ
     *
     * @param x ブロック位置X座標
     * @param y ブロック位置Y座標
     */
    public BrickBlank(int x, int y) {
        super(x, y);

        // ブロック種別
        super.brikeType = BrickType.Blank;
        // ブロック強度初期化
        super.robustness = 0;
    }

    /**
     * ブロック色取得
     *
     * @return ブロックの色
     */
    @Override
    protected int getColor()
    {
        return Color.BLACK;
    }
}