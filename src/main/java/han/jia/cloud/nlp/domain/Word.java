package han.jia.cloud.nlp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity class, corresponding to the ema.word table of the nlp Postgres database.
 * 
 * @author Jiayun Han
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "word", schema = "ema")
public class Word extends NamedEntity {

	private Integer id;
	private boolean splittable;

	public Word() {
	}

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "word_id_seq", sequenceName = "ema.word_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isSplittable() {
		return splittable;
	}

	public void setSplittable(boolean splittable) {
		this.splittable = splittable;
	}
}
