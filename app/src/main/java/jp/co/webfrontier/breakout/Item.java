package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * ゲームの表示要素の抽象クラス
 * 新しい表示要素を作成する場合はこのクラスを継承すること
 */
public abstract class Item {

    /**
     * 表示要素の更新を行う
     * 継承先のクラスでは表示要素に応じた更新処理を実装すること
     *
     */
    void update() {}

    /**
     * 表示要素の描画を行う
     * 継承先のクラスでは表示要素に応じた描画処理を実装すること
     *
     * @param canvas キャンバス
     */
    void draw(Canvas canvas) {}
}