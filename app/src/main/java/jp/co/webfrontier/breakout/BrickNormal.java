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
     * @param x ブロックのX座標
     * @param y ブロックのY座標
     */
    public BrickNormal(int x, int y) {
        super(x, y);

        // ブロックの種別を上書きする
        super.type = Type.Normal;
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