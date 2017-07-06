package han.jia.cloud.nlp.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import han.jia.cloud.nlp.domain.Ending;
import han.jia.cloud.nlp.domain.Inflected;
import han.jia.cloud.nlp.domain.Prefix;
import han.jia.cloud.nlp.domain.Word;
import han.jia.cloud.nlp.ema.GraphNode;
import han.jia.cloud.nlp.ema.MorphNode;
import han.jia.cloud.nlp.ema.SuffixObj;
import han.jia.cloud.nlp.service.WordService;
import han.jia.cloud.nlp.util.Dictionary;
import han.jia.cloud.nlp.util.Constants;

/**
 * The purpose is to ensure a singleton instance of the expensive Dictionary is
 * created when MorphParser is initiated.
 * 
 * @author Jiayun Han
 *
 */
public enum Singleton {

	INSTANCE;

	private Dictionary dictionary;

	public Dictionary getDictionary(WordService wordService) {
		return Optional.ofNullable(dictionary).orElseGet(
				() -> buildDictionary(wordService));
	}

	private Dictionary buildDictionary(WordService wordService) {

		Dictionary dictionary = new Dictionary();

		Map<Boolean, List<Word>> wordMap = wordService.findAllWords().collect(
				Collectors.groupingBy(Word::isSplittable));

		Map<Integer, List<Ending>> endingMap = wordService.findAllEndings()
				.collect(Collectors.groupingBy(Ending::getType));

		dictionary = new Dictionary();

		dictionary.setEdErEstIng("ed", "er", "est", "ing");
		dictionary.setEsEnding("ches", "oes", "ses", "shes", "ves", "xes",
				"zes");

		setUnsplittables(wordMap, dictionary);
		setWords(wordMap, dictionary);

		setStrongSuffixes(endingMap, dictionary);
		setCYs(endingMap, dictionary);
		setVCCs(endingMap, dictionary);

		setSuffixTable(endingMap, dictionary);
		setPrefixes(wordService, dictionary);
		setAffixes(dictionary);

		setInflectionTable(wordService, dictionary);
		setDerivativeMap(wordService, dictionary);
		setMorphNodeMap(wordService, dictionary);

		this.dictionary = dictionary;
		return dictionary;
	}

	private void setVCCs(Map<Integer, List<Ending>> endingMap,
			Dictionary dictionary) {
		dictionary.setvCCs(endingMap
				.get(EndingType.VOWEL_CONSONANT_CONSONANT.getCode()).stream()
				.map(Ending::getName).collect(Collectors.toList()));
	}

	private void setCYs(Map<Integer, List<Ending>> endingMap,
			Dictionary dictionary) {
		dictionary.setcYs(endingMap.get(EndingType.CONSONANT_Y.getCode())
				.stream().map(Ending::getName).collect(Collectors.toList()));
	}

	private void setDerivativeMap(WordService wordService, Dictionary dictionary) {

		final String splitter = "\\s*,\\s*";

		Map<String, GraphNode> graphNodeMap = new HashMap<>();

		wordService.findAllDerivatives().forEach(d -> {

			String ks = d.getName();
			GraphNode stem = new GraphNode(ks);
			graphNodeMap.put(ks, stem);

			Arrays.stream(d.getDerivatives().split(splitter)).forEach(ds -> {
				GraphNode derivative = new GraphNode(ds);
				derivative.setStem(stem);
				stem.addDerivative(derivative);
				graphNodeMap.put(ds, derivative);
			});
		});

		dictionary.setDerivativeMap(graphNodeMap);
	}

	private void setMorphNodeMap(WordService wordService, Dictionary dictionary) {

		HashMap<String, MorphNode> morphNodeMap = new HashMap<>();
		wordService
				.findAllLeftRights()
				.forEach(
						n -> {

							String word = n.getName();
							MorphNode node = new MorphNode(word,
									MorphType.Word, null, null);

							String left = n.getLeft();
							String bareLeft = left.endsWith(Constants.HYPHEN) ? left
									.substring(0, left.length() - 1) : null;

							String right = n.getRight();
							String bareRight = right
									.startsWith(Constants.HYPHEN) ? right
									.substring(1) : null;

							if (bareLeft == null && bareRight == null) { // bull
																			// +
																			// pen
								node.setSon(new MorphNode(left, MorphType.Word,
										null, null));
								node.setDaughter(new MorphNode(right,
										MorphType.Word, null, null));

							} else if (bareLeft != null && bareRight != null) { // aero-
																				// +
																				// -phyte
								node.setSon(new MorphNode(bareLeft,
										MorphType.Prefix, null, null));
								node.setDaughter(new MorphNode(bareRight,
										MorphType.Suffix, null, null));

							} else if (bareLeft == null) { // adulterer + -ous
								node.setSon(new MorphNode(left, MorphType.Stem,
										null, null));
								node.setDaughter(new MorphNode(bareRight,
										MorphType.Suffix, null, null));

							} else { // ambi- + sexual
								node.setSon(new MorphNode(bareLeft,
										MorphType.Prefix, null, null));
								node.setDaughter(new MorphNode(right,
										MorphType.Stem, null, null));
							}

							morphNodeMap.put(word, node);

						});

		dictionary.setMorphNodeMap(morphNodeMap);
	}

	private void setInflectionTable(WordService wordService,
			Dictionary dictionary) {
		Map<String, Inflected> inflectionTable = new HashMap<>();
		wordService.findAllInflected().forEach(
				inf -> inflectionTable.put(inf.getName(), inf));
		dictionary.setInflectionTable(inflectionTable);
	}

	private void setAffixes(Dictionary dictionary) {
		List<String> affixes = new ArrayList<>(dictionary.getSuffixTable()
				.keySet());
		affixes.addAll(dictionary.getPrefixes());
		affixes.addAll(dictionary.getStrongSuffixes());

		dictionary.setAffixes(affixes);
	}

	private void setPrefixes(WordService wordService, Dictionary dictionary) {
		dictionary.setPrefixes(wordService.findAllPrefixes()
				.map(Prefix::getName).collect(Collectors.toList()));
	}

	private void setStrongSuffixes(Map<Integer, List<Ending>> endingMap,
			Dictionary dictionary) {
		dictionary.setStrongSuffixes(endingMap
				.get(EndingType.STRONG_SUFFIX.getCode()).stream()
				.map(Ending::getName).collect(Collectors.toList()));
	}

	private void setUnsplittables(Map<Boolean, List<Word>> wordMap,
			Dictionary dictionary) {
		dictionary.setUnsplittables(wordMap.get(false).stream()
				.map(Word::getName).collect(Collectors.toList()));
	}

	private void setWords(Map<Boolean, List<Word>> wordMap,
			Dictionary dictionary) {
		List<String> words = wordMap.get(true).stream().parallel()
				.map(Word::getName).collect(Collectors.toList());
		words.addAll(dictionary.getUnsplittables());
		dictionary.setWords(words);
	}

	private void setSuffixTable(Map<Integer, List<Ending>> endingMap,
			Dictionary dictionary) {
		LinkedHashMap<String, List<SuffixObj>> suffixTable = new LinkedHashMap<String, List<SuffixObj>>();

		endingMap.get(EndingType.DECOMPOSABLE.getCode()).stream().parallel()
				.forEachOrdered(e -> {
					suffixTable.put(e.getName(), e.buildSuffixes());
				});

		dictionary.setSuffixTable(suffixTable);
	}
}
