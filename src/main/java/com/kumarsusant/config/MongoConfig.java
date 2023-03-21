package com.kumarsusant.config;

import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.bol.crypt.CryptVault;
import com.bol.secure.CachedEncryptionEventListener;
import com.kumarsusant.schema.UserSchema;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
	private static final String DB_CONNECTION = "mongodb://localhost:27017";

	@Override
	protected String getDatabaseName() {
		return "HSUserDB";
	}

	private Map<String, BsonDocument> getJsonSchemaMap(String base64DataKeyId) {
		return Stream
				.of(new AbstractMap.SimpleEntry<>("HSUserDB.EncryptData",
						BsonDocument.parse(UserSchema.getDocument(base64DataKeyId).toJson())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public MongoClient mongoClient() {
		MongoClientSettings clientSettings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(DB_CONNECTION)).build();
		MongoClient mongoClient = MongoClients.create(clientSettings);
		return mongoClient;
//		final byte[] localMasterKey = new byte[96];
//		new SecureRandom().nextBytes(localMasterKey);
//		Map<String, Map<String, Object>> kmsProviders = new HashMap<String, Map<String, Object>>() {
//			{
//				put("local", new HashMap<String, Object>() {
//					{
//						put("key", localMasterKey);
//					}
//				});
//			}
//		};
//		String keyVaultNamespace = "encryption.__keyVault";
//		ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
//				.keyVaultMongoClientSettings(MongoClientSettings.builder()
//						.applyConnectionString(new ConnectionString("mongodb://localhost:27017")).build())
//				.keyVaultNamespace(keyVaultNamespace).kmsProviders(kmsProviders).build();
//		ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
//		BsonBinary dataKeyId = clientEncryption.createDataKey("local", new DataKeyOptions());
//		final String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());
//		AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
//				.keyVaultNamespace(keyVaultNamespace).kmsProviders(kmsProviders)
//				.schemaMap(getJsonSchemaMap(base64DataKeyId)).build();
//		MongoClientSettings clientSettings = MongoClientSettings.builder()
//				.applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
//				.autoEncryptionSettings(autoEncryptionSettings).build();
//		MongoClient mongoClient = MongoClients.create(clientSettings);
//		return mongoClient;
	}

	// normally you would use @Value to wire a property here
	private static final byte[] secretKey = Base64.getDecoder().decode("hqHKBLV83LpCqzKpf8OvutbCs+O5wX5BPu3btWpEvXA=");
	private static final byte[] oldKey = Base64.getDecoder().decode("cUzurmCcL+K252XDJhhWI/A/+wxYXLgIm678bwsE2QM=");

	@Bean
	public CryptVault cryptVault() {
		return new CryptVault().with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(0, oldKey)
				.with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(1, secretKey).withDefaultKeyVersion(1);
	}

	@Bean
	public CachedEncryptionEventListener encryptionEventListener(CryptVault cryptVault) {
		return new CachedEncryptionEventListener(cryptVault);
	}

	@Override
	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory,
			MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
		MappingMongoConverter converter = super.mappingMongoConverter(databaseFactory, customConversions,
				mappingContext);
		// NB: without overriding defaultMongoTypeMapper, an _class field is put in
		// every document
		// since we know exactly which java class a specific document maps to, this is
		// surplus
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return converter;
	}
}
