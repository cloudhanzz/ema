package han.jia.cloud.nlp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity class, corresponding to the ema.inflected table of the nlp Postgres database.
 * 
 * @author Jiayun Han
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "inflected", schema = "ema")
public class Inflected extends NamedEntity {

	private Integer id;
	private String base;
	private String inflection;

	public Inflected() {
	}

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "inflected_id_seq", sequenceName = "ema.inflected_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inflected_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getInflection() {
		return inflection;
	}

	public void setInflection(String inflection) {
		this.inflection = inflection;
	}
}
