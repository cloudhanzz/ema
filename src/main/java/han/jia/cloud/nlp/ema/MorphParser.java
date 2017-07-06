package han.jia.cloud.nlp.ema;

import static han.jia.cloud.nlp.util.Constants.*;
import static han.jia.cloud.nlp.util.StringUtil.crudeRootSuffix;
import static han.jia.cloud.nlp.util.StringUtil.endWithListElement;
import static han.jia.cloud.nlp.util.StringUtil.findStemFromRight;
import static han.jia.cloud.nlp.util.StringUtil.splitEnding;
import static han.jia.cloud.nlp.util.StringUtil.splitFromRight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import han.jia.cloud.nlp.domain.Inflected;
import han.jia.cloud.nlp.enums.MorphType;
import han.jia.cloud.nlp.enums.QuasiIrregular;
import han.jia.cloud.nlp.enums.Singleton;
import han.jia.cloud.nlp.service.WordService;
import han.jia.cloud.nlp.util.Dictionary;
import han.jia.cloud.nlp.util.StringUtil;

/**
 * The tool to parse an English word.
 * 
 * @author Jiayun Han
 *
 */
public class MorphParser {

	private static final int MIN_OKs = 1;
	// for caching purpose
	private Map<String, MorphNode> wordCache = new HashMap<String, MorphNode>();
	private Map<String, String[]> stemSuffixMap = new HashMap<String, String[]>();
	private Map<String, String[]> prefixStemMap = new HashMap<String, String[]>();

	// used to catch cyclicity
	private String wordBeingParsed;

	// The root of the word being parsed
	private String wordRoot;

	private boolean isFirstTime;

	private final Dictionary dictionary;

	public MorphParser(WordService wordService) {
		dictionary = Singleton.INSTANCE.getDictionary(wordService);
	}

	/**
	 * Parses a lexicon into a {@code MorphNode} object
	 * 
	 * @param text
	 *            The text to be parsed
	 * 
	 * @return The result of parsing the lexicon, represented as a
	 *         {@code MorphNode} object
	 */
	public MorphNode parse(String text) {

		if (wordCache.containsKey(text)) {
			return wordCache.get(text);
		}

		String lexicon = text.trim().split(SPACE)[0];

		isFirstTime = true;
		wordBeingParsed = lexicon;

		trySetWordRoot(lexicon, true);
		MorphNode node = parseAux(lexicon, MorphType.Word, true);

		markRoots(node);
		wordCache.put(text, node);

		return node;
	}

	/**
	 * This is the helper method of the morph parser.
	 * 
	 * @param lexicon
	 *            The text to be parsed
	 * 
	 * @param type
	 *            The type of the lexicon, which is one of the 6 types:
	 *            Inflection, Prefix, Suffix, Stem, Root, Word
	 * 
	 * @param checkQuasiIrreg
	 *            Whether to check quasi irregular ending. Quasi irregular
	 *            ending like schema -> schemata, phenomenon -> phenomena
	 * 
	 * @return The result of parsing the lexicon, represented as a
	 *         {@code MorphNode} object
	 * 
	 * @see han.jia.cloud.nlp.enums.MorphType
	 */
	private MorphNode parseAux(String lexicon, MorphType type, boolean checkQuasiIrreg) {

		if (wordCache.containsKey(lexicon)) {
			return wordCache.get(lexicon);
		}

		MorphNode node = new MorphNode(lexicon, type);
		if (dictionary.getUnsplittables().contains(lexicon)
				|| (isFirstTime == false && wordBeingParsed != null && wordBeingParsed.equals(lexicon))) {
			wordCache.put(lexicon, node);
			return node;
		}

		isFirstTime = false;

		node = deInflect(lexicon, type, checkQuasiIrreg);

		bootstrap(node);

		if (node.getSon() == null && node.getDaughter() == null) {
			MorphNode node2 = lastResort(node, lexicon, type);
			if (node2 != null) {
				wordCache.put(lexicon, node2);
				return node2;
			}
		}

		markRoots(node);
		int oks = node.validate(dictionary);
		if (oks == 0) {
			if (node.getSon() != null) {
				node = new MorphNode(lexicon, type);
			}
		}

		wordCache.put(lexicon, node);
		return node;
	}

	private MorphNode deInflect(String word, MorphType type, boolean checkQuasiIrreg) {

		if (word == null || word.isEmpty()) {
			return null;
		}

		MorphNode node = new MorphNode(word, type);
		if (dictionary.getUnsplittables().contains(word)) {
			return node;
		}

		if (dictionary.getMorphNodeMap().containsKey(word)) {
			return dictionary.getMorphNodeMap().get(word).type(type);
		}

		boolean done = checkInflection(word, node);

		if (!done) {
			if (checkQuasiIrreg) {
				done = checkQuasiIrregulars(word, node);
			}

			if (!done) {
				done = checkCieEnding(word, node);
				if (!done) {
					done = checkVccEnding(word, node);
					if (!done) {
						done = checkEdErEstIng(word, node);
						if (!done) {
							done = checkEndingS(word, node);
						}
					}
				}
			}
		}

		return node;
	}

	private boolean checkInflection(String word, MorphNode node) {
		boolean done = false;
		if (dictionary.getInflectionTable().containsKey(word)) {
			Inflected inflected = dictionary.getInflectionTable().get(word);
			done = checkInflectionAndSet(inflected.getBase(), inflected.getInflection(), node);
		}
		return done;
	}

	private boolean checkQuasiIrregulars(String word, MorphNode node) {

		return Arrays.stream(QuasiIrregular.values()).filter(q -> q.matches(word)).map(q -> checkQuasi(q, node))
				.filter(e -> e == true).findAny().orElse(false);
	}

	private boolean checkQuasi(QuasiIrregular irreg, MorphNode node) {

		boolean done = false;
		Matcher matcher = irreg.getPattern().matcher(node.getText());
		if (matcher.matches()) {
			String head = matcher.group(1);
			String stem = head + irreg.getAddOn();
			String inflection = irreg.getInflection();
			done = checkInflectionAndSet(stem, inflection, node);
		}

		return done;
	}

	private boolean checkCieEnding(String word, MorphNode node) {

		boolean done = false;

		if (endWithListElement(word, dictionary.getcYs()) != -1) {

			// word ending with consonant + ie + others, e.g. cities, happier
			String[] headTail = crudeRootSuffix(word, dictionary.getcYs());

			if (headTail != null) {
				String stemHead = headTail[0];
				String roughTail = headTail[1];

				headTail = splitEnding(roughTail);

				String stemCarriedOver = headTail[0];
				String stem = stemHead + stemCarriedOver;
				String inflection = headTail[1];

				done = checkInflectionAndSet(stem, inflection, node);
			}
		}
		return done;
	}

	private boolean checkVccEnding(String str, MorphNode node) {
		boolean done = false;
		if (endWithListElement(str, dictionary.getvCCs()) != -1) {

			String[] headTail = crudeRootSuffix(str, dictionary.getvCCs());
			if (headTail != null) {
				String stemHead = headTail[0];
				String roughTail = headTail[1];

				// otter -> [ot, er]
				headTail = splitEnding(roughTail);

				String stemCarriedover = headTail[0];
				String stem = stemHead + stemCarriedover; // h + ot = hot
				String inflection = headTail[1]; // er
				done = checkInflectionAndSet(stem, inflection, node);

			}
		}
		return done;
	}

	private boolean checkEdErEstIng(String str, MorphNode node) {
		boolean done = false;
		if (endWithListElement(str, dictionary.getEdErEstIng()) != -1) {
			String[] stemInflection = StringUtil.cutWhenEndsWithErEdEstIng(str, dictionary.getWords());
			String stem = stemInflection[0];
			String inflection = stemInflection[1];
			done = checkInflectionAndSet(stem, inflection, node);
		}
		return done;
	}

	private boolean checkEndingS(String word, MorphNode node) {
		boolean done = false;
		if (word.endsWith(s)) {
			done = checkInflectionAndSet(word.substring(0, word.length() - 1), s, node);
			if (!done) {
				if (endWithListElement(word, dictionary.getEsEnding()) != -1) {
					String[] headTail = crudeRootSuffix(word, dictionary.getEsEnding());
					String stemHead = headTail[0];
					String roughTail = headTail[1];

					headTail = splitEnding(roughTail);
					String stemCarriedover = headTail[0];
					String inflection = headTail[1];
					String stem = stemHead + stemCarriedover;

					done = checkInflectionAndSet(stem, inflection, node);
				}
			}
		}
		return done;
	}

	private boolean checkInflectionAndSet(String stem, String inflection, MorphNode node) {
		boolean done = false;
		if (dictionary.getWords().contains(stem)) {

			done = true;
			trySetWordRoot(stem, false);

			node.setSon(new MorphNode(stem, MorphType.Stem));
			node.setDaughter(new MorphNode(inflection, MorphType.Inflection));
		} else {
			node.setSon(null);
			node.setDaughter(null);
		}
		return done;
	}

	private String[] toPrefixStem(String word) {

		if (word == null) {
			return null;
		}

		if (prefixStemMap.containsKey(word)) {
			return prefixStemMap.get(word);
		}

		String[] pair = null;

		// min-length = 4, 'abed'
		if (word.length() >= 4 && !dictionary.getUnsplittables().contains(word)) {

			int maxOks = MIN_OKs;
			for (String prefix : dictionary.getPrefixes()) {

				if (word.startsWith(prefix)) {
					int cutIndexFromLeft = word.contains(HYPHEN) ? prefix.length() + 1 : prefix.length();
					String stem = word.substring(cutIndexFromLeft);

					if (stem.length() < 3) {
						continue;
					} else if (prefix.equals(ir)) {
						if (!stem.startsWith(r)) {
							continue;
						}
					} else if (prefix.equals(il)) {
						if (!stem.startsWith(l)) {
							continue;
						}
					} else if (prefix.equals(im)) {
						if (!stem.startsWith(m) && !stem.startsWith(p) && !stem.startsWith(b)) {
							continue;
						}
					} else if (prefix.equals(in)) {
						if (stem.startsWith(m) || stem.startsWith(p) || stem.startsWith(b) || stem.startsWith(l)
								|| stem.startsWith(r)) {
							continue;
						}
					}

					boolean done = false;
					String stemRoot = getRoot(stem);
					if (wordRoot != null && stemRoot != null && stemRoot.equals(wordRoot)) {
						done = true;
					} else {
						if (stemRoot != null) {
							String theWordRoot = getRoot(word);
							if (theWordRoot != null && theWordRoot.equals(stemRoot)) {
								done = true;
							}
						}
					}

					if (done) {
						pair = new String[] { prefix, stem };
						wordRoot = stemRoot;
						break;
					}

					MorphNode morphNode = parseAux(stem, MorphType.Stem, true);
					markRoots(morphNode);
					int oks = morphNode.validate(dictionary);
					if (oks > maxOks) {
						maxOks = oks;
						pair = new String[] { prefix, stem };
					}
				}
			}
		}

		prefixStemMap.put(word, pair);
		return pair;
	}

	private void bootstrap(MorphNode node) {

		if (node == null || (node.getType() != MorphType.Word && node.getType() != MorphType.Stem)
				|| dictionary.getUnsplittables().contains(node.getText())) {
			return;
		}

		if (node.getSon() == null && node.getDaughter() == null) {
			String[] stemSuffix = toStemSuffix(node.getText());

			if (stemSuffix != null) {
				trySetWordRoot(stemSuffix[0], false);
				useSuffix(node, stemSuffix);
			} else {
				String[] prefixStem = toPrefixStem(node.getText());
				if (prefixStem != null) {
					trySetWordRoot(prefixStem[1], false);
					usePrefix(node, prefixStem);
				}
			}

		} else {
			if (node.getSon() != null
					&& (node.getSon().getType() == MorphType.Word || node.getSon().getType() == MorphType.Stem)) {
				MorphNode newSon = parseAux(node.getSon().getText(), MorphType.Stem, false);
				node.setSon(newSon);
			}

			if (node.getDaughter() != null && (node.getDaughter().getType() == MorphType.Word
					|| node.getDaughter().getType() == MorphType.Stem)) {
				MorphNode newDau = parseAux(node.getDaughter().getText(), MorphType.Stem, false);
				node.setDaughter(newDau);
			}
		}
	}

	private MorphNode lastResort(MorphNode node, String lexicon, MorphType type) {

		List<String[]> stemInflPairs = simpleDeinflect(lexicon);
		if (stemInflPairs != null && !stemInflPairs.isEmpty()) {

			for (String[] stemInflPair : stemInflPairs) {
				if (stemInflPair == null) {
					continue;
				}

				String stem = stemInflPair[0];
				String infl = stemInflPair[1];

				MorphNode node2 = parseAux(stem, type, false);

				if (node2 != null && node2.getSon() != null && node2.getDaughter() != null) {
					MorphNode node3 = (MorphNode) node2.clone();
					node3.setText(node.getText());
					node2.downgrade();
					node3.setSon(node2);
					node3.setDaughter(new MorphNode(infl, MorphType.Inflection));
					return node3;
				}
			}
		}

		return null;
	}

	/**
	 * Use breadth-first search algorithm to break a word into its stem and
	 * suffix
	 * 
	 * @param word
	 * @return
	 */
	private String[] toStemSuffix(String word) {

		if (word == null) {
			return null;
		}

		if (stemSuffixMap.containsKey(word)) {
			return stemSuffixMap.get(word);
		}

		String[] pair = null;

		// min-length = 3, as in 'icy'
		if (word.length() >= 3 && !dictionary.getUnsplittables().contains(word)) {

			int maxOks = MIN_OKs;
			outer: for (String ending : dictionary.getSuffixTable().keySet()) {

				if (word.endsWith(ending)) {
					List<SuffixObj> suffObjs = dictionary.getSuffixTable().get(ending);

					for (SuffixObj suffObj : suffObjs) {
						int cutIndexFromRight = suffObj.getCutPosition();
						String addon = suffObj.getAddon();
						String stem = findStemFromRight(word, cutIndexFromRight, addon);
						if (stem == null) {
							continue;
						}

						if (addon.equals(MARKER_1)) {
							String tail = word.replace(stem, EMPTY_STR);
							if (tail.isEmpty()) {
								continue;
							}

							char cLast = stem.charAt(stem.length() - 1);
							char cFirst = tail.charAt(0);
							if (cLast != cFirst) {
								continue;
							}
						}

						boolean done = false;
						String stemRoot = getRoot(stem);

						if ((dictionary.getWords().contains(stem) && dictionary.getStrongSuffixes().contains(ending))
								|| (wordRoot != null && stemRoot != null && stemRoot.equals(wordRoot))) {
							done = true;
						} else {
							if (stemRoot != null) {
								String theWordRoot = getRoot(word);
								if (theWordRoot != null && theWordRoot.equals(stemRoot)) {
									done = true;
								}
							}
						}

						if (done) {
							pair = new String[] { stem, ending };
							wordRoot = stemRoot;
							break outer;
						}

						MorphNode morphNode = parseAux(stem, MorphType.Stem, true);
						markRoots(morphNode);
						int oks = morphNode.validate(dictionary);
						if (oks > maxOks) {
							maxOks = oks;
							pair = new String[] { stem, ending };
						}
					}
				}
			}
		}

		stemSuffixMap.put(word, pair);
		return pair;
	}

	private void useSuffix(MorphNode node, String[] stemSuffix) {

		if (node != null && stemSuffix != null) {
			String stem = stemSuffix[0];
			String suffix = stemSuffix[1];

			node.setDaughter(new MorphNode(suffix, MorphType.Suffix));
			MorphNode son = parseAux(stem, MorphType.Stem, false);
			node.setSon(son);
		}
	}

	private void usePrefix(MorphNode node, String[] prefixStem) {
		if (node != null && prefixStem != null) {
			node.setSon(new MorphNode(prefixStem[0], MorphType.Prefix));
			MorphNode dau = parseAux(prefixStem[1], MorphType.Stem, false);
			node.setDaughter(dau);
		}
	}

	private List<String[]> simpleDeinflect(String word) {
		if (word == null || word.length() < 4 || dictionary.getUnsplittables().contains(word)) {
			return null;
		}

		List<String[]> stemInflPairs = new ArrayList<String[]>();

		if (word.length() >= 5 && word.endsWith(ies)) {
			stemInflPairs.add(splitFromRight(word, 3, y));
		} else if (endWithListElement(word, dictionary.getEsEnding()) != -1) {
			stemInflPairs.add(splitFromRight(word, 2));
		} else if (word.endsWith(s) && !word.endsWith(ss)) {
			stemInflPairs.add(splitFromRight(word, 1));
		} else if (word.length() >= 5 && word.endsWith(ves)) {
			stemInflPairs.add(splitFromRight(word, 1));
			stemInflPairs.add(splitFromRight(word, 3, fe));
		} else if (word.length() >= 5 && word.endsWith(er) || word.endsWith(ed)) {
			stemInflPairs.add(splitFromRight(word, 1));
			stemInflPairs.add(splitFromRight(word, 2));
			stemInflPairs.add(splitFromRight(word, 3));
			stemInflPairs.add(splitFromRight(word, 3, y));
		} else if (word.length() >= 5 && word.endsWith(est)) {
			stemInflPairs.add(splitFromRight(word, 2));
			stemInflPairs.add(splitFromRight(word, 3));
			stemInflPairs.add(splitFromRight(word, 4));
		} else if (word.length() >= 5 && word.endsWith(ing)) {
			stemInflPairs.add(splitFromRight(word, 3));
			stemInflPairs.add(splitFromRight(word, 3, e));
			stemInflPairs.add(splitFromRight(word, 4));
		}
		return stemInflPairs;
	}

	/**
	 * Mars the root of the passed morph node.
	 * 
	 * <p>
	 * A morph node has no root if it is an inflection, a prefix, or a suffix;
	 * it may have one root, e.g exciting, or more than one root, e.g. bulldog.
	 * 
	 * <p>
	 * This is a recursive method.
	 * 
	 * @param node
	 *            The morph node whose root(s) to be marked out
	 */
	private void markRoots(MorphNode node) {

		if (node == null || node.getType() == MorphType.Inflection || node.getType() == MorphType.Prefix
				|| node.getType() == MorphType.Suffix) {
			return;
		}

		if (node.getSon() == null && node.getDaughter() == null) {
			node.setType(MorphType.Root);
			return;
		}

		markRoots(node.getSon());
		markRoots(node.getDaughter());
	}

	/**
	 * Tries to set the root of the word being parsed, if it is still not set or
	 * forced to do so.
	 * 
	 * <p>
	 * Depending on whether the word exists in the derivative dictionary, the
	 * root of this word may or may not be set at the end of this operation.
	 * 
	 * @param word
	 *            The word whose root to be set
	 * @param forceReset
	 *            true to try this operation; false to return with no operation.
	 */
	private void trySetWordRoot(String word, boolean forceReset) {
		if (forceReset || wordRoot == null) {
			wordRoot = getRoot(word);
		}
	}

	/**
	 * Returns the root of a word.
	 * 
	 * @param word
	 *            The word whose root is to be found
	 * 
	 * @return Three possibilities: {@literal null} if the word does not exist
	 *         in the derivative dictionary, otherwise the entire word if the
	 *         word has no stem or the root otherwise.
	 */
	private String getRoot(String word) {
		String root = null;
		GraphNode graphNode = dictionary.getDerivativeMap().get(word);
		if (graphNode != null) {
			root = graphNode.findRoot();
		}

		return root;
	}
}
