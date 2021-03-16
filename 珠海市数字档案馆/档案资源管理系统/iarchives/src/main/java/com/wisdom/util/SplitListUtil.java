package com.wisdom.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/7/2.
 */
public class SplitListUtil {

    public List<List<String>> splitList(List<String> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize; // TODO
        List<List<String>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;

    }
}
