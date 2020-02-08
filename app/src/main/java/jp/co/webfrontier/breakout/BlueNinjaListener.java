package jp.co.webfrontier.breakout;

import org.json.JSONObject;

/**
 * BlueNinjaとの通信を行うためのインターフェース
 */

public interface BlueNinjaListener {

    /**
     * BLE機器の接続状態の変更通知
     *
     * @param connected BLE機器接続状態
     */
    public void onBLEConnectionStatusChanged(boolean connected);

    /**
     * BLE機器からのデータ受信
     *
     * @param dataObject 受信データ(Json)
     */
    public void onBLEDataReceived(JSONObject dataObject);
}
