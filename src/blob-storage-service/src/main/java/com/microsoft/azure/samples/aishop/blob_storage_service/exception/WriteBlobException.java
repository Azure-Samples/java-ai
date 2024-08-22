package com.microsoft.azure.samples.aishop.blob_storage_service.exception;

public class WriteBlobException extends Exception {
  static final long serialVersionUID = 2023101012345678901L;

    public WriteBlobException(String message) {
        super(message);
    }

    public WriteBlobException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteBlobException(Throwable cause) {
        super(cause);
    }
}
