package com.wisdom.util.retention;

/**
 * Created by Rong on 2018/11/2.
 */
public class WightWord {

    private String word;
    private double wight;
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public double getWight() {
        return wight;
    }
    public void setWight(double wight) {
        this.wight = wight;
    }
    public WightWord(String word, double wight){
        this.word = word;
        this.wight = wight;
    }

}
