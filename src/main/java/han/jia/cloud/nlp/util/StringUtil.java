package han.jia.cloud.nlp.util;

import java.util.List;

import han.jia.cloud.nlp.util.Constants;

/**
 * A utility class for processing strings.
 * 
 * @author Jiayun Han
 *
 */
public final class StringUtil {

	/**
	 * Tries to cut the word into two parts with the cutting position at the
	 * word.length() - ending.length() where ending is the first of the endings
	 * that the string ends with.
	 * 
	 * <p>
	 * For example, word = cities; endings = {ied, ier, ies ...}, then the word
	 * will be cut into {cit, ies}
	 * 
	 * @param word
	 *            The word to be split into two parts
	 * @param endings
	 *            The endings for which the word tries to match its ending
	 * 
	 * @return A two string array if the word ends with one of the endings, null
	 *         if no ending matches
	 */
	public static String[] crudeRootSuffix(String word, List<String> endings) {

		int index = endWithListElement(word, endings);
		if (index != -1) {
			String ending = endings.get(index);
			String crudeBase = word.substring(0,
					word.length() - ending.length());
			return new String[] { crudeBase, ending };
		}
		return null;
	}

	/**
	 * Tries to cut the word into its root and suffix when the word ends with
	 * er, ed, est, or ing
	 * 
	 * @param word
	 *            The word to be split
	 * 
	 * @param words
	 *            The word list used to validate the resulted root
	 * @return The root and the suffix of the word
	 */
	public static String[] cutWhenEndsWithErEdEstIng(String word,
			List<String> words) {

		String[] rs = new String[2];
		String tryWord;

		if (word.endsWith(Constants.est) || word.endsWith(Constants.ing)) {
			// append 'e' to the part without 'est'/'ing' to form a try-word.
			// If the try-word is a real word, it serves as the first element
			// of the array and the last 3 letters forms the second element.
			// e.g.,
			// hoping -> [hop, ing]: hop + e = hope, so: hoping -> [hope, ing]

			tryWord = word.substring(0, word.length() - 3) + Constants.e;

			// if tryWord is a real word
			if (words.contains(tryWord)) // hoping
			{
				rs[0] = tryWord;
				rs[1] = word.substring(tryWord.length() - 1); // offset the
																// added 'e'
			} else // working
			{
				// if tryWord is not a real word, take the original part without
				// 'est' / 'ing'. e.g. working -> [work, ing]: work + e = worke
				// (not real word), so: working -> [work, ing]

				tryWord = word.substring(0, word.length() - 3);
				rs[0] = tryWord;
				rs[1] = word.substring(tryWord.length());
			}
		} else {
			// if it does not end with 'est' or 'ing', it ends with either 'er'
			// or 'ed'. Check 'e-dropping' similarly.

			tryWord = word.substring(0, word.length() - 1); // lover

			// lover -> [love,er]
			if (words.contains(tryWord)) {
				rs[0] = tryWord; // love
				rs[1] = word.substring(tryWord.length() - 1); // er or ed
			} else {
				// worker
				tryWord = word.substring(0, word.length() - 2);
				rs[0] = tryWord;
				rs[1] = word.substring(tryWord.length());
			}
		}

		return rs;
	}

	/**
	 * Returns the index of the ending of the endings that the word ends with
	 * 
	 * @param word
	 *            The word whose ending is to be checked
	 * @param endings
	 *            The ending to search against
	 * @return The index of the ending of the endings that the word ends with or
	 *         -1 if search fails
	 */
	public static int endWithListElement(String word, List<String> endings) {
		for (int i = 0; i < endings.size(); i++) {
			if (word.endsWith(endings.get(i)))
				return i;
		}

		return -1;
	}

	/**
	 * Returns the stem of the word found from right to left
	 * 
	 * @param word
	 *            The word whose stem to be found
	 * 
	 * @param offset
	 *            Used to adjust the cutting position
	 * 
	 * @param extras
	 *            The extra strings to be added to the resulted stem
	 * 
	 * @return the found stem or null if the offset is longer than word length
	 */
	public static String findStemFromRight(String word, int offset,
			String... extras) {
		String[] pair = splitFromRight(word, offset, extras);
		if (pair == null) {
			return null;
		}

		return pair[0];
	}

	/**
	 * Cuts the word at the desired position and returns the resulted 2 string
	 * array
	 * 
	 * @param word
	 *            The word to be cut
	 * @param offset
	 *            Used to adjust the cutting position
	 * @param extras
	 *            The extra strings to be added back to the resulted head string
	 * @return the resulted 2 string array if cutting is possible; null
	 *         otherwise
	 */
	public static String[] splitFromRight(String word, int offset,
			String... extras) {
		int cut = word.length() - offset;

		if (cut > 0) {
			String[] headTail = { word, Constants.EMPTY_STR };
			String head = word.substring(0, cut);
			String tail = word.substring(cut);

			for (String extra : extras)
				if (!extra.equals(Constants.MARKER_1)) {
					head += extra;
				}

			headTail[0] = head;
			headTail[1] = tail;

			return headTail;
		}

		return null;
	}

	/**
	 * Returns the two parts resulted from splitting the passed ending
	 * 
	 * @param ending
	 *            The ending to be split
	 * @return the two parts resulted from splitting the passed ending
	 */
	public static String[] splitEnding(String ending) {
		String[] ss = new String[2];

		// [bu]shes, [bo]xes, [kni]ves, but not [cit]ies
		if (ending.endsWith(Constants.es) && !ending.endsWith(Constants.ies)) {
			ss[0] = ending.startsWith(Constants.v) ? Constants.f : ending
					.substring(0, ending.length() - 2);
			ss[1] = Constants.es;
		} else if (ending.charAt(1) == ending.charAt(2)) {
			// [h]otter, [beg]inner, [b]egged
			ss[0] = ending.substring(0, 2);
			ss[1] = ending.substring(3);
		} else {
			// [ci]ties, [hur]ried
			ss[0] = ending.charAt(0) + Constants.y; // e.g. t + y
			ss[1] = ending.substring(2); // e.g. 'er' in [car]rier
		}

		return ss;
	}
}
