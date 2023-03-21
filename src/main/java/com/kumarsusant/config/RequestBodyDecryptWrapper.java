package com.kumarsusant.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;

import org.apache.commons.io.IOUtils;

import com.kumarsusant.util.AsymmetricEncryptionUtil;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class RequestBodyDecryptWrapper extends HttpServletRequestWrapper {
	private byte[] decryptRequestBody = null;

	public RequestBodyDecryptWrapper(HttpServletRequest request, PrivateKey privateKey) {
		super(request);
		// Convert InputStream data to byte array and store it to this wrapper instance,
		try {
			InputStream inputStream = request.getInputStream();
			byte[] encryptedRequstBody = IOUtils.toByteArray(inputStream);
			this.decryptRequestBody = AsymmetricEncryptionUtil
					.decryptMessage(new String(encryptedRequstBody), privateKey).getBytes();
			String strDecrypted = new String(decryptRequestBody);
			System.out.println("Datatatata :" + strDecrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptRequestBody);
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public boolean isFinished() {
				return false;
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}
