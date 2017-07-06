package han.jia.cloud.nlp.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
public class NamedEntity implements Serializable {
		
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String value) {
		if(value != null){
			this.name = value;
		}
	}
}
