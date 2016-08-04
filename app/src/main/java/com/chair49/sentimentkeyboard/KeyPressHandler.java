package com.chair49.sentimentkeyboard;

import android.os.Handler;

class KeyPressHandler extends Handler {
    private final Runnable runner;
    private final int time = 300;

    public void stop() {
        this.removeCallbacks(this.runner);
    }


    public KeyPressHandler(final Runnable runnable) {
        this.runner = runnable;
        this.postDelayed(this.runner,this.time);
    }
}