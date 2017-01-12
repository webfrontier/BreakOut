package jp.co.webfrontier.breakout;

import android.graphics.Color;
import android.view.View;

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

        // ブロックの種別を上書きする
        super.type = Type.Unbroken;
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

    /**
     * ブロック破壊
     *
     * @param view ブロック描画オブジェクト
     */
    public void crash(View view) {
        super.crash(view);
    }
}