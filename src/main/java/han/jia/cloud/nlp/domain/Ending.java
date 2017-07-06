package han.jia.cloud.nlp.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import han.jia.cloud.nlp.ema.SuffixObj;

@SuppressWarnings("serial")
@Entity
@Table(name = "ending", schema = "ema")
public class Ending extends NamedEntity {

	private Integer id;
	private String cutWays;
	private String suffix;
	private Integer type;

	public Ending() {
	}

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "ending_id_seq", sequenceName = "ema.ending_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ending_id_seq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "cut_ways")
	public String getCutWays() {
		return cutWays;
	}

	public void setCutWays(String value) {
		this.cutWays = value;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String value) {
		this.suffix = value;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<SuffixObj> buildSuffixes() {
		return SuffixObj.parse(cutWays, suffix);
	}
}
