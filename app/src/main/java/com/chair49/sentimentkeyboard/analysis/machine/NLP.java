package com.chair49.sentimentkeyboard.analysis.machine;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class NLP {

     Tokenizer tokenizer;
     POSTaggerME tagger;
     ChunkerME chunker;





    public  void init(Context c) {
        long start = System.nanoTime();

        InputStream modelIn = null;

        try {
            modelIn = c.getAssets().open("models/en-token.bin");
            TokenizerModel modelTok = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(modelTok);

            modelIn = c.getAssets().open("models/en-pos-maxent.bin");
            POSModel modelTag = new POSModel(modelIn);
            tagger = new POSTaggerME(modelTag);


            modelIn = c.getAssets().open("models/en-chunker.bin");
            ChunkerModel modelChunk = new ChunkerModel(modelIn);
            chunker = new ChunkerME(modelChunk);


        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println((System.nanoTime() - start) / 1000000
                + "ms to load NLP");

    }

    /**
     * Takes a string and splits it into tokens
     *
     * @param sentences A string of text you wish to convert
     * @return An array of tokens
     */
    public  String[] tokenize(String sentences) {
        return tokenizer.tokenize(sentences);
    }

    /**
     * Takes an array of tokens and converts them to speech tags
     *
     * @param tokens an array of tokens you wish to convert
     * @return An array of tagged parts of speech
     */
    public  String[] tag(String[] tokens) {
        return tagger.tag(tokens);
    }

    /**
     * A chunk is a group of similar tokens <br>
     *
     * @param tokens an array of all the words
     * @param tags   an array of all the tags of the words
     * @return the chunks
     * @see <a
     * href="http://kontext.fraunhofer.de/haenelt/kurs/Referate/Ardelean_SS03/chunk-6.pdf">sentence
     * chunking(http://kontext.fraunhofer.de/haenelt/kurs/Referate/Ardelean_SS03/chunk-6.pdf)</a>
     */
    public  String[] chunks(String[] tokens, String[] tags) {
        return chunker.chunk(tokens, tags);
    }

    /**
     * each string is all the words in a chunk seperated by a space
     *
     * @param tokens
     * @param chunks
     * @return each chunk as a group of tokens
     * @see {@link NLP#chunks(String[], String[])}
     */
    public  String[] groupChunks(String[] tokens, String[] chunks) {
        boolean b = true;
        int j = 0;
        List<String> re = new ArrayList<>(tokens.length);
        for (int i = 0; i < chunks.length; i++) {
            if (re.size() > j)
                re.set(j, re.get(j) + " " + tokens[i]);
            else
                re.add(tokens[i]);
            if (chunks[i].contains("B") && b) {
                j++;
                b = true;
            } else {
                b = false;
            }

        }
        return re.toArray(new String[re.size()]);

    }



}
