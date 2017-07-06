package han.jia.cloud.nlp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity class, corresponding to the ema.prefix table of the nlp Postgres database.
 * 
 * @author Jiayun Han
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "prefix", schema = "ema")
public class Prefix extends NamedEntity {

	private Integer id;

	public Prefix() {
	}

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "prefix_id_seq", sequenceName = "ema.prefix_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prefix_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
