package jp.co.webfrontier.breakout;

/**
 * ブロックの種別
 */
public enum BrickType {
    /**
     * ブロックがない
     */
    Blank(0),
    /**
     * 通常のブロック
     */
    Normal(1),
    /**
     * 破壊不可のブロック
     */
    Unbroken(2),
    /**
     * スペシャルブロック
     */
    Special(3);

    /**
     * 種別値
     */
    int value = 0;

    /**
     * コンストラクタ
     *
     * @param value 種別値
     */
    private BrickType(int value)
    {
        this.value = value;
    }

    /**
     * ブロックの種別を取得する
     *
     * @return 種別
     */
    int getValue()
    {
        return value;
    }
}
