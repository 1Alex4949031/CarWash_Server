package ru.nsu.carwash_server.payload.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {
	private String message;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public MessageResponse(@JsonProperty("message") String message) {
		this.message = message;
	}
}
