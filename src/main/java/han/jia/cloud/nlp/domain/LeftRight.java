package han.jia.cloud.nlp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity class, corresponding to the ema.son_daughter table of the nlp
 * Postgres database. In this application, 'son' and 'left' are used
 * interchangeably, so are 'daughter' and 'right'.
 * 
 * @author Jiayun Han
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "son_daughter", schema = "ema")
public class LeftRight extends NamedEntity {

	private Integer id;
	private String left;
	private String right;

	public LeftRight() {
	}

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "son_daughter_id_seq", sequenceName = "ema.son_daughter_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "son_daughter_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "son")
	public String getLeft() {
		return left;
	}

	public void setLeft(String value) {
		this.left = value;
	}

	@Column(name = "daughter")
	public String getRight() {
		return right;
	}

	public void setRight(String value) {
		this.right = value;
	}
}
