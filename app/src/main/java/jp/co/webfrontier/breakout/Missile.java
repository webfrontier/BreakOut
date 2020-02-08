package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;

/** A-07. ボーナスアイテム（ミサイル）の取得
 * ボーナスブロックを破壊するとボーナスアイテムが降ってくる
 * ボーナスアイテムとしてブロックを破壊できるミサイルを作成する
 * タップでミサイルを発射しブロックを破壊できる
 */
/*
 * ボーナスとして取得したミサイルを表すクラス
 * 表示要素なのでItemクラスを継承する
 */
public class Missile extends Item {
    /**
     * アイテムの幅
     */
    public static int WIDTH = 10;
    /**
     * アイテムの高さ
     */
    public static int HEIGHT = 20;
    /**
     * 速度（Y方向）
     */
    private int ySpeed = -5;

    /**
     * コンストラクタ
     *
     */
    public Missile() {}

    /**
     * コンストラクタ
     *
     * @param x アイテムx位置
     * @param y アイテムy位置
     */
    public Missile(int x, int y) {
        color = Color.WHITE;
        painter.setColor(color);
        center.x = x;
        center.y = y - HEIGHT / 2;
        this.rect.set(center.x - WIDTH / 2, center.y - HEIGHT / 2, center.x + WIDTH / 2, center.y + HEIGHT / 2);
    }

    /**
     * アイテムの状態の更新を行う
     * Item#updateメソッドをオーバーライドして、アイテム独自の更新処理を実装する
     * 速度など状況に応じて次のフレームで表示するアイテムの状態(位置、色、大きさなど)に更新する
     * この処理ではパッドでの当たり判定は考慮しない
     */
    @Override
    public void update() {
        this.rect.set(rect.left, rect.top + ySpeed, rect.right, rect.bottom + ySpeed);
    }

    /**
     * アイテムの描画を行う
     * Item#drawメソッドをオーバーライドして、ブロック独自の描画処理を実装する
     *
     * @param canvas 描画するキャンバス
     * @param x 描画を開始する座標(X座標)
     * @param y 描画を開始する座標(Y座標)
     */
    @Override
    public void draw(Canvas canvas, int x, int y) {
        canvas.drawRect(x + rect.left, y + rect.top, x + rect.right, y + rect.bottom, painter);
    }
}
