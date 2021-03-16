package com.wisdom.util.retention;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.occurrence.Occurrence;
import com.hankcs.hanlp.corpus.occurrence.PairFrequency;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.dictionary.stopword.Filter;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词处理器
 * Created by Rong on 2018/11/1.
 */
public class Participle {

    //分词器类型
    public enum Type {
        TYPE_WORD,               //Word分词器
        TYPE_HanLP_Word,        //HanLP分词器
        TYPE_HanLP_KeyWord,     //HanLP关键词
        TYPE_HanLP_Phrase       //HanLP短语
    }

    /**
     * 文本分词，默认使用Word分词器
     * @param content   文本
     * @return  分词结果
     */
    public static List<WightWord> segment(String content){
        return segment(content, Type.TYPE_HanLP_Phrase);
    }

    /**
     * 文本分词
     * @param content   文本
     * @param type      分词器类型
     * @return  分词结果
     */
    public static List<WightWord> segment(String content, Type type){
        List<WightWord> result = new ArrayList<>();
        //如果文本内容长度不超过3，不做分词直接返回
        if(content.length() < 3){
            result.add(new WightWord(content, 1));
            return result;
        }
        switch (type){
            case TYPE_WORD:
                /**
                 * 1. Word分词
                 * 去除停顿词，如：是、的等
                 * 指定使用全切分算法
                 */
                List<Word> words = WordSegmenter.seg(content, SegmentationAlgorithm.FullSegmentation);
                words.forEach(word -> { result.add(new WightWord(word.getText(),1)); });
                break;
            case TYPE_HanLP_Word:
                /**
                 * 2. HanLP语言处理
                 * 使用HMM-Bigram模型最短路分词
                 */
                List<Term> terms = HanLP.segment(content);
                terms.forEach(term -> {
                    if(!term.nature.equals(Nature.w) && !term.nature.equals(Nature.uj)){
                        result.add(new WightWord(term.word,1));
                    }
                });
                break;
            case TYPE_HanLP_KeyWord:
                /**
                 * 3. HanLP语言处理
                 * 通过TextRank算法，提取关键词
                 */
                List<String> keylist = HanLP.extractKeyword(content, content.length() / 5);
                keylist.forEach(key -> {
                    result.add(new WightWord(key,1));
                });
                break;
            case TYPE_HanLP_Phrase:
                /**
                 * 4. HanLP语言处理
                 * 基于互信息和左右信息熵提取短语
                 */
                Filter[] filterChain = new Filter[]{
                    CoreStopWordDictionary.FILTER,
                    new Filter(){
                        @Override
                        public boolean shouldInclude(Term term){
                            if (term.nature == Nature.t || term.nature == Nature.nx)
                                return false;
                            return true;
                        }
                    }
                };
                Occurrence occurrence = new Occurrence();
                for (List<Term> term : NotionalTokenizer.seg2sentence(content, filterChain)){
                    occurrence.addAll(term);
                }
                occurrence.compute();
                for (PairFrequency phrase : occurrence.getPhraseByMi()){
                    result.add(new WightWord(phrase.first+phrase.second, phrase.mi));
                }
                break;
            default:
                words = WordSegmenter.seg(content, SegmentationAlgorithm.FullSegmentation);
                words.forEach(word -> { result.add(new WightWord(word.getText(),1)); });
                break;
        }
        return  result;
    }

}
