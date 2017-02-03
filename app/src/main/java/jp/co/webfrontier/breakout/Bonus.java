package jp.co.webfrontier.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/** A-07. ボーナスアイテム（ミサイル）の取得
 * ボーナスブロックを破壊するとボーナスアイテムが降ってくる
 * ボーナスアイテムとしてブロックを破壊できるミサイルを作成する
 * タップでミサイルを発射しブロックを破壊できる
 */

/*
 * ボーナスアイテムを表す基底クラス
 * 表示要素なのでItemクラスを継承する
 */
public class Bonus extends Item {
    /**
     * アイテムの幅
     */
    public static int WIDTH;
    /**
     * アイテムの高さ
     */
    public static int HEIGHT;
    /**
     * 速度（Y方向）
     */
    private int ySpeed = 5;

    /**
     * アイテムの種別
     */
    public enum Type {
        /**
         * ミサイル
         */
        MISSILE("ミサイル", 1);

        /**
         * 種別名
         */
        private final String name;

        /**
         * 種別値
         */
        private final int value;

        /**
         * コンストラクタ
         *
         * @param value 種別値
         */
        private Type(final String name, final int value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * アイテムの種別名を取得する
         *
         * @return 種別名
         */
        String getName() { return name; }

        /**
         * アイテムの種別値を取得する
         *
         * @return 種別値
         */
        int getValue()
        {
            return value;
        }
    }
    /**
     * アイテムの種別
     */
    protected Bonus.Type type = Type.MISSILE;

    /**
     * コンストラクタ
     *
     */
    public Bonus() {}

    /**
     * コンストラクタ
     *
     * @param rect アイテム位置
     */
    public Bonus(Rect rect) {
        color = Color.GREEN;
        painter.setColor(color);
        center.x = rect.left + rect.width() / 2;
        center.y = rect.top + rect.height();
        WIDTH = rect.width() / 2;
        HEIGHT = rect.height();
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
        canvas.drawRoundRect(x + rect.left, y + rect.top, x + rect.right, y + rect.bottom, 20, 20, painter);
    }

    /**
     * ボーナス種別取得
     * @return ボーナス種別
     */
    public Bonus.Type getBonusType() {
        return type;
    }
}
