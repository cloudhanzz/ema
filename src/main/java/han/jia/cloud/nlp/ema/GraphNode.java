package han.jia.cloud.nlp.ema;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a node of a cyclic graph that represents a derivative
 * word.
 * 
 * @author Jiayun Han
 *
 */
public class GraphNode {

	private String word;
	private GraphNode stem;
	private Set<GraphNode> derivatives;

	/**
	 * Creates an instance using the passed word
	 * 
	 * @param word
	 *            The entire word used to create an instance of this class
	 */
	public GraphNode(String word) {
		this.word = word;
	}

	/**
	 * Returns the entire text of this word
	 * 
	 * @return The entire text of this word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Returns the stem of this word as a {@code GraphNode} object
	 * 
	 * @return The stem of this word as a {@code GraphNode} object
	 */
	public GraphNode getStem() {
		return stem;
	}

	/**
	 * Sets the stem of this instance
	 * 
	 * @param stem
	 *            The stem of the word represented as a {@code GraphNode}
	 */
	public void setStem(GraphNode stem) {
		this.stem = stem;
	}

	/**
	 * Returns the derivatives of the stem of this word as {@code GraphNode}
	 * objects
	 * 
	 * @return The derivatives of the stem of this word as {@code GraphNode}
	 *         objects
	 */
	public Set<GraphNode> getDerivatives() {
		return derivatives;
	}

	/**
	 * It recursively searches for the word root
	 * 
	 * @return The root if the word has one or the entire word
	 */
	public String findRoot() {
		return stem == null ? word : stem.findRoot();
	}

	/**
	 * Adds a derivative to the derivative collection of this word
	 * 
	 * @param derivative
	 *            The derivative to add to the collection
	 */
	public void addDerivative(GraphNode derivative) {
		if (derivatives == null) {
			derivatives = new HashSet<GraphNode>();
		}
		derivatives.add(derivative);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	public String toString() {
		String s = word;
		if (stem != null) {
			s += "\n stem = " + stem.word;
		}
		if (derivatives != null && !derivatives.isEmpty()) {
			s += "\n derivatives";
			for (GraphNode derivative : derivatives) {
				s += "\n    " + derivative.word;
			}
		}
		return s;
	}
}
