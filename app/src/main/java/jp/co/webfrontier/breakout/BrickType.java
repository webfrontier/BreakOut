package jp.co.webfrontier.breakout;

/**
 * ブロック種別
 */
public enum BrickType {
    /**
     * ブロックなし
     */
    Blank(0),
    /**
     * 通常ブロック
     */
    Normal(1),
    /**
     * 破壊不可
     */
    Unbroken(2);

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
     * 種別値取得
     *
     * @return 種別値
     */
    int getValue()
    {
        return value;
    }
}
