package br.com.gt.imagelitleapi.infra.jwt;

public class InvalidTokenException extends  RuntimeException{

    public InvalidTokenException(String message) {
        super(message);
    }
}
