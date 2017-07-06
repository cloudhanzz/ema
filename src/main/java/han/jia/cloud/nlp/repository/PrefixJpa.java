package han.jia.cloud.nlp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import han.jia.cloud.nlp.domain.Prefix;

/**
* 
* @author Jiayun Han
*
*/
@Repository
public interface PrefixJpa extends CrudRepository<Prefix, Integer> {
}