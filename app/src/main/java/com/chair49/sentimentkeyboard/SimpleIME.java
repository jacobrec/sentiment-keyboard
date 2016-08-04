
package com.chair49.sentimentkeyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.chair49.sentimentkeyboard.analysis.NextWord;
import com.chair49.sentimentkeyboard.analysis.Sentiment;
import com.chair49.sentimentkeyboard.analysis.machine.NLP;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class SimpleIME extends InputMethodService implements OnKeyboardActionListener {
    private static final String TAG = "SentimentKeyboard";


    private MyKeyboardView kv;
    private Keyboard qwerty;
    private Keyboard symb;

    private Vibrator vib;


    private int abcView = 0;

    private KeyPressHandler longhandler;

    private Sentiment sentiment;

    private NextWord next;

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = this.getCurrentInputConnection();
        this.playClick(primaryCode);
        switch (primaryCode) {
            case MyKeyboardView.SHIFTCODE:
                kv.shift = !kv.shift;
                break;
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_DONE:
                try {
                    makeText(getApplicationContext(), getResults(sentiment.doesOffend(ic.getTextBeforeCursor(150, 0) + (String) ic.getTextAfterCursor(150, 0))), LENGTH_SHORT).show();
                }catch(NullPointerException e){
                    makeText(getApplicationContext(), "Sentiment analysis not loaded yet", LENGTH_SHORT).show();
                }
                break;
            case MyKeyboardView.SWAPVIEWCODE:
                this.swapView();
                break;
            case 46: // period
                if (ic.getTextBeforeCursor(1, 0).equals(" "))
                    ic.deleteSurroundingText(1, 0);
                ic.commitText(". ", 2);
                kv.shift = true;
                break;
            case 44: // comma
                if (ic.getTextBeforeCursor(1, 0).equals(" "))
                    ic.deleteSurroundingText(1, 0);
                ic.commitText(", ", 2);
                break;
            case -48://left word prediction
                setCorrection(1, ic);
                break;
            case -49://middle word prediction
                setCorrection(0, ic);
                break;
            case -50://right word prediction
                setCorrection(2, ic);
                break;
            default:// regular letters
                char code = (char) primaryCode;
                if (Character.isLetter(code) && kv.caps ^ kv.shift) {
                    code = Character.toUpperCase(code);
                }

                ic.commitText(String.valueOf(code), 1);
                if (kv.shift) {
                    kv.shift = false;
                }
                break;

        }
        if (next != null) {
            if (ic.getTextBeforeCursor(50, 0).equals("")) {
                kv.shift = true;
            } else if (((String) ic.getTextBeforeCursor(50, 0)).endsWith(" ")) {
                String[] words = ((String) ic.getTextBeforeCursor(50, 0)).split(" ");
                String one = null, two = null;
                if (words.length > 1) {
                    two = words[words.length - 1];
                    one = words[words.length - 2];
                }

                if (one != null && two != null) {
                    Log.i(TAG, one + " " + two);
                    kv.shouldCorrect = false;
                    kv.giveCorrections(next.getNextWords(one, two));
                    Log.i(TAG, "Goes to: " + kv.getCorrections(0) + " " + kv.getCorrections(1) + " " + kv.getCorrections(2));
                }
            } else {
                String currentWord = ((String) ic.getTextBeforeCursor(50, 0));
                kv.shouldCorrect = true;
                kv.correct(currentWord);
            }
        } else {
            String[] cor = new String[3];
            cor[0] = "Loading...";
            kv.giveCorrections(cor);
        }
        this.qwerty.setShifted(kv.caps ^ kv.shift);
        this.kv.invalidateAllKeys();

    }

    private void setCorrection(int i, InputConnection ic) {
        if (ic.getTextBeforeCursor(1, 0).equals(" ")) {
            String texts = this.kv.getCorrections(i);
            ic.commitText(texts + " ", texts.length() + 1);
        } else {
            String currentWord = ((String) ic.getTextBeforeCursor(50, 0));
            String[] words = currentWord.split(" ");
            currentWord = words[words.length - 1];
            int length = currentWord.length();
            ic.deleteSurroundingText(length, 0);
            String texts = this.kv.getCorrections(i);
            ic.commitText(texts + " ", texts.length() + 1);
        }
    }

    private String getResults(boolean b) {
        if (b)
            return "This may be offensive";
        return "That text is fine";
    }

    private void onKeyLongPress(int keyCode) {
        Log.v(TAG, "long pressed: " + keyCode);
        switch (keyCode) {
            case -100:
                kv.caps = !kv.caps;
                this.qwerty.setShifted(kv.caps ^ kv.shift);
                kv.shift = true;
                this.kv.invalidateAllKeys();
                this.vib.vibrate(100L);
                break;
            case Keyboard.KEYCODE_DONE:
                InputConnection ic = this.getCurrentInputConnection();
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                ic.finishComposingText();
                break;
            default:
        }
    }

    @Override
    public void onPress(final int code) {
        if (code == -48 || code == -49 || code == -50) {
            kv.setPreviewEnabled(false);
        }
        this.longhandler = new KeyPressHandler(new Runnable() {
            public void run() {
                SimpleIME.this.onKeyLongPress(code);
            }
        });
    }

    @Override
    public void onRelease(int code) {
        this.longhandler.stop();
        if (code == -48 || code == -49 || code == -50) {
            kv.setPreviewEnabled(true);
        }
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        sentiment = new Sentiment(this.getBaseContext());
        this.qwerty.setShifted(kv.caps ^ kv.shift);
        this.kv.invalidateAllKeys();
        String[] cor = new String[3];
        cor[0] = "Loading...";
        kv.giveCorrections(cor);
        new LoadFilesTask() {
            @Override
            protected void onPostExecute(NextWord n) {
                super.onPostExecute(n);
                next = n;
                System.out.print("Loaded Files");
                String[] cor = new String[3];
                cor[0] = " ";
                kv.giveCorrections(cor);
            }
        }.execute(this.getBaseContext());
        System.out.print("Loaded Files");

        InputConnection ic = this.getCurrentInputConnection();
        if (ic.getTextBeforeCursor(50, 0).equals(""))
            kv.shift = true;
        this.qwerty.setShifted(kv.caps ^ kv.shift);

    }

    private class LoadFilesTask extends AsyncTask<Context, Void, NextWord> {

        protected NextWord doInBackground(Context... c) {
           // NLP.init(c[0]);
            return new NextWord(c[0]);
        }

    }


    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case -5:
                am.playSoundEffect(7);
                break;
            case -4:
            case 10:
                am.playSoundEffect(8);
                break;
            case 32:
                am.playSoundEffect(6);
                break;
            default:
                am.playSoundEffect(5);
        }

    }

    @Override
    public View onCreateInputView() {
        final ViewGroup nullParent = null;
        this.kv = (MyKeyboardView) this.getLayoutInflater().inflate(R.layout.keyboard, nullParent, false);
        this.qwerty = new Keyboard(this, R.xml.qwerty);
        this.symb = new Keyboard(this, R.xml.sym);
        this.kv.setKeyboard(this.qwerty);
        this.kv.setOnKeyboardActionListener(this);
        this.vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        return this.kv;
    }

    private void swapView() {
        ++this.abcView;
        this.abcView %= 2;
        if (this.abcView == 0) {
            this.kv.setKeyboard(qwerty);
        } else if (this.abcView == 1) {
            this.kv.setKeyboard(symb);
        }
    }


}
