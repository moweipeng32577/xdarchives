package com.wisdom.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyWordUtil {
	
	private Map<String, Object> dictionaryMap;

	public KeyWordUtil(List<String> config) {
		this.dictionaryMap = handleToMap(config);
	}

	public Map<String, Object> getDictionaryMap() {
		return dictionaryMap;
	}

	public void setDictionaryMap(Map<String, Object> dictionaryMap) {
		this.dictionaryMap = dictionaryMap;
	}

	private Map<String, Object> handleToMap(List<String> wordSet) {
		if (wordSet == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>(wordSet.size());
		Map<String, Object> curMap = null;
		Iterator<String> ite = wordSet.iterator();
		while (ite.hasNext()) {
			String word = ite.next();
			curMap = map;
			int len = word.length();
			for (int i = 0; i < len; i++) {
				String key = String.valueOf(word.charAt(i));
				Map<String, Object> wordMap = (Map<String, Object>) curMap.get(key);
				if (wordMap == null) {
					wordMap = new HashMap<String, Object>();
					wordMap.put("isEnd", "0");
					curMap.put(key, wordMap);
					curMap = wordMap;
				} else {
					curMap = wordMap;
				}
				if (i == len - 1) {
					curMap.put("isEnd", "1");
				}
			}
		}
		return map;
	}

	public int checkWord(String text, int beginIndex) {
		if (dictionaryMap == null) {
			throw new RuntimeException("字典不能为空！");
		}
		boolean isEnd = false;
		int wordLength = 0;
		Map<String, Object> curMap = dictionaryMap;
		int len = text.length();
		for (int i = beginIndex; i < len; i++) {
			String key = String.valueOf(text.charAt(i));
			curMap = (Map<String, Object>) curMap.get(key);
			if (curMap == null) {
				break;
			} else {
				wordLength++;
				if ("1".equals(curMap.get("isEnd"))) {
					isEnd = true;
				}
			}
		}
		if (!isEnd) {
			wordLength = 0;
		}
		return wordLength;
	}

	public Set<String> getWords(String text) {
		Set<String> wordSet = new HashSet<String>();
		int len = text.length();
		for (int i = 0; i < len; i++) {
			int wordLength = checkWord(text, i);
			if (wordLength > 0) {
				String word = text.substring(i, i + wordLength);
				wordSet.add(word);
				i = i + wordLength - 1;
			}
		}
		return wordSet;
	}
}