package com.kumarsusant.config;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.MediaType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class ResponseBodyEncryptWrapper extends HttpServletResponseWrapper {
	private ByteArrayOutputStream output;
	private FilterServletOutputStream filterOutputStream;

	public ResponseBodyEncryptWrapper(HttpServletResponse response) {
		super(response);
		output = new ByteArrayOutputStream();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (filterOutputStream == null) {
			filterOutputStream = new FilterServletOutputStream(output);
		}
		return filterOutputStream;
	}

	@Override
	public String getHeader(String headerName) {
		String headerValue = super.getHeader(headerName);
		if ("Accept".equalsIgnoreCase(headerName)) {
			return MediaType.APPLICATION_JSON_VALUE;
		} else if ("Content-Type".equalsIgnoreCase(headerName)) {
			return MediaType.APPLICATION_JSON_VALUE;
		}
		return headerValue;
	}

	@Override
	public String getContentType() {
		String contentTypeValue = super.getContentType();
		if (MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(contentTypeValue)) {
			return MediaType.APPLICATION_JSON_VALUE;
		}
		return contentTypeValue;
	}

	public byte[] getDataStream() {
		return output.toByteArray();
	}

	public class FilterServletOutputStream extends ServletOutputStream {
		private final DataOutputStream outputStream;

		FilterServletOutputStream(OutputStream output) {
			super();
			this.outputStream = new DataOutputStream(output);
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener listener) {
		}

		@Override
		public void write(int b) throws IOException {
			outputStream.write(b);
		}
	}
}
