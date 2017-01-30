package jp.co.webfrontier.breakout;

import android.graphics.Color;

/** S-02. ボーナスブロックの追加
 * ブロック（ボーナス）
 */
public class BrickBonus extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickBonus";

    public BrickBonus() {
        super();

        // ブロックの種別を上書きする
        type = Type.BONUS;

        // ペインターへ色設定
        color = Color.MAGENTA;
        painter.setColor(color);
    }

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public BrickBonus(int x, int y) {
        super(x, y);

        // ブロックの種別を上書きする
        type = Type.BONUS;

        // ペインターへ色設定
        color = Color.MAGENTA;
        painter.setColor(color);
    }
}