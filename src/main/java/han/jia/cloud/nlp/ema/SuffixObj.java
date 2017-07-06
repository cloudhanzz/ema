package han.jia.cloud.nlp.ema;

import java.util.ArrayList;
import java.util.List;

/**
 * An object representation of a suffix.
 * 
 * @author Jiayun Han
 *
 */
public class SuffixObj {

	private static final String COMMA = "\\s*,\\s*";

	// e.g. for 'bility' in 'ability'

	// The index at which to split a word, e.g. 5 from right
	int cutPosition;

	// The string to add to the left-hand side after being split to restore the
	// word, e.g.
	String addon;

	// The acual suffix, e.g. ty
	String suffix;

	/**
	 * Instantiates a SuffixObj object
	 * 
	 * @param cutPosition
	 *            The index from where to split a word
	 * @param addon
	 *            The string to add back to the head of the split word
	 * @param suffix
	 *            The suffix resulted from the splitting
	 */
	public SuffixObj(int cutPosition, String addon, String suffix) {
		this.cutPosition = cutPosition;
		this.addon = addon;
		this.suffix = suffix;
	}

	/**
	 * 
	 * @return The index at which to split a word
	 */
	public int getCutPosition() {
		return cutPosition;
	}

	/**
	 * 
	 * @return The string to add to the left-hand side after being split to
	 *         restore the word
	 */
	public String getAddon() {
		return addon;
	}

	/**
	 * 
	 * @return The acual suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	public static List<SuffixObj> parse(String cutWays, String suffix) {

		List<SuffixObj> suffixObjs = new ArrayList<SuffixObj>();
		String[] variations = cutWays.split(COMMA);

		for (int j = 0; j < variations.length; j++) {
			String variation = variations[j];
			int cutPosition = Integer.parseInt(variation.substring(0, 1));
			String addon = variation.substring(1);

			SuffixObj suffixObj = new SuffixObj(cutPosition, addon, suffix);
			suffixObjs.add(suffixObj);
		}

		return suffixObjs;
	}
}
