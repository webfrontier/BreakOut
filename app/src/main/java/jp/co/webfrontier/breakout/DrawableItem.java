package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 描画用のインタフェース
 * 新しい表示部品を作成する場合はこのインターフェースを実装すること
 */
public interface DrawableItem {
    /**
     * 表示部品の描画処理を行う
     * 表示部品に応じた描画処理を実装すること
     *
     * @param canvas キャンバス
     */
    void draw(Canvas canvas);

    /**
     * 表示部品を描画するための領域を取得する
     * 表示部品に応じた描画領域を返すこと
     *
     * @return 描画領域
     */
    Rect getRect();
}