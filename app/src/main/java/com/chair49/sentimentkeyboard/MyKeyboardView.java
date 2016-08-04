package com.chair49.sentimentkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import java.util.List;
import java.util.Locale;

public class MyKeyboardView extends KeyboardView implements SpellCheckerSession.SpellCheckerSessionListener {
    private final Context c;
    public boolean shouldCorrect = true;
    private final String[] blank = new String[]{" ", " ", " "};

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        SuggestionsInfo si = results[0].getSuggestionsInfoAt(results[0].getSuggestionsCount() - 1);
        if (shouldCorrect) {
            System.out.println("Corrections:");
            this.corrections = blank;
            for (int k = 0; k < si.getSuggestionsCount(); k++) {
                giveCorrectionsAt(si.getSuggestionAt(k), k);
                System.out.print(si.getSuggestionAt(k));
            }
            System.out.println();
        }
        this.invalidate();
    }


    public void correct(String input) {
        TextServicesManager tsm =
                (TextServicesManager) c.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        SpellCheckerSession session = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, true);
        session.getSentenceSuggestions(new TextInfo[]{new TextInfo(input)}, 3);

    }

    private String[] corrections;

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        corrections = new String[]{" ", " ", " "};
    }


    public static final int SHIFTCODE = -100;
    public static final int SWAPVIEWCODE = -8920;

    public boolean caps = false;
    public boolean shift = false;

    private Paint paint;

    @Override
    public void onDraw(Canvas canvas) {
       if(paint == null){
           paint = new Paint();
       }
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {

            paint.setTextAlign(Paint.Align.CENTER);
            int size = 60;
            paint.setTextSize(size);
            paint.setColor(Color.argb(255,200,230,201));

            if (key.label != null) {
                if (this.isShifted()) {
                    canvas.drawText(key.label.toString().toUpperCase(), key.x + (key.width / 2),
                            key.y + (key.height / 2) + size / 2, paint);
                } else {
                    canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                            key.y + (key.height / 2) + size / 2, paint);
                }
            }
            if (key.codes[0] == SHIFTCODE) {
                if (caps) {
                    canvas.drawCircle(key.x + key.width / 4, key.y + key.height / 4 * 3, 8, paint);
                }
            } else if (key.codes[0] == -48) {
                canvas.drawText(corrections[1], key.x + (key.width / 2),
                        key.y + (key.height / 2) + size / 2, paint);
            } else if (key.codes[0] == -49) {
                canvas.drawText(corrections[0], key.x + (key.width / 2),
                        key.y + (key.height / 2) + size / 2, paint);
            } else if (key.codes[0] == -50) {
                canvas.drawText(corrections[2], key.x + (key.width / 2),
                        key.y + (key.height / 2) + size / 2, paint);
            }
        }
    }

    public void giveCorrections(String[] cor) {
        this.corrections = cor;
        for (int i = 0; i < 3; i++) {
            if (this.corrections[i] == null)
                this.corrections[i] = " ";
        }
    }

    private void giveCorrectionsAt(String suggestionAt, int i) {
        this.corrections[i] = suggestionAt;
    }


    public String getCorrections(int i) {
        return this.corrections[i];
    }
}
