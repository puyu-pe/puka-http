package pe.puyu.pukahttp.services.api;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResponseApi<T> {

	public ResponseApi() {
		message = new SimpleStringProperty();
		error = new SimpleStringProperty();
		data = new SimpleObjectProperty<>();
		status = new SimpleStringProperty();
	}

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public SimpleStringProperty messageProperty() {
		return message;
	}

	public String getError(){
		return error.get();
	}

	public void setError(String error){
		this.error.set(error);
	}

	public SimpleStringProperty errorProperty(){
		return error;
	}

	public T getData(){
		return data.get();
	}

	public void setData(T data){
		this.data.set(data);
	}

	public SimpleObjectProperty<T> dataProperty(){
		return data;
	}

	public String getStatus(){
		return status.get();
	}

	public void setStatus(String status){
		this.status.set(status);
	}

	public SimpleStringProperty statusProperty(){
		return status;
	}

	private final SimpleStringProperty message;
	private final SimpleStringProperty error;
	private final SimpleObjectProperty<T> data;
	private final SimpleStringProperty status;

}