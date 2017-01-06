package jp.co.webfrontier.breakout;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * 効果音制御クラス
 */
public final class SoundController {
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

        // 音データ読み込み
        ID_HIT_BRICK = sSoundPool.load(sActivity, R.raw.button04b, 1);
        ID_HIT_HARD_BRICK = sSoundPool.load(sActivity, R.raw.laser1, 1);
        ID_HIT_PAD = sSoundPool.load(sActivity, R.raw.button01b, 1);
        ID_LOST_BALL = sSoundPool.load(sActivity, R.raw.powerdown07, 1);
        ID_CLEAR = sSoundPool.load(sActivity, R.raw.powerup02, 1);
        ID_GAME_OVER = sSoundPool.load(sActivity, R.raw.powerdown03, 1);
    }

    /**
     * リソース解放
     */
    public static void release() {
        sSoundPool.release();
    }

    /**
     * ブロック破壊音再生
     */
    public static void playHitBrick()
    {
        sSoundPool.play(ID_HIT_BRICK, 1.0f, 1.0f, 0, 0, 1);
    }

    /**
     * 破壊不可ブロックヒット音再生
     */
    public static void playHitHardBrick()
    {
        sSoundPool.play(ID_HIT_HARD_BRICK, 1.0f, 1.0f, 0, 0, 1);
    }

    /**
     * パッド反射音
     */
    public static void playHitPad()
    {
        sSoundPool.play(ID_HIT_PAD, 1.0f, 1.0f, 0, 0, 1);
    }

    /**
     * ボールロスト音再生
     */
    public static void playLostBall()
    {
        sSoundPool.play(ID_LOST_BALL, 1.0f, 1.0f, 0, 0, 1);
    }

    /**
     * ゲームクリア音再生
     */
    public static void playClear()
    {
        sSoundPool.play(ID_CLEAR, 1.0f, 1.0f, 0, 0, 1);
    }

    /**
     * ゲームオーバー音再生
     */
    public static void playGameOver()
    {
        sSoundPool.play(ID_GAME_OVER, 1.0f, 1.0f, 0, 0, 1);
    }
}
