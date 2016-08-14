package jp.co.webfrontier.breakout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * ブロック崩しメインアクティビティ
 */
public class MainActivity extends AppCompatActivity {
    /**
     * デバッグログ用タグ
     */
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
