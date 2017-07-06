package han.jia.cloud.nlp.service.impl;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import han.jia.cloud.nlp.domain.Derivative;
import han.jia.cloud.nlp.domain.Ending;
import han.jia.cloud.nlp.domain.Inflected;
import han.jia.cloud.nlp.domain.LeftRight;
import han.jia.cloud.nlp.domain.Prefix;
import han.jia.cloud.nlp.domain.Word;
import han.jia.cloud.nlp.repository.DerivativeJpa;
import han.jia.cloud.nlp.repository.EndingJpa;
import han.jia.cloud.nlp.repository.InflectedJpa;
import han.jia.cloud.nlp.repository.LeftRightJpa;
import han.jia.cloud.nlp.repository.PrefixJpa;
import han.jia.cloud.nlp.repository.WordJpa;
import han.jia.cloud.nlp.service.WordService;

/**
 * A Spring component class supporting DI, it provides services of creating the
 * lookup hub to bootstrap and speed up the morphological analysis.
 * 
 * @author Jiayun Han
 *
 */
@Component
public class WordServiceImpl implements WordService {

	@Inject
	private WordJpa wordJpa;

	@Inject
	private PrefixJpa prefixJpa;

	@Inject
	private EndingJpa endingJpa;

	@Inject
	private InflectedJpa inflectedJpa;

	@Inject
	private DerivativeJpa derivativeJpa;

	@Inject
	private LeftRightJpa leftRightJpa;

	@Override
	public Stream<Word> findAllWords() {
		return StreamSupport.stream(wordJpa.findAll().spliterator(), false);
	}

	@Override
	public Stream<Ending> findAllEndings() {
		return StreamSupport.stream(endingJpa.findAll().spliterator(), false);
	}

	@Override
	public Stream<Prefix> findAllPrefixes() {
		return StreamSupport.stream(prefixJpa.findAll().spliterator(), false);
	}

	@Override
	public Stream<Inflected> findAllInflected() {
		return StreamSupport
				.stream(inflectedJpa.findAll().spliterator(), false);
	}

	@Override
	public Stream<Derivative> findAllDerivatives() {
		return StreamSupport.stream(derivativeJpa.findAll().spliterator(),
				false);
	}

	@Override
	public Stream<LeftRight> findAllLeftRights() {
		return StreamSupport
				.stream(leftRightJpa.findAll().spliterator(), false);
	}
}
