package han.jia.cloud.nlp.util;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the more specific and more efficient handler relative to the generic
 * ObjectHandler.
 * 
 * @author Jiayun Han
 */
public class PatternHandler {

	private final Pattern pattern;
	private final Consumer<Matcher> handler;
	private Matcher matcher;

	/**
	 * Constructs an instance
	 * 
	 * @param pattern
	 *            The pattern used to match a word
	 * @param handler
	 *            The consumer to process the matcher if it is able to do so
	 */
	public PatternHandler(Pattern pattern, Consumer<Matcher> handler) {
		this.pattern = pattern;
		this.handler = handler;
	}

	/**
	 * Check whether this pattern matches the word. It also instantiates the
	 * matcher object to be used in the future.
	 * 
	 * @param word
	 *            The word to be matched against the pattern
	 * 
	 * @return True if the pattern matches the word; false otherwise.
	 */
	public boolean match(String word) {
		matcher = pattern.matcher(word);
		return matcher.matches();
	}

	/**
	 * Handle the instantiated matcher resulted from matching a word against the
	 * pattern.
	 * 
	 * <p>
	 * We want this method to return true once it is invoked, regardless of its
	 * execution.
	 * 
	 * @return When this method is called, true is always returned, as the
	 *         matching process has been handled by another operation.
	 */
	@SuppressWarnings("finally")
	public boolean handle() {
		try {
			handler.accept(matcher);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} finally {
			return true;
		}
	}
}
