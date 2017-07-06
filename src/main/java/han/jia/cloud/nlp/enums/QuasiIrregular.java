package han.jia.cloud.nlp.enums;

import static han.jia.cloud.nlp.util.Constants.*;

import java.util.regex.Pattern;

/**
 * For matching and processing semi-irregular words.
 * 
 * @author Jiayun Han
 *
 */
public enum QuasiIrregular {

	_i_1(Pattern.compile("(.{4,})i", Pattern.CASE_INSENSITIVE), us, i),
	_i_2(Pattern.compile("(.{4,})i", Pattern.CASE_INSENSITIVE), o, i),
	
	_a_1(Pattern.compile("(.{4,})a", Pattern.CASE_INSENSITIVE), um, a),
	_a_2(Pattern.compile("(.{4,})a", Pattern.CASE_INSENSITIVE), on, a),
	_a_3(Pattern.compile("(.{3,})ta", Pattern.CASE_INSENSITIVE), EMPTY_STR, ta),
	
	_es(Pattern.compile("(.{3,})es", Pattern.CASE_INSENSITIVE), is, e),
	_ae(Pattern.compile("(.{3,}a)e", Pattern.CASE_INSENSITIVE), EMPTY_STR, e),
	
	_men(Pattern.compile("(.*m)en", Pattern.CASE_INSENSITIVE), an, e),
	_ept(Pattern.compile("(.{2,})ept", Pattern.CASE_INSENSITIVE), eep, ept);

	private Pattern pattern;
	private String addOn;
	private String inflection;

	private QuasiIrregular(Pattern pattern, String addOn, String inflection) {
		this.pattern = pattern;
		this.addOn = addOn;
		this.inflection = inflection;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getAddOn() {
		return addOn;
	}

	public String getInflection() {
		return inflection;
	}
	
	public boolean matches(String word){
		return pattern.matcher(word).matches();
	}    
}
