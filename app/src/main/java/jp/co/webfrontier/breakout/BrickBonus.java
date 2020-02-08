package jp.co.webfrontier.breakout;

import android.graphics.Color;

/** A-06. ボーナスブロックの追加
 * ボーナスアイテムを出すブロック
 * Brickを継承して作成する
 */
public class BrickBonus extends Brick {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickBonus";

    public BrickBonus() {
        super();
        initialize();
    }

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public BrickBonus(int x, int y) {
        super(x, y);
        initialize();
    }

    /**
     * ブロックを初期化する
     *
     */
    public void initialize() {
        // ブロックの種別を上書きする
        type = Type.BONUS;

        // ペインターへ色設定
        color = Color.MAGENTA;
        painter.setColor(color);
    }

    @Override
    public int getPoint() {
        return 50;
    }
}
