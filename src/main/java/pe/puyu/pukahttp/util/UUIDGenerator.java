package pe.puyu.pukahttp.util;
import java.util.Base64;
import java.util.UUID;

public class UUIDGenerator {

	public static String random() {
		UUID uuid = UUID.randomUUID();
		return base64Encode(uuid.getMostSignificantBits()) + base64Encode(uuid.getLeastSignificantBits());
	}

	private static String base64Encode(long value) {
		byte[] bytes = new byte[6];
		for (int i = 0; i < 6; i++) {
			bytes[i] = (byte) ((value >> (8 * (5 - i))) & 0xFF);
		}
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
