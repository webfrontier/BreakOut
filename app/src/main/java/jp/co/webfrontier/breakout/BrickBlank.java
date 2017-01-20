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

    public BrickBlank() {
        super();

        // ブロックの種別を上書きする
        type = Type.BLANK;

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
    public BrickBlank(int x, int y) {
        super(x, y);

        // ブロックの種別を設定する
        type = Type.BLANK;

        // ペインターへ色設定
        this.color = Color.BLACK;
        painter.setColor(color);
    }
}