package han.jia.cloud.nlp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity class, corresponding to the ema.derivative table of the nlp Postgres database.
 * 
 * @author Jiayun Han
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "derivative", schema = "ema")
public class Derivative extends NamedEntity {

	static final String splitter1 = "\\|";
	
	private Integer id;	
	private String derivatives;

	public Derivative() {
	}		

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "derivative_id_seq", sequenceName = "ema.derivative_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "derivative_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDerivatives() {
		return derivatives;
	}

	public void setDerivatives(String derivatives) {
		this.derivatives = derivatives;
	}
}
