package com.kumarsusant.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kumarsusant.schema.MongoEncryptionSchema;
import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;

public class MongoDBUtil {
	private static final Logger logger = LoggerFactory.getLogger(MongoDBUtil.class);

	private static MongoClient mongoClient = null;
	private static MongoDatabase db = null;
	private static final String keyAltName = "dataEncKey";
	private static final String keyDb = "encryption";
	private static final String keyColl = "__keyVault";
	private static final String CONNECTION_STRING = "mongodb://localhost:27017";

	private static Map<String, Map<String, Object>> kmsProviders;
	private static String masterKeyStr;

	private static Map<String, BsonDocument> getJsonSchemaMap(String base64DataKeyId, String schemaType) {
		return Stream
				.of(new AbstractMap.SimpleEntry<>("HSUserDB.EncryptData",
						BsonDocument.parse(MongoEncryptionSchema.getDocument(base64DataKeyId, schemaType).toJson())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static void initializeDB(String schemaType) {
		if (mongoClient == null) {
			if (true) {
				String keyVaultNamespace = "encryption.__keyVault";
				BsonBinary dataKeyId = getEncryptionKey();
				final String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());
				AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
						.keyVaultNamespace(keyVaultNamespace).kmsProviders(kmsProviders)
						.schemaMap(getJsonSchemaMap(base64DataKeyId, schemaType)).build();
				MongoClientSettings clientSettings = MongoClientSettings.builder()
						.applyConnectionString(new ConnectionString(CONNECTION_STRING))
						.autoEncryptionSettings(autoEncryptionSettings).build();
				mongoClient = MongoClients.create(clientSettings);

			}
			if (db == null) {
				db = mongoClient.getDatabase("HSUserDB");
			}
		}
	}

	public static MongoDatabase getMongoDataBase(String schemaType) {
		if (db == null) {
			initializeDB(schemaType);
		}
		return db;
	}

	private static BsonBinary getEncryptionKey() {
		String encryptionKey = findDataEncryptionKey(keyAltName, keyDb, keyColl);

		BsonBinary dataKeyId = null;

		if (encryptionKey != null) {
			// Print the key
			logger.debug("Retrieved your existing key >> " + encryptionKey);
			dataKeyId = new BsonBinary(getGuidFromByteArray(Base64.getDecoder().decode(encryptionKey)));
			Document query = new Document("keyAltNames", keyAltName);
			FindIterable<Document> keys = createMongoClient(CONNECTION_STRING).getDatabase(keyDb)
					.getCollection("MasterKey").find(query);
			Document listDocument = iteratorToList(keys.iterator()).get(0);
			masterKeyStr = (String) listDocument.get("masterKey");
			generateLocalKMSProvider(Base64.getDecoder().decode(masterKeyStr));
		} else {
			generateLocalKMSProvider(generateMasterKey());
			DataKeyOptions dataKeyOptions = new DataKeyOptions();
			dataKeyOptions.keyAltNames(Arrays.asList(keyAltName));
			dataKeyId = getClientEncryption().createDataKey("local", dataKeyOptions);
			Document query = new Document();
			query.append("_id", 1);
			query.append("keyAltNames", keyAltName);
			query.append("masterKey", masterKeyStr);
			createMongoClient(CONNECTION_STRING).getDatabase(keyDb).getCollection("MasterKey").insertOne(query);
		}
		return dataKeyId;
	}

	public static <T> List<T> iteratorToList(Iterator<T> itr) {
		List<T> list = new ArrayList<T>();

		if (itr != null) {
			while (itr.hasNext()) {
				list.add(itr.next());
			}
		}
		return list;
	}

	private static ClientEncryption getClientEncryption() {
		String keyVaultNamespace = "encryption.__keyVault";
		ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
				.keyVaultMongoClientSettings(MongoClientSettings.builder()
						.applyConnectionString(new ConnectionString(CONNECTION_STRING)).build())
				.keyVaultNamespace(keyVaultNamespace).kmsProviders(kmsProviders).build();

		// ClientEncryption Object
		ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
		return clientEncryption;
	}

	private static String findDataEncryptionKey(String keyAltName, String keyDb, String keyColl) {
		try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
			Document query = new Document("keyAltNames", keyAltName);
			MongoCollection<Document> collection = mongoClient.getDatabase(keyDb).getCollection(keyColl);
			BsonDocument doc = collection.withDocumentClass(BsonDocument.class).find(query).first();

			if (doc != null) {
				return Base64.getEncoder().encodeToString(doc.getBinary("_id").getData());
			}
			return null;
		}
	}

	private static UUID getGuidFromByteArray(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		UUID uuid = new UUID(bb.getLong(), bb.getLong());
		return uuid;
	}

	private static byte[] generateMasterKey() {
		final byte[] masterKey = new byte[96];
		new SecureRandom().nextBytes(masterKey);
		masterKeyStr = Base64.getEncoder().encodeToString(masterKey);
		return masterKey;
	}

	private static Map<String, Map<String, Object>> generateLocalKMSProvider(byte[] localMasterKey) {

		return kmsProviders = new HashMap<String, Map<String, Object>>() {
			{
				put("local", new HashMap<String, Object>() {
					{
						put("key", localMasterKey);
					}
				});
			}
		};
	}

	private static MongoClient createMongoClient(String connectionString) {
		return MongoClients.create(connectionString);
	}
}
