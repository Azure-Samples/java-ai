package com.microsoft.azure.samples.aishop.blob_storage_service.exception;

public class GetResourceUrlException extends Exception {
    static final long serialVersionUID = 2023101012345678902L;

    public GetResourceUrlException(String message) {
        super(message);
    }

    public GetResourceUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetResourceUrlException(Throwable cause) {
        super(cause);
    }
}
