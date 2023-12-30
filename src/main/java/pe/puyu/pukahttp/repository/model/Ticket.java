package pe.puyu.pukahttp.repository.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "tickets")
public class Ticket {
	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, dataType = DataType.LONG_STRING)
	private String data;

	@DatabaseField(dataType = DataType.DATE_STRING, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL", readOnly = true, canBeNull = false)
	private Date created_at;

	public Ticket() {

	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return this.data;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
