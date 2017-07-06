package han.jia.cloud.nlp.util;

import han.jia.cloud.nlp.domain.Inflected;
import han.jia.cloud.nlp.ema.GraphNode;
import han.jia.cloud.nlp.ema.MorphNode;
import han.jia.cloud.nlp.ema.SuffixObj;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An instance of this class holds the needed lookup tables. It is expensive to
 * create one so it must be created as a singleton.
 * 
 * @author Jiayun Han
 *
 */
public class Dictionary {

	private List<String> edErEstIng;
	private List<String> esEnding;

	private List<String> unsplittables;
	private List<String> words;

	private List<String> strongSuffixes;

	private List<String> cYs;
	private List<String> vCCs;

	private Map<String, List<SuffixObj>> suffixTable;

	private List<String> prefixes;
	private List<String> affixes;

	private Map<String, GraphNode> derivativeMap;

	// inflected-str -> inflected-object
	private Map<String, Inflected> inflectionTable;

	// built from morph_rules file: word => MorphNode form of this word, its son
	// and/or daughter may be decomposible
	private Map<String, MorphNode> morphNodeMap;

	// for caching purpose
	private Map<String, MorphNode> wordNodeMap;

	public List<String> getEsEnding() {
		return esEnding;
	}

	public void setEsEnding(String... ss) {
		this.esEnding = Arrays.asList(ss);
	}

	public List<String> getEdErEstIng() {
		return edErEstIng;
	}

	public void setEdErEstIng(String... ss) {
		this.edErEstIng = Arrays.asList(ss);
	}

	public List<String> getcYs() {
		return cYs;
	}

	public void setcYs(List<String> cYs) {
		this.cYs = cYs;
	}

	public List<String> getvCCs() {
		return vCCs;
	}

	public void setvCCs(List<String> vCCs) {
		this.vCCs = vCCs;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public List<String> getAffixes() {
		return affixes;
	}

	public void setAffixes(List<String> affixes) {
		this.affixes = affixes;
	}

	public Map<String, GraphNode> getDerivativeMap() {
		return derivativeMap;
	}

	public void setDerivativeMap(Map<String, GraphNode> derivativeMap) {
		this.derivativeMap = derivativeMap;
	}

	public Map<String, List<SuffixObj>> getSuffixTable() {
		return suffixTable;
	}

	public void setSuffixTable(Map<String, List<SuffixObj>> suffixTable) {
		this.suffixTable = suffixTable;
	}

	public Map<String, Inflected> getInflectionTable() {
		return inflectionTable;
	}

	public void setInflectionTable(Map<String, Inflected> inflectionTable) {
		this.inflectionTable = inflectionTable;
	}

	public List<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
	}

	public List<String> getUnsplittables() {
		return unsplittables;
	}

	public void setUnsplittables(List<String> unsplittables) {
		this.unsplittables = unsplittables;
	}

	public Map<String, MorphNode> getMorphNodeMap() {
		return morphNodeMap;
	}

	public void setMorphNodeMap(Map<String, MorphNode> morphNodeMap) {
		this.morphNodeMap = morphNodeMap;
	}

	public Map<String, MorphNode> getWordNodeMap() {
		return wordNodeMap;
	}

	public void setWordNodeMap(Map<String, MorphNode> wordNodeMap) {
		this.wordNodeMap = wordNodeMap;
	}

	public List<String> getStrongSuffixes() {
		return strongSuffixes;
	}

	public void setStrongSuffixes(List<String> strongSuffixes) {
		this.strongSuffixes = strongSuffixes;
	}
}
