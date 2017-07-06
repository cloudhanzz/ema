package han.jia.cloud.nlp.ema;

import static java.util.stream.Collectors.toList;
import static han.jia.cloud.nlp.util.Constants.*;

import han.jia.cloud.nlp.enums.MorphType;
import han.jia.cloud.nlp.util.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class models a word's internal structure and provides means to
 * manipulate its morphemes.
 * 
 * @author Jiayun Han
 *
 */
public class MorphNode {

	private static boolean called;

	// The depth of this Node. Depth number starts from 0.
	private int depth;

	/**
	 * The text of this Node, which can be the whole word or one of its
	 * components, depending on its depth
	 */
	private String text;

	/**
	 * The morphological type of this Node. It is one of the 6 enum constants.
	 */
	private MorphType morphType;

	/**
	 * The left sub-node of this Node
	 */
	private MorphNode son;

	/**
	 * The right sub-node of this Node
	 */
	private MorphNode daughter;

	/**
	 * It creates a new instance of MorphNode, using the provided parameters.
	 * 
	 * @param text
	 *            The text of this MorphNode
	 * @param morphType
	 *            The MorphType of this MorphNode
	 * @param son
	 *            The left sub-MorphNode of this MorphNode
	 * @param daughter
	 *            The right sub-MorphNode of this MorphNode
	 */
	public MorphNode(String text, MorphType morphType, MorphNode son, MorphNode daughter) {
		setText(text);
		setType(morphType);
		setSon(son);
		setDaughter(daughter);
	}

	/**
	 * It creates a new instance of MorphNode with no son or daughter, using the
	 * provided parameters.
	 * 
	 * @param text
	 *            The text of this MorphNode
	 * @param morphType
	 *            The MorphType of this MorphNode
	 */
	public MorphNode(String text, MorphType morphType) {
		this(text, morphType, null, null);
	}

	/**
	 * @return The depth of this Node
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the depths of this Node.
	 * 
	 * @param depth
	 *            The value of the depth to be set.
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return The text of this Node, which can be the whole word or one of its
	 *         components.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of this Node.
	 * 
	 * @param text
	 *            The new text to be assigned to this Node.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return The morphological type of this Node. It is one of the 6 enum
	 *         constants.
	 */
	public MorphType getType() {
		return morphType;
	}

	/**
	 * Sets the type of this Node.
	 * 
	 * @param type
	 *            The new type to be assigned to this Node.
	 */
	public void setType(MorphType type) {
		this.morphType = type;
	}

	/**
	 * Sets the type of this Node and return this instance itself to chaining
	 * operations
	 * 
	 * @param type
	 *            The new type to be assigned to this Node.
	 * 
	 * @return This instance itself to chaining operations
	 */
	public MorphNode type(MorphType type) {
		this.morphType = type;
		return this;
	}

	/**
	 * @return The left sub-node of this Node
	 */
	public MorphNode getSon() {
		return son;
	}

	/**
	 * Sets the left-node of this Node.
	 * 
	 * @param node
	 *            The value to be assigned to the son of this Node.
	 */
	public void setSon(MorphNode node) {
		if (node != null) {
			this.son = node;
		}
	}

	/**
	 * @return The right sub-node of this Node
	 */
	public MorphNode getDaughter() {
		return daughter;
	}

	/**
	 * Sets the right-node of this Node.
	 * 
	 * @param node
	 *            The value to be assigned to the daughter of this Node.
	 */
	public void setDaughter(MorphNode node) {
		if (node != null) {
			this.daughter = node;
		}
	}

	/**
	 * It retrieves the son and daughter of a MorphNode at the specified depth.
	 * 
	 * @param depth
	 *            The depth at which the son and daughter to be retrieved
	 * 
	 * @return The son and daughter of a MorphNode at the specified depth. The
	 *          result is one of the 3 cases:
	 *          <ul>
	 *          <li>null if depth is greater than the depth of this MorphNode</li>
	 *          <li>An array of 2 null elements if this Node has no instantiated
	 *          children at the specified depth</li>
	 *          <li>The instantiated son and daughter of this Node</li>
	 *          </ul>
	 */
	public MorphNode[] getChildren(int depth) {
		// stop case 1: found instantaited son and daughter
		if (this.depth == depth) {
			return new MorphNode[] { son, daughter };
		}

		// stop case 2: no son means no daughter, therefore
		// return null. This occurs when depth > node's deepest depth.
		if (son == null) {
			return null;
		}

		// recursion: case 3: has instantiated son-side grandchildren,
		// search and return the son's children.
		if (son.getSon() != null) {
			return son.getChildren(depth);
		}

		// recursion: case 4: has daughter-side grandchildren,
		// search and return the daughter's children.
		return daughter.getChildren(depth);
	}

	/**
	 * Downgrades this MorphNode by increasing its depth and the depths of all
	 * of its child MorphNodes by 1 recursively.
	 */
	public void downgrade() {

		this.setDepth(depth++);

		if (morphType == MorphType.Word) {
			morphType = MorphType.Stem;
		}

		if (son != null)
			son.downgrade();

		if (daughter != null)
			daughter.downgrade();
	}

	private static String catStrs(int times, String symbol) {

		String ss = EMPTY_STR;
		for (int i = 0; i < times; i++) {
			ss += symbol;
		}

		return ss;
	}

	/**
	 * Returns a pretty-formatted string representation of this instance.
	 * 
	 * @param times
	 *            The number of times to repeat the same string marker
	 * 
	 * @param hMarker
	 *            The string marker for connect strings horizontally
	 * 
	 * @param vMarker
	 *            The string marker for connect strings vertically
	 * 
	 * @return A pretty-formatted string representation of this instance
	 */
	public String prettyPrint(int times, String hMarker, String vMarker) {

		String indentation = catStrs(hMarker.length(), ONE_SPACE);
		String pipes = catStrs(times, vMarker + ONE_SPACE);
		String s = pipes + hMarker + text + LEFT_BRACE + this.morphType + RIGHT_BRACE;

		if (son != null) {

			times++;
			pipes += vMarker + ONE_SPACE;

			if (!called) {
				called = true;

				// 1 accounts for '-' in '+-'
				indentation = catStrs(times + 1, ONE_SPACE);
			}

			String sonStr = son == null ? EMPTY_STR : son.prettyPrint(times, hMarker, vMarker);
			String daughterStr = daughter == null ? EMPTY_STR : daughter.prettyPrint(times, hMarker, vMarker);

			s += NL + indentation + sonStr;
			s += NL + indentation + pipes + vMarker;
			s += NL + indentation + daughterStr;
		}

		return s;
	}

	/**
	 * Returns the string representation of this instance
	 * 
	 * @return the string representation of this instance
	 */
	public String toString() {
		called = false;
		return prettyPrint(0, "+-", "|");
	}

	/**
	 * Clones a MorphNode out of this Node.
	 * 
	 * @return A new MorphNode cloned out of this Node
	 */
	@Override
	public Object clone() {

		MorphNode node = new MorphNode(text, morphType, null, null);

		if (son != null) {
			node.setSon((MorphNode) son.clone());
		}

		if (daughter != null) {
			node.setDaughter((MorphNode) daughter.clone());
		}

		return node;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MorphNode) {
			MorphNode that = (MorphNode) o;
			return this.text.equals(that.text);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.text.hashCode();
	}

	/**
	 * Returns the content morphemes of this instance.
	 * <p>
	 * Content morphemes in this application refers to all types of morphemes
	 * but inflections.
	 * 
	 * @return The content morphemes of this instance
	 */
	public List<String> findContentMorphemes() {
		return findMorphemes(m -> m.getType() != MorphType.Inflection);
	}

	/**
	 * Returns the content morphemes of this instance of the specified morph
	 * types.
	 * <p>
	 * Content morphemes in this application refers to all types of morphemes
	 * but inflections.
	 * 
	 * @param type
	 *            The specific type of the content morphemes to be returned
	 * @return The content morphemes of this instance of the specified morph
	 *         types
	 */
	public List<String> findContentMorphemes(MorphType type) {
		return findMorphemes(m -> m.getType() == type);
	}

	/**
	 * Returns all prefixes of this instance. A word may have more than one
	 * prefix, like 'anti' and 'en' in 'anti-enslavement'
	 * 
	 * @return all prefixes of this instance
	 */
	public List<String> findPrefixes() {
		return findMorphemes(m -> m.getType() == MorphType.Prefix);
	}

	/**
	 * Returns all suffixes of this instance. A word may have more than one
	 * sufffix, like 'ize' and 'tion' in 'modernization'
	 * 
	 * @return all suffixes of this instance
	 */
	public List<String> findSuffixes() {
		return findMorphemes(m -> m.getType() == MorphType.Suffix);
	}

	/**
	 * Returns all stems of this instance. A word may have more than one stem.
	 * 
	 * @return The stems of this instance
	 */
	public List<String> findStems() {
		return findMorphemes(m -> m.getType() == MorphType.Stem);
	}

	/**
	 * Returns all roots of this instance. Though not common, a word may have
	 * more than one root, like bulldog.
	 * 
	 * @return The roots of this instance
	 */
	public List<String> findRoots() {
		return findMorphemes(m -> m.getType() == MorphType.Root);
	}

	/**
	 * The morphems that satisfy the filter
	 * 
	 * @param filter
	 *            The predicate used to filter the desired morphemes
	 *            
	 * @return The morphems that satisfy the filter
	 */
	public List<String> findMorphemes(Predicate<MorphNode> filter) {
		return findAllMorphemes().stream().filter(filter).map(MorphNode::getText).collect(toList());
	}

	/**
	 * Returns all morphemes of this morph node
	 * 
	 * @return All morphemes of this morph node, regardless of their types.
	 */
	public List<MorphNode> findAllMorphemes() {
		List<MorphNode> nodes = new ArrayList<>();
		findAllMorphemesAux(nodes);
		return nodes;
	}

	/**
	 * This is the actual method that recursively find and collect all morphemes
	 * of this node.
	 * 
	 * @param stack
	 *            as suggested by the name, it serves as the stack to collect
	 *            the found morphemes.
	 */
	private void findAllMorphemesAux(List<MorphNode> stack) {

		if (son == null && daughter == null) {
			stack.add(this);
			return;
		}

		if (son != null) {
			son.findAllMorphemesAux(stack);
		}

		if (daughter != null) {
			daughter.findAllMorphemesAux(stack);
		}
	}

	/**
	 * It checks the validity of this MorphNode by examining its content
	 * morphemes.
	 * 
	 * @param dictionary The dictionary used for validation
	 * 
	 * @return The number of passing checks
	 */
	public int validate(Dictionary dictionary) {

		int oks = 0;
		List<String> roots = findRoots();

		for (String root : roots) {
			if (dictionary.getWords().contains(root)) {
				oks++;
			}
		}

		List<String> contentMorphemes = findContentMorphemes();

		for (String morpheme : contentMorphemes) {

			GraphNode graphNode = dictionary.getDerivativeMap().get(morpheme);
			if (graphNode != null) {
				String root = graphNode.findRoot();
				if (roots.contains(root)) {
					oks++;
				}
			}
		}

		return oks;
	}

	/**
	 * Sets the depths of the word and its left (son) and right (daughter)
	 * components, recursively. The depths setting stops when a component
	 * becomes {@literal null}.
	 * 
	 * <p>
	 * This method relies on the recursive helper method.
	 */
	public void organizeDepths() {
		organizeDepthsAux(0);
	}

	/**
	 * Sets the depths of the word and its left (son) and right (daughter)
	 * components, recursively. The depths setting stops when a component
	 * becomes {@literal null}.
	 * 
	 * @param initialDepth
	 *            The value of the initial depth, the following value is based
	 *            on this one incrementally.
	 */
	private void organizeDepthsAux(int initialDepth) {

		setDepth(initialDepth);

		if (son != null) {
			son.organizeDepthsAux(initialDepth + 1);
		}

		if (daughter != null) {
			daughter.organizeDepthsAux(initialDepth + 1);
		}
	}
}
