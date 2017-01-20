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
        type = Type.NORMAL;

        // ペインターへ色設定
        color = Color.GRAY;
        painter.setColor(color);
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
        type = Type.NORMAL;

        // ペインターへ色設定
        color = Color.GRAY;
        painter.setColor(color);
    }
}