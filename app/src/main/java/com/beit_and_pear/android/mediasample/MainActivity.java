package com.beit_and_pear.android.mediasample;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // メディアプレーヤーフィールド
    private MediaPlayer _player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // フィールドのメディアプレーヤーオブジェクトを生成
        _player = new MediaPlayer();
        // 音声ファイルのURI文字列を作成
        String mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.mountain_stream1;
        // 音声ファイルのURI文字列を元にURIオブジェクトを生成
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);
        try {
            // メディアプレーヤーに音声ファイルを指定
            _player.setDataSource(MainActivity.this, mediaFileUri);
            // 非同期でのメディア再生準備が完了した際のリスナを設定
            _player.setOnPreparedListener(new PlayerPreparedListener());
            // メディア再生が終了した際のリスナを設定
            _player.setOnCompletionListener(new PlayerCompletionListener());
            // 非同期でメディア再生を準備
            _player.prepareAsync();
        } catch (IOException ex) {
            Log.e("MediaSample", "メディアプレーヤー準備時の例外発生", ex);

            // 本番ではアラートの表示など処理が必要

        }

        // スイッチを取得
        SwitchMaterial loopSwitch = findViewById(R.id.swLoop);
        // スイッチにリスナを設定
        loopSwitch.setOnCheckedChangeListener(new LoopSwitchChangedListener());
    }

    // プレーヤーの再生準備が整った時のリスナクラス
    private class PlayerPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            // 各ボタンをタップ可能に設定
            Button btPlay = findViewById(R.id.btPlay);
            btPlay.setEnabled(true);
            Button btBack = findViewById(R.id.btBack);
            btBack.setEnabled(true);
            Button btForward = findViewById(R.id.btForward);
            btForward.setEnabled(true);
        }
    }

    // 再生が終了した時のリスナクラス
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            // もしループ設定がされていないならば
            if (! _player.isLooping()) {
                // 再生ボタンのラベルを「再生」に設定
                Button btPlay = findViewById(R.id.btPlay);
                btPlay.setText(R.string.bt_play_play);
            }
        }
    }

    // リピートスイッチの変更を検出
    private class LoopSwitchChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // ループするかどうかを設定
            _player.setLooping(isChecked);
        }
    }

    // 再生ボタン
    public void onPlayButtonClick(View view) {
        // 再生ボタンを取得
        Button btPlay = findViewById(R.id.btPlay);
        // プレーヤーが再生中ならば
        if (_player.isPlaying()) {
            // プレーヤーを一時停止
            _player.pause();
            // 再生ボタンのラベルを「再生」に設定
            btPlay.setText(R.string.bt_play_play);
        } else { // プレーヤーが再生中でなければ
            // プレーヤーを再生
            _player.start();
            // 再生ボタンのラベルを「一時停止」に設定
            btPlay.setText(R.string.bt_play_pause);
        }
    }

    // 戻るボタン
    public void onBackButtonClick(View view) {
        // 再生位置を先頭に変更
        _player.seekTo(0);
    }

    // 進むボタン
    public void onForwardButtonClick(View view) {
        // 現在再生中のメディアファイルの長さを取得
        int duration = _player.getDuration();
        // 再生位置を終端に変更
        _player.seekTo(duration);
        // 再生中でなければ
        if (! _player.isPlaying()) {
            // 再生を開始
            _player.start();
        }
    }

    @Override
    protected void onDestroy() {
        // プレーヤーが再生中なら
        if (_player.isPlaying()) {
            // プレーヤーを停止
            _player.stop();
        }
        // プレーヤーを解放
        _player.release();
        // プレーヤー用フィールドをnullに
        _player = null;
        // 親クラスのメソッド呼び出し
        super.onDestroy();
    }
}