package com.chair49.sentimentkeyboard.analysis;

import android.content.Context;

import com.chair49.sentimentkeyboard.DefaultHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Sentiment {//For machine learning remove all commented code

    public Map<String, Integer> dict;
    public Map<String, Double> dictIntense;
    public Set<String> negSet;
   // FieldGetter fg;
    public Sentiment(Context baseContext) {
       // fg = new FieldGetter(this);
        //fg.nlp.init(baseContext);
        try {
            negSet = negationSet(baseContext);
            dict = readMerged(baseContext); //Map of all negative words and associated values in merged.txt
            dictIntense = readMergedIntensifier(baseContext);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public boolean doesOffend(String input) {
        return isOffensive(input);
    }
    double getScore(int sentenceLength, int numberOfExclamationMarks,
                           int numberOfQuestionMarks, int numberOfEmojis, int numberOfVerbs,
                           int numberOfDeterminers, int numberOfAdjectives,
                           int numberOfNegations, int numberOfIntensifiers, float polarity) {
        return -0.1794 * numberOfEmojis + 0.2798 * numberOfNegations + -0.1686
                * numberOfIntensifiers + -0.063 * polarity + 0.4364;
    }

    boolean isOffensive(String in) {
//        double score = getScore(fg.Length(in), fg.Emojis(in),
//                fg.Quest(in), fg.Emojis(in),
//                fg.Verbs(in), fg.Subjects(in),
//                fg.Adjectives(in), negNum(in),
//                intenseNum(in), fg.Polarity(in));
//        return score < 0.5f;
        //TODO: remove temporary method before release
        double last = 1;
        float score = 0;
        for (String word : in.toLowerCase().split("(\\W*)+ ")) {
            if (this.dictIntense.containsKey(word)) {
                last *= this.dictIntense.get(word);
            } else if (this.negSet.contains(word)) {
                last *= -1;
            } else {
                if (this.dict.containsKey(word))
                    score += last * this.dict.get(word);
                last = 1;
            }
        }
        score *= last;

        return score < -1;
    }

    private Map<String, Integer> readMerged(Context c) throws IOException { //This function returns a map of the merged.txt file
        Map<String, Integer> mergedMap; //Initializing maps
        Scanner mergedScanner = new Scanner(c.getAssets().open("merged.txt"));
        mergedMap = new DefaultHashMap<>(0);
        while (mergedScanner.hasNext()) { //While there are still values to add
            String mergedString = mergedScanner.nextLine(); //Takes individual line as string
            mergedMap.put(mergedString.split(",")[0], Integer.parseInt(mergedString.split(",")[1])); //Add an entry per line, split by a comma for each value
        }
        mergedScanner.close();

        return mergedMap; //Returns map of String and associated integer value
    }

    private Map<String, Double> readMergedIntensifier(Context c) throws IOException { //This function returns a map of the merged intensifier file
        Map<String, Double> intensifierMap; //Initializing map
        Scanner intensifierScanner = new Scanner(c.getAssets().open("intensifiers.txt"));
        intensifierMap = new DefaultHashMap<>(1d);
        while (intensifierScanner.hasNext()) { //While there are still values to add
            String intensifierString = intensifierScanner.nextLine(); //Takes individual line as string
            intensifierMap.put(intensifierString.split(",")[0], Double.parseDouble(intensifierString.split(",")[1])); //Add an entry per line, split by a comma for each value
        }
        intensifierScanner.close();

        return intensifierMap; //Returns map of String and associated double value
    }

    private Set<String> negationSet(Context c) throws IOException { //This function returns a set of possible negations
        Scanner negScanner = new Scanner(c.getAssets().open("negation.txt"));
        ArrayList<String> list2 = new ArrayList<>(); //First creates an ArrayList to store values
        while (negScanner.hasNext()) { //While there are still values to add
            list2.add(negScanner.next()); //Add to list
        }
        negScanner.close();
        return new HashSet<>(list2); //Returns the set of negations
    }

    public int intenseNum(String s) {
        String[] ss = s.toLowerCase().split(" ");
        int i = 0;
        for (String a : ss) {
            if (dictIntense.containsKey(a))
                i++;
        }
        return i;

    }

    public int negNum(String s) {
        String[] ss = s.toLowerCase().split(" ");
        int i = 0;
        for (String a : ss) {
            if (negSet.contains(a))
                i++;
        }
        return i;

    }


}
