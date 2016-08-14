package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 表示部品インタフェースクラス
 */
public interface DrawableItem {
    /**
     * 描画処理
     *
     * @param canvas キャンバス
     */
    void draw(Canvas canvas);

    /**
     * 描画領域取得
     *
     * @return 描画領域
     */
    Rect getRect();
}