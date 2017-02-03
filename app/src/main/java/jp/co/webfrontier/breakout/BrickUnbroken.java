package jp.co.webfrontier.breakout;

import android.graphics.Color;

/** A-02. 壊れないブロックを作る
 * Brickを継承して新たな壊れないブロックのクラスを作成
 * crashメソッドをオーバーライドして、ブロックが壊れないようにする
 * 偶数行、偶数列の位置に壊れないブロックを配置する
 * 残りブロック数のカウントに壊れないブロックを含まないようにする
 */
public class BrickUnbroken extends Brick {

    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "BrickUnbroken";

    /**
     * ブロックの色
     *
     */
    private static int COLOR = Color.WHITE;

    /**
     * コンストラクタ
     *
     */
    public BrickUnbroken() {
        super();
        initialize();
    }

    /**
     * コンストラクタ
     *
     * @param x ブロック位置(X座標)
     * @param y ブロック位置(Y座標)
     */
    public BrickUnbroken(int x, int y) {
        super(x, y);
        initialize();
    }

    /**
     * ブロックを初期化する
     *
     */
    public void initialize() {
        // ブロック種別
        type = Type.UNBROKEN;

        // ペインターへ色設定
        painter.setColor(COLOR);
    }

    /**
     * ブロックを破壊する
     *
     */
    @Override
    public void crash() {}
}
