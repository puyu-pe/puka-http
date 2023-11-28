package pe.puyu.sweetprinterpos.repository.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tickets")
public class Ticket {
	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, dataType = DataType.LONG_STRING)
	private String data;

	public Ticket(){

	}


	public void setData(String data) {
		this.data = data;
	}

	public String getData(){
		return this.data;
	}

	public void setId(Long id){
		this.id = id;
	}
	public long getId(){
		return this.id;
	}
}
