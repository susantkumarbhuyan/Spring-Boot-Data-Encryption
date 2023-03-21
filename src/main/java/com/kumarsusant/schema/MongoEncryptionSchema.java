package com.kumarsusant.schema;

import java.util.Arrays;

import org.bson.Document;

import com.kumarsusant.constants.SchemaConstants;

public class MongoEncryptionSchema {

	public static Document getDocument(String keyId, String schemaType) {
		return switch (schemaType) {
		case SchemaConstants.USER_SCHEMA -> getUserSchema(keyId);
		default -> new Document();
		};
	}

	private static Document getUserSchema(String keyId) {
		Document document = new Document();
		document.append("bsonType", "object")
				// .append("encryptMetadata", createEncryptMetadataSchema(keyId))
				// .append("required", Arrays.asList(new String[]{"firstName","lastName"}))
				.append("properties",
						new Document().append("name", buildEncryptedField("string", true, keyId))
								.append("phoneNo", buildEncryptedField("long", false, keyId)));

		return document;
	}

	// JSON Schema helpers
	private static Document buildEncryptedField(String bsonType, Boolean isDeterministic, String keyId) {
		String DETERMINISTIC_ENCRYPTION_TYPE = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
		String RANDOM_ENCRYPTION_TYPE = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";

		return new Document().append("encrypt", new Document().append("bsonType", bsonType)
				.append("keyId",
						Arrays.asList(new Document().append("$binary",
								new Document().append("base64", keyId).append("subType", "04"))))
				.append("algorithm", (isDeterministic) ? DETERMINISTIC_ENCRYPTION_TYPE : RANDOM_ENCRYPTION_TYPE));
	}

}
