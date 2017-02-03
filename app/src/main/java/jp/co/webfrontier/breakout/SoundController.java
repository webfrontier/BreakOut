package jp.co.webfrontier.breakout;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * 効果音制御クラス
 */
public class SoundController {
    /**
     * 効果音管理クラス
     */
    private static SoundPool sSoundPool;
    /**
     * アクティビティ
     */
    private static Activity sActivity = null;
    /**
     * ブロック破壊音ID
     */
    private static int ID_HIT_BRICK;
    /**
     * 破壊不可ブロックヒット音ID
     */
    private static int ID_HIT_HARD_BRICK;
    /**
     * パッド反射音ID
     */
    private static int ID_HIT_PAD;
    /**
     * ボールロスト音ID
     */
    private static int ID_LOST_BALL;
    /**
     * ゲームクリア音ID
     */
    private static int ID_CLEAR;
    /**
     * ゲームオーバー音ID
     */
    private static int ID_GAME_OVER;

    /**
     * 初期化処理
     *
     * @param activity アクティビティ
     */
    public static void initialize(Activity activity) {
        sActivity = activity;

        // 取得元URL : 無料効果音素材(http://taira-komori.jpn.org/freesound.html)
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        sSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();
    }

    /**
     * リソース解放
     */
    public static void release() {
        sSoundPool.release();
    }
}
