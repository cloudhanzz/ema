package han.jia.cloud.nlp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import han.jia.cloud.nlp.domain.LeftRight;

/**
* 
* @author Jiayun Han
*
*/
@Repository
public interface LeftRightJpa extends CrudRepository<LeftRight, Integer> {
}