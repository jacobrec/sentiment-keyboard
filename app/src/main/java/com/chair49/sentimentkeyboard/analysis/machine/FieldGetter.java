package com.chair49.sentimentkeyboard.analysis.machine;


import com.chair49.sentimentkeyboard.analysis.Sentiment;

public class FieldGetter {
    String tags[] = null;
    IsBadV4 ib = new IsBadV4();
    public NLP nlp;
    private Sentiment sent;
    public FieldGetter(Sentiment sent){
        nlp = new NLP();
        this.sent = sent;
    }


    public int Excalm(String string) {
        return string.length() - string.replaceAll("!", "").length();
    }

    public int Quest(String string) {
        return string.length() - string.replaceAll("\\?", "").length();
    }

    public int Verbs(String string) {
        if (tags == null)
            tags = nlp.tag(nlp.tokenize(string));
        int verbs = 0;
        for (String tag : tags) {
            if (tag.contains("VB"))
                verbs++;
        }
        return verbs;
    }

    public int Adjectives(String string) {
        if (tags == null)
            tags = nlp.tag(nlp.tokenize(string));
        int adjectives = 0;
        for (String tag : tags) {
            if (tag.contains("JJ"))
                adjectives++;
        }
        return adjectives;
    }


    public float Polarity(String string) {
        return ib.isBadScore(string,nlp,sent);
    }

    public int Emojis(String string) {
        char[] chars = string.toCharArray();
        int index;
        char ch1;
        char ch2;
        int count = 0;

        index = 0;
        while (index < chars.length - 1) {
            ch1 = chars[index];
            if ((int) ch1 == 0xD83C) {
                ch2 = chars[index + 1];
                if ((int) ch2 >= 0xDF00 && (int) ch2 <= 0xDFFF) {
                    index += 2;
                    count++;
                    continue;
                }
            } else if ((int) ch1 == 0xD83D) {
                ch2 = chars[index + 1];
                if ((int) ch2 >= 0xDC00 && (int) ch2 <= 0xDEFF) {
                    index += 2;
                    count++;
                    continue;
                }
            }
            ++index;
        }
        return count;
    }

    public int Length(String string) {
        if (tags == null)
            tags = nlp.tag(nlp.tokenize(string));
        return tags.length;
    }

    public int Subjects(String string) {
        if (tags == null)
            tags = nlp.tag(nlp.tokenize(string));
        int subjects = 0;
        for (String tag : tags) {
            if (tag.contains("PR") || tag.contains("NN") || tag.contains("DT"))
                subjects++;
        }
        return subjects;
    }

    public String Text(String string) {
        return "\"" + string + "\"";
    }

}
