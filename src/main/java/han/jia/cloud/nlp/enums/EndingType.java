package han.jia.cloud.nlp.enums;

/**
 * The types of a wording ending
 * 
 * @author Jiayun Han
 *
 */
public enum EndingType {
	
	STRONG_SUFFIX(1), 
	
	// ies in 'cities', 
	DECOMPOSABLE(2), 
	
	// 'y' in 'city', not 'y' in 'boy'
	CONSONANT_Y(3), 
	
	// 'et' in 'get', -> getter
	VOWEL_CONSONANT_CONSONANT(4);
	
	private int code;
	
	private EndingType(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}	
}
