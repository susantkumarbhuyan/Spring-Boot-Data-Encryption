package com.kumarsusant.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsymmetricEncryptionUtil {

	private static final Logger logger = LoggerFactory.getLogger(AsymmetricEncryptionUtil.class);
	private static final String ALGORITHM = "RSA";
	private static final String ENCRYPTION_ALGORITHM = "RSA/ECB/NOPADDING";
	private static final String CHARSET = "UTF-8";
	private static final String SIGNING_ALGORITHM = "SHA256withRSA";

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public void generateKeys() {
		try {
			KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			logger.debug("Error while encrypting: " + e.toString());
		}
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public String getPublicKeyString() {
		byte[] publicKeyBytes = publicKey.getEncoded();
		return Base64.getEncoder().encodeToString(publicKeyBytes);
	}

	public String getPrivateKeyString() {
		byte[] privateKeyBytes = privateKey.getEncoded();
		return Base64.getEncoder().encodeToString(privateKeyBytes);
	}

	public static PrivateKey convertStringToPrivateKey(String privateKeyString) {
		PrivateKey prvKey = null;
		try {
			byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			prvKey = keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.debug("Error while convertStringToPrivateKey: " + e.toString());
		}
		return prvKey;
	}

	public static PublicKey convertStringToPublicKey(String publicKeyString) {
		PublicKey pbKey = null;
		try {
			byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			pbKey = keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.debug("Error while convertStringToPublicKey: " + e.toString());
		}
		return pbKey;
	}

	public static String encryptMessage(String message, PublicKey publicKey) {

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encrypted = cipher.doFinal(message.getBytes(CHARSET));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptMessage(String encrptedMessage, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrptedMessage));
			return new String(decrypted, CHARSET);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Indicating that the message was indeed sent by the sender whose public key
	 * was used for verification, and that the message has not been tampered with in
	 * transit.
	 */
	// Function to implement Digital signature using SHA256 and RSA algorithm by
	// passing private key.
	public static byte[] createDigitalSignature(byte[] input, PrivateKey Key) throws Exception {
		Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
		signature.initSign(Key);
		signature.update(input);
		return signature.sign();
	}

	// Function for Verification of the digital signature by using the public key
	public static boolean verifyDigitalSignature(byte[] input, byte[] signatureToVerify, PublicKey key) {
		try {
			Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
			signature.initVerify(key);
			signature.update(input);
			return signature.verify(signatureToVerify);
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
//		AsymmetricEncryptionUtil encryptionUtil = new AsymmetricEncryptionUtil();
//		encryptionUtil.generateKeys();
//		String privateKeyStr = encryptionUtil.getPrivateKeyString();
//		String publicKeyStr = encryptionUtil.getPublicKeyString();

		String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiFTPwTWcMnkdvV5ZIfPNhqDLm50odVVkV6vFOIYGzI7cmDTiRplV8VdDHEU+Jg9QJOxqXx43DIsJdpYB3Caesi6j4Uu6QzX0iZDkH2uplxsrb56f8hJsyrTihjKQj4h/o2kaI3HOs2O6WvW7z1GBDBLAuABPKVVXeCFQu0JJ2a4x/xyEj0fgtsP7VBA6BLlKwC8yvGQshbzCb2qExzsEXXlRvc61WJ4plg4hEIABr37qD4wiXUABOXWbc0LwNl8PvyjMuYJIsIjPcETGohnwX8vqv5GgK0bt9GdeG+yldNPVF4WVdF+ajH0UYUaNdGGvpF6SgskFT/S2TFzWGZWTvQIDAQAB";
		String privateKeyStr = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCIVM/BNZwyeR29Xlkh882GoMubnSh1VWRXq8U4hgbMjtyYNOJGmVXxV0McRT4mD1Ak7GpfHjcMiwl2lgHcJp6yLqPhS7pDNfSJkOQfa6mXGytvnp/yEmzKtOKGMpCPiH+jaRojcc6zY7pa9bvPUYEMEsC4AE8pVVd4IVC7QknZrjH/HISPR+C2w/tUEDoEuUrALzK8ZCyFvMJvaoTHOwRdeVG9zrVYnimWDiEQgAGvfuoPjCJdQAE5dZtzQvA2Xw+/KMy5gkiwiM9wRMaiGfBfy+q/kaArRu30Z14b7KV009UXhZV0X5qMfRRhRo10Ya+kXpKCyQVP9LZMXNYZlZO9AgMBAAECggEABLhS/RB7GPBCjFTg5yEGw5t7kSauyOks8TAcQum5AIoNryNdli4m4JUx2J+5GB49mxmoUSS9qSyDJlzpMZMQuYCN4fX6O+t5TO38R+41T+r75QBr7zieFaKPmS/U+3MxYBn6crG1A1b3k8CgQPxEaIOZsoUoOHkg7yLQJbuUzPmDB/sB4b259LPgoGIO9/RujQAAYwZ9uNDi62v6jTxKSXG9uC/lRVR4lm8jynqM79ChyTmt3lJqX4pyrHkeBJK5rXN7itYW8OdnKob6CQGHKdnUs/nkG5N5PcsS4m/E5VdqV6moU7BSiyqdkyIvL+T4PswFX30grg7LqIZ8gWo3rQKBgQC6bQqVD9J4OvdT7tT0jn1TsAtDuDHJP5RRW88e0mqU2Ak2EWI9tEda/WVnFrC9FMkoGpF4Xh4devEOeNCCa5yy1EtG7HAYBUSf9uY/ViSHjqlK/LfpLY5NBC1YTJmlxJz2pQ5nZL+ULmOuJUvzD0b3AljUAy/2PIB2uJX+QTEK2wKBgQC7NcZbubh13IJgvNG9gZWanFHPTz0/XG2/AQCa9bwRdILRjjrir/07tXXRJeK9ZhHBS81MPSdz06LgXWsKM9QMVyjHFz0WuFCzfLzmTB7o8YREnsrP/E09ZYd0/20/35DTfomvNOWcmGlr52Mn3l+kPOaytQU9M1R2tmXPZQEDRwKBgQCphfyIfgHK2PwNM/IsjOIEVuJ3ayjCs3Eg5paalYbb4PPHWdl2+NemMyShsK1400z0jexYg2VpM2G+4jfhURAGQixNyzVHcorr162aTLAdIKpriRGIFaf/+tttHwLdDcQaOQ+3D5NyoRh9xGajGPJULasu59MWXAAJdWLX+Noq3wKBgQCMzW4qEO4en0HkFj+9w1fM2GNjg5pyLEdSkCNvaQERM8ssZ0f8tRbGML2zF0aI4trZwfRfseMZZ4SqMTjPjJmgynEeBGlqzCfORzQgFgJtezDbF388nOtPESynH3GAx9o6Z4KQpz1G4Q7xHC/kjYjrku6EXEfbdt7BBoBeUv5HAwKBgQCLYwCgCpfRDmKIfgPX+LHLyvVhVw8quNuhpJ0kB8LQlOwSSj1gfqw3Y5mlKKcJgba6+C2ozBMbK+puI1kkrnCmRV8QcifHjZW9fdgK0B13ND75tanPYehsMA8bzdano9WoYTjjfNaO+HMMh26dId4DH1PjLlz948D50xegHLp8Hw==";

		PrivateKey privatekey = AsymmetricEncryptionUtil.convertStringToPrivateKey(privateKeyStr);
		PublicKey publicKey = AsymmetricEncryptionUtil.convertStringToPublicKey(publicKeyStr);
		try {
			String data = "{\r\n" + "  \"id\": \"23\",\r\n" + "  \"name\": \"Susant\",\r\n" + "  \"dob\": 28,\r\n"
					+ "  \"phoneNo\": 789789\r\n" + "}";
//			String data = "dGYS+uFnTsNJNf2eN4DbFo/gJlxOhEpb34TGqXV3s17hFA0JWtbPx3dGYzJoRaJ0V05/yoldfiDFGlhwhodpfbmFC+CexNYOpby5T5sSf4ORPO8rPkIlxw0dZV77fObRdg3+o3FrXf3RO1icKlZEZ/GfoMNWDqZMjkLXnOLVODJMp8/yp+u10/UKCVUrcQGiXBauIyzzHoog53/SbFKbJN8yWTI/pz+mbnYFR0k4/Ac3CJfnoYgQyiHkHy2FfuvORHUP7u0dTQNYK4S7x5YjyeKsc5HFTTbKb1q9xYioxXPYxF5DBKRM+bpKudKzW4vIYmTUXddd6FEttCSjewxUQA==";
//			byte[] signMsg = AsymmetricEncryptionUtil.createDigitalSignature(data.getBytes(), privatekey);
//			String signMsgString = DatatypeConverter.printBase64Binary(signMsg);
//			System.out.println("SignMsgString  :" + signMsgString);
//			boolean inVerified = AsymmetricEncryptionUtil.verifyDigitalSignature(data.getBytes(),
//					DatatypeConverter.parseBase64Binary(signMsgString), publicKey);
				System.out.println("Data  :" + data);
//			String encptData = AsymmetricEncryptionUtil.encryptMessage(data, publcKey);
				String encptData = "cDJqMi56vwEg4C75ArWYXzJJy2WCx+mf3vSgdbFLkIjTzhWIlR3g3QDobaJkQrKuBEZEve/6RgjMn/Ix+5SB3ktFFEWzqmrebSUb3BipJx5olzR9UpEs7Py+sM3nwhU2tOy5A3auYyjnPeaYBiXLRYK9L4XDmOBRbD5oP2xMUIZaICKU+g+OtW+GQSA/B8ezSJ+C5d/fAom/YYNiBscm4QF630m8VEfmT8v0IMXlXDqLSZ/c0eEsepuEpnHAiHp6NQZQysutGLZj+edQGCGhCcTM7MvwiqNRUsicIGDPpzB40nIWilwQjxJetCe5LTVsg56Xb7TGndMPw4HgQDkFOw==";
				String decryptData = AsymmetricEncryptionUtil.decryptMessage(encptData, privatekey);
				byte[] decByte = decryptData.getBytes();
				System.out.println("PublicKey  :" + publicKeyStr);
				System.out.println("PrivateKey  :" + privateKeyStr);
				System.out.println("Encrypted Data :" + encptData);
				System.out.println("Decrypt Data :" + new String(decByte, StandardCharsets.UTF_8));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
