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

    /** A-05. ゲームの得点を表示する
     * 得点表示用のUI部品(TextView)を配置する
     * ブロックの耐久度ごとに破壊したときに得られる得点を決める
     * 得点を加算していき表示する
     */
    /**
     * ブロックを破壊したときに得られる得点を取得する
     *
     * @return 得点
     */
    @Override
    public int getPoint() {
        return 0;
    }
}