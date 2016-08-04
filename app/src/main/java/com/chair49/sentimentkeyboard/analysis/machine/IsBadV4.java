package com.chair49.sentimentkeyboard.analysis.machine;

import com.chair49.sentimentkeyboard.analysis.Sentiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;


public class IsBadV4 {


    public boolean isBad(String input,NLP nlp, Sentiment sent) {
        return isBadScore(input,nlp,sent) < 0;

    }

    public float isBadScore(String input,NLP nlp, Sentiment sent) {
        String[] toks = nlp.tokenize(input);
        String[] tags = nlp.tag(toks);
        String[] chunks = nlp.chunks(toks, tags);
        String[] groups = nlp.groupChunks(toks, chunks);
        float score = 0;
        for (String group : groups) {
            float multi = 1;
            float groupScore = 0;
            for (String word : group.toLowerCase().split("(\\W*)+ ")) {
                if (sent.negSet.contains(word))
                    multi *= -1;
                multi *= sent.dictIntense.get(word);
                groupScore += sent.dict.get(word);
            }
            score += groupScore * multi;
        }

        return score;

    }


}