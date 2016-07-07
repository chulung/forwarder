package com.chulung.forwarder.codec;

public abstract class SystemException extends RuntimeException {
	
	private static final long serialVersionUID = -4721447594864849522L;
	
	protected SystemException(final String errorMessage, final Object... args) {
		super(String.format(errorMessage, args));
	}
	
	protected SystemException(final String errorMessage, final Exception cause, final Object... args) {
		super(String.format(errorMessage, args), cause);
	}
	
	protected SystemException(final Exception cause) {
		super(cause.getMessage(), cause);
	}
}
