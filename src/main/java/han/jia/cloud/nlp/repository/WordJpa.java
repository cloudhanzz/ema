package han.jia.cloud.nlp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import han.jia.cloud.nlp.domain.Word;

/**
 * 
 * @author Jiayun Han
 *
 */
@Repository
public interface WordJpa extends CrudRepository<Word, Integer> {
}