package jp.co.webfrontier.breakout;

/**
 * インデックス
 */
public class SetXY {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "SetXY";
    /**
     * インデックス（列）
     */
    public int col = 0;
    /**
     * インデックス（行）
     */
    public int row = 0;

    /**
     * コンストラクタ
     *
     * @param col 列
     * @param row 行
     */
    SetXY(int col, int row) {
        this.col = col;
        this.row = row;
    }

    /**
     * 比較
     *
     * @param obj 比較対象
     * @return true 一致
     * @return false 不一致
     */
    public boolean equals(SetXY obj) {
        return (obj != null && this.col == obj.col && this.row == obj.row);
    }

    /**
     * 文字列取得
     *
     * @return 文字列[(col: xx, row: yy)]
     */
    public String toString() {
        return ("(col: " + col + ", row: " + row + ")");
    }
}
