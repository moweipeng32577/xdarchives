package com.wisdom.web.service;

import com.hankcs.hanlp.HanLP;
import com.wisdom.util.retention.Participle;
import com.wisdom.util.retention.Viterbi;
import com.wisdom.util.retention.WightWord;
import com.wisdom.web.entity.AlgorithmRetention;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.repository.AlgorithmRetentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * Created by Rong on 2018/10/30.
 */
@Service
@Transactional
public class AlgorithmRetentionService {

    @Autowired
    AlgorithmRetentionRepository algorithmRetentionRepository;

    @Transactional(rollbackFor = { Exception.class })
    public boolean resetRetentionTable(List<Object []> needToStudyList){
        try{
            algorithmRetentionRepository.deleteAll();
            for(int i = 0, len = needToStudyList.size(); i < len; i++) {
                this.register((String)needToStudyList.get(i)[0],(String)needToStudyList.get(i)[1]);
            }
        }catch(Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    /**
     * 新数据补充到预测表
     * @param content
     * @param retention
     */
    public void register(String content, String retention){
        //1.分词
        List<WightWord> words = Participle.segment(content);
        for (int i = 0; i < words.size(); i++) {
            //2.词条及保管期限写入预测表
            AlgorithmRetention algorithmRetention = algorithmRetentionRepository.findByWordAndRetention(words.get(i).getWord(), retention);
            if(algorithmRetention == null){
                //新增词条
                algorithmRetention = new AlgorithmRetention();
                algorithmRetention.setWord(words.get(i).getWord());
                algorithmRetention.setRetention(retention);
                algorithmRetention.setNums(1);
            }else{
                //已存在词条增加次数
                algorithmRetention.setNums(algorithmRetention.getNums() + 1);
            }
            algorithmRetention.setModifydate(new Date());
            algorithmRetentionRepository.save(algorithmRetention);
        }
    }

    enum Retention{
        永久,长期,短期
    };

    /**
     * 保管期限预测，并返回过程信息
     * @param content
     * @param process
     * @return
     */
    public String algorithmRetentionWithProcess(String content, List<String> process){
        String retention = algorithm(content, process);
        return retention;
    }

    /**
     * 保管期限预测
     * @param content
     * @return
     */
    public String algorithmRetention(String content){
        String retention = "";
        List<String> process = new ArrayList<String>();
        retention = algorithm(content,process);
        process.stream().forEach(log -> System.out.println(log));
        return  retention;
    }

    /**
     * 获取已有机器学习库内容
     * @param page
     * @param limit
     * @return
     */
    public Page<AlgorithmRetention> findLibrary(int page, int limit){
        Pageable pageable = new PageRequest(page - 1, limit, new Sort(Sort.Direction.DESC,"nums"));
        return algorithmRetentionRepository.findAll(pageable);
    }

    /**
     * 保管期限预测核心方法
     * @param content
     * @return
     */
    private String algorithm(String content, List<String> process){
        process.add("====================保管期限辅助鉴定开始=======================");
        process.add("鉴定内容：");
        process.add("    " + content);
        //1.内容分词
        Map<String, Double> wordswight = new HashMap<String, Double>();
        List<WightWord> words = Participle.segment(content);
        //2.从预测表识别每个词对应不同保管期限的概率
        String[] wordstr = new String[words.size()];
        for (int i = 0; i < words.size(); i++) {
            wordstr[i] = words.get(i).getWord();
            wordswight.put(wordstr[i], words.get(i).getWight());
        }
        List<AlgorithmRetention> algList = algorithmRetentionRepository.findAllByWordIn(wordstr);
        if(algList.size() == 0){

        }
        //3.构造隐马尔可夫数据模型，进行预测
        // 3.1 初始概率，保管期限为永久、长期、短期的初始概率
        double[] start_status = new double[] { 0.6, 0.28, 0.12 } ;
         /**
         * 3.2 状态转换矩阵
         * 各行元素之和为1
         * 即当前保管期限为永久、长期、短期，下一次保管期限为永久、长期、短期的概率
         * 这里假设下一次保管期限与上一次相同的概率为60%，其余两种保管期限的概率各为20%
         */
        double[][] transititon_probability = new double[][] {
                { 0.6, 0.2, 0.2 },
                { 0.2, 0.6, 0.2 },
                { 0.2, 0.2, 0.6 }
        };
        /**
         * 3.3 混淆观察矩阵
         * 各行元素之和为1
         * 通过观察序列，即文本进行分词后的词条
         * 即当每个词条对保管期限影响的概率
         * 此处使用已保存的数据进行计算
         * 比如词条为关于、欣档、通知，保管期限为永久出现的次数为3，1，6，则概率为0.3，0.1，0.6
         * 范例如：
         * { 0.3, 0.1, 0.6 }
         * { 0.5, 0.3, 0.2 }
         * { 0.2, 0.7, 0.1 }
         */
        Set<String> wordSet = new LinkedHashSet<>();
        Map<String, Integer> permanent = new HashMap<String, Integer>();
        Map<String, Integer> longterm = new HashMap<String, Integer>();
        Map<String, Integer> shortterm = new HashMap<String, Integer>();
        Map<String, Integer> totalMap = new HashMap<String, Integer>();
        for (int i = 0; i < algList.size(); i++) {
            AlgorithmRetention alg = algList.get(i);
            wordSet.add(alg.getWord());
            if(totalMap.containsKey(alg.getRetention())){
                totalMap.put(alg.getRetention(), totalMap.get(alg.getRetention()) + alg.getNums());
            }else{
                totalMap.put(alg.getRetention(), alg.getNums());
            }
            switch (alg.getRetention()){
                case "永久":
                    permanent.put(alg.getWord(), alg.getNums());
                    break;
                case "长期":
                    longterm.put(alg.getWord(), alg.getNums());
                    break;
                case "短期":
                    shortterm.put(alg.getWord(), alg.getNums());
                    break;
                default:
                    break;
            }
        }
        List<String> wordList = new ArrayList<>();
        wordList.addAll(wordSet);
        double[][] emission_probability = new double[3][wordSet.size()];
        for (int i = 0; i < Retention.values().length; i++) {
            for (int j = 0; j < wordList.size(); j++) {
                String word = wordList.get(j);
                switch (Retention.values()[i]){
                    case 永久:
                        emission_probability[i][j] = permanent.containsKey(word) ? (double)permanent.get(word)/totalMap.get("永久") : 0;
                        break;
                    case 长期:
                        emission_probability[i][j] = longterm.containsKey(word) ? (double)longterm.get(word)/totalMap.get("长期") : 0;
                        break;
                    case 短期:
                        emission_probability[i][j] = shortterm.containsKey(word) ? (double)shortterm.get(word)/totalMap.get("短期") : 0;
                        break;
                    default:
                        break;
                }
            }
        }
        // 3.4 隐藏状态序列（保管期限：永久，长期，短期）
        int[] states = new int[]{ 0, 1, 2 };
        // 3.5 观察序列（词条）
        int[] observations = new int[wordList.size()];
        for (int i = 0; i < wordList.size(); i++) {
            observations[i] = i;
        }
        // 3.6 使用维特比算法进行计算，预测最可能的状态序列
        String logstr = "";
        int[] result = Viterbi.compute(observations, states, start_status, transititon_probability, emission_probability);
        Map<String, Double> resultMap = new HashMap<>();
        logstr = "内容分词结果：";
        process.add(logstr);
        logstr = "    " + HanLP.segment(content);
        process.add(logstr);
        logstr = "词组组合结果（内容及互信息值）：";
        process.add(logstr);
        logstr = "    ";
        for (String word : wordList) {
            logstr += word + "("+wordswight.get(word)+")" + " ";
        }
        process.add(logstr);
        process.add("结合机器学习库，运用隐马尔可夫模型，用维特比算法得出词组序列对应的最可能的保管期限序列为：");
        logstr = "    ";
        for (int i = 0; i < result.length; i++) {
            String ret = Retention.values()[result[i]].name();
            logstr += ret + " ";
            if(resultMap.containsKey(ret)){
                resultMap.put(ret, resultMap.get(ret) + wordswight.get(wordList.get(i)));
            }else{
                resultMap.put(ret, wordswight.get(wordList.get(i)));
            }
        }
        process.add(logstr);
        logstr = "汇总得出不同保管期限的权重：";
        process.add(logstr);
        logstr = "    ";
        double maxWight = 0;
        String maxRetention = "";
        for (String key : resultMap.keySet()){
            logstr += key+"("+resultMap.get(key)+") ";
            if(resultMap.get(key) > maxWight){
                maxRetention = key;
                maxWight = resultMap.get(key);
            }
        }
        process.add(logstr);
        process.add("最终预测结果：" + maxRetention);
        process.add("====================保管期限辅助鉴定结束=======================");
        return maxRetention;
    }

    public Page<AlgorithmRetention> findBySearch(String condition, String operator, String content, int page, int limit, Sort sort){
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return algorithmRetentionRepository.findAll(sp,pageRequest);
    }
}
