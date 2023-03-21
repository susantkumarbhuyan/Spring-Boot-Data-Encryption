package com.kumarsusant.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kumarsusant.util.AsymmetricEncryptionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EncryptionFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(EncryptionFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String encryptdData = "76oAwfWn7qbggbGwHV37H/AsiWNug2ebfP3etyxfFGoZF+gH/IvCLmW8lAHq125/iWg/JtCyHus1nP68cJD9HO5H04UdNBJPAJDQvrGMRV/eBEcMGhAIjeQO38V+Yup8/EKlVl5zIHcGY8ydIkjLJkvkRGqftgo3TXlTWwYJ/KPnsfVR1xvErKDwq4B3NWsNMR2SZiPMjrPZJ4T7yNsixqEdiDcacj5tApd64NroMH4KyheAqU/7dJGb2FNqACsvc/DD1rh6kCkuFx1LgN9fKgFQGol4JHwBSlud/B1oWpMveOJpnM+00FUSGjGtpIeeR1lB91JbnKb4SZWrpKhWCg==";
		// String publicKeyStr =
		// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiFTPwTWcMnkdvV5ZIfPNhqDLm50odVVkV6vFOIYGzI7cmDTiRplV8VdDHEU+Jg9QJOxqXx43DIsJdpYB3Caesi6j4Uu6QzX0iZDkH2uplxsrb56f8hJsyrTihjKQj4h/o2kaI3HOs2O6WvW7z1GBDBLAuABPKVVXeCFQu0JJ2a4x/xyEj0fgtsP7VBA6BLlKwC8yvGQshbzCb2qExzsEXXlRvc61WJ4plg4hEIABr37qD4wiXUABOXWbc0LwNl8PvyjMuYJIsIjPcETGohnwX8vqv5GgK0bt9GdeG+yldNPVF4WVdF+ajH0UYUaNdGGvpF6SgskFT/S2TFzWGZWTvQIDAQAB";
		String privateKeyStr = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCIVM/BNZwyeR29Xlkh882GoMubnSh1VWRXq8U4hgbMjtyYNOJGmVXxV0McRT4mD1Ak7GpfHjcMiwl2lgHcJp6yLqPhS7pDNfSJkOQfa6mXGytvnp/yEmzKtOKGMpCPiH+jaRojcc6zY7pa9bvPUYEMEsC4AE8pVVd4IVC7QknZrjH/HISPR+C2w/tUEDoEuUrALzK8ZCyFvMJvaoTHOwRdeVG9zrVYnimWDiEQgAGvfuoPjCJdQAE5dZtzQvA2Xw+/KMy5gkiwiM9wRMaiGfBfy+q/kaArRu30Z14b7KV009UXhZV0X5qMfRRhRo10Ya+kXpKCyQVP9LZMXNYZlZO9AgMBAAECggEABLhS/RB7GPBCjFTg5yEGw5t7kSauyOks8TAcQum5AIoNryNdli4m4JUx2J+5GB49mxmoUSS9qSyDJlzpMZMQuYCN4fX6O+t5TO38R+41T+r75QBr7zieFaKPmS/U+3MxYBn6crG1A1b3k8CgQPxEaIOZsoUoOHkg7yLQJbuUzPmDB/sB4b259LPgoGIO9/RujQAAYwZ9uNDi62v6jTxKSXG9uC/lRVR4lm8jynqM79ChyTmt3lJqX4pyrHkeBJK5rXN7itYW8OdnKob6CQGHKdnUs/nkG5N5PcsS4m/E5VdqV6moU7BSiyqdkyIvL+T4PswFX30grg7LqIZ8gWo3rQKBgQC6bQqVD9J4OvdT7tT0jn1TsAtDuDHJP5RRW88e0mqU2Ak2EWI9tEda/WVnFrC9FMkoGpF4Xh4devEOeNCCa5yy1EtG7HAYBUSf9uY/ViSHjqlK/LfpLY5NBC1YTJmlxJz2pQ5nZL+ULmOuJUvzD0b3AljUAy/2PIB2uJX+QTEK2wKBgQC7NcZbubh13IJgvNG9gZWanFHPTz0/XG2/AQCa9bwRdILRjjrir/07tXXRJeK9ZhHBS81MPSdz06LgXWsKM9QMVyjHFz0WuFCzfLzmTB7o8YREnsrP/E09ZYd0/20/35DTfomvNOWcmGlr52Mn3l+kPOaytQU9M1R2tmXPZQEDRwKBgQCphfyIfgHK2PwNM/IsjOIEVuJ3ayjCs3Eg5paalYbb4PPHWdl2+NemMyShsK1400z0jexYg2VpM2G+4jfhURAGQixNyzVHcorr162aTLAdIKpriRGIFaf/+tttHwLdDcQaOQ+3D5NyoRh9xGajGPJULasu59MWXAAJdWLX+Noq3wKBgQCMzW4qEO4en0HkFj+9w1fM2GNjg5pyLEdSkCNvaQERM8ssZ0f8tRbGML2zF0aI4trZwfRfseMZZ4SqMTjPjJmgynEeBGlqzCfORzQgFgJtezDbF388nOtPESynH3GAx9o6Z4KQpz1G4Q7xHC/kjYjrku6EXEfbdt7BBoBeUv5HAwKBgQCLYwCgCpfRDmKIfgPX+LHLyvVhVw8quNuhpJ0kB8LQlOwSSj1gfqw3Y5mlKKcJgba6+C2ozBMbK+puI1kkrnCmRV8QcifHjZW9fdgK0B13ND75tanPYehsMA8bzdano9WoYTjjfNaO+HMMh26dId4DH1PjLlz948D50xegHLp8Hw==";
		PrivateKey privateKey = AsymmetricEncryptionUtil.convertStringToPrivateKey(privateKeyStr);

		String publicKeyStr = ((HttpServletRequest) request).getHeader("client-publickey");
		if (publicKeyStr == null) {
			((HttpServletResponse) response).setStatus(401);
			response.getOutputStream().write("Client PublicKey is missing!".getBytes());
			return;
		}
		PublicKey publicKey = AsymmetricEncryptionUtil.convertStringToPublicKey(publicKeyStr);
		
//		String signatureStr = ((HttpServletRequest) request).getHeader("signature");
//		if (signatureStr == null) {
//			((HttpServletResponse) response).setStatus(401);
//			response.getOutputStream().write("Signature is missing!".getBytes());
//			return;
//		}		
//		String requstBody = getEnryptedRequestBody(request);
//		if (!AsymmetricEncryptionUtil.verifyDigitalSignature(requstBody.getBytes(),
//				DatatypeConverter.parseBase64Binary(signatureStr), publicKey)) {
//			((HttpServletResponse) response).setStatus(401);
//			response.getOutputStream().write("Client Server MisMatching !".getBytes());
//			return;
//		}
		boolean isContainRequestBody = "1".equals(((HttpServletRequest) request).getHeader("requestbody"));
		boolean isPost = "POST".equals(((HttpServletRequest) request).getMethod());

		if (isPost || isContainRequestBody) {
			RequestBodyDecryptWrapper bodyDecryptWrapper = new RequestBodyDecryptWrapper((HttpServletRequest) request,
					privateKey);
			ResponseBodyEncryptWrapper responseBodyDecryptWrapper = new ResponseBodyEncryptWrapper(
					(HttpServletResponse) response);

			chain.doFilter(bodyDecryptWrapper, responseBodyDecryptWrapper);
			responseBodyDecryptWrapper.flushBuffer();
			byte[] decryptedByteResponseBody = responseBodyDecryptWrapper.getDataStream();
			String decryptedStrResponseBody = new String(decryptedByteResponseBody, StandardCharsets.UTF_8);
			logger.info("EncryptionFilter > Decrypted_ResponseBody  >>>> " + decryptedStrResponseBody);

			String encryptedStrResponseBody = AsymmetricEncryptionUtil.encryptMessage(decryptedStrResponseBody,
					publicKey);
			logger.debug("EncryptionFilter > encrypted_ResponseBody  >>>> " + encryptedStrResponseBody);
			System.out.println("Encrypted Data :" + encryptedStrResponseBody);
			byte[] encryptedByteData = Base64.getDecoder().decode(encryptedStrResponseBody);
			response.getWriter().write(encryptedStrResponseBody);
			// response.getOutputStream().write(encryptedStrResponseBody.getBytes());
		} else {
			ResponseBodyEncryptWrapper responseBodyDecryptWrapper = new ResponseBodyEncryptWrapper(
					(HttpServletResponse) response);

			chain.doFilter(request, responseBodyDecryptWrapper);
			responseBodyDecryptWrapper.flushBuffer();
			byte[] decryptedByteResponseBody = responseBodyDecryptWrapper.getDataStream();
			String decryptedStrResponseBody = new String(decryptedByteResponseBody, StandardCharsets.UTF_8);
			logger.debug("EncryptionFilter > Decrypted_ResponseBody  >>>> " + decryptedStrResponseBody);

			String encryptedStrResponseBody = AsymmetricEncryptionUtil.encryptMessage(decryptedStrResponseBody,
					publicKey);
			System.out.println("Encrypted Data :" + encryptedStrResponseBody);
			logger.debug("EncryptionFilter > encrypted_ResponseBody  >>>> " + encryptedStrResponseBody);
			byte[] encryptedByteData = Base64.getDecoder().decode(encryptedStrResponseBody);
			response.getWriter().write(encryptedStrResponseBody);
			// response.getOutputStream().write(encryptedStrResponseBody.getBytes());
		}
	}

	private String getEnryptedRequestBody(ServletRequest request) {

		try {
			InputStream inputStream = request.getInputStream();
			byte[] encryptedRequstBody = IOUtils.toByteArray(inputStream);
			return new String(encryptedRequstBody);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
