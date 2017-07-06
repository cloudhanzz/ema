package han.jia.cloud.nlp.service;

import java.util.stream.Stream;

import han.jia.cloud.nlp.domain.Derivative;
import han.jia.cloud.nlp.domain.Ending;
import han.jia.cloud.nlp.domain.Inflected;
import han.jia.cloud.nlp.domain.LeftRight;
import han.jia.cloud.nlp.domain.Prefix;
import han.jia.cloud.nlp.domain.Word;

/**
 * It provides services of creating the lookup hub to bootstrap and speed up the
 * morphological analysis.
 * 
 * @author Jiayun Han
 *
 */
public interface WordService {
	
	Stream<Word> findAllWords();

	Stream<Ending> findAllEndings();

	Stream<Prefix> findAllPrefixes();

	Stream<Inflected> findAllInflected();

	Stream<Derivative> findAllDerivatives();

	Stream<LeftRight> findAllLeftRights();
}
