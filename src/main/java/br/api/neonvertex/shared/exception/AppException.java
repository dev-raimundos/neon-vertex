package br.api.neonvertex.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;
import java.util.Map;

public class AppException extends RuntimeException {

    private final ProblemDetail problem;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    private AppException(HttpStatus status, String message) {
        this(status, message, List.of());
    }

    private AppException(HttpStatus status, String message, List<String> errors) {
        super(message);
        this.problem = ProblemDetail.forStatusAndDetail(status, message);
        this.problem.setProperty("toast", Map.of(
                "type", resolveToastType(status),
                "message", message
        ));
        if (!errors.isEmpty()) {
            this.problem.setProperty("errors", errors);
        }
    }

    // -------------------------------------------------------------------------
    // 2xx
    // -------------------------------------------------------------------------
    public static AppException noContent(String message) {
        return new AppException(HttpStatus.NO_CONTENT, message);
    }

    // -------------------------------------------------------------------------
    // 4xx
    // -------------------------------------------------------------------------
    public static AppException badRequest(String message) {
        return new AppException(HttpStatus.BAD_REQUEST, message);
    }

    public static AppException badRequest(String message, List<String> errors) {
        return new AppException(HttpStatus.BAD_REQUEST, message, errors);
    }

    public static AppException unauthorized(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, message);
    }

    public static AppException unauthorized() {
        return unauthorized("Não autenticado.");
    }

    public static AppException forbidden(String message) {
        return new AppException(HttpStatus.FORBIDDEN, message);
    }

    public static AppException forbidden() {
        return forbidden("Acesso negado.");
    }

    public static AppException notFound(String message) {
        return new AppException(HttpStatus.NOT_FOUND, message);
    }

    public static AppException conflict(String message) {
        return new AppException(HttpStatus.CONFLICT, message);
    }

    public static AppException conflict(String message, List<String> errors) {
        return new AppException(HttpStatus.CONFLICT, message, errors);
    }

    public static AppException unprocessable(String message) {
        return new AppException(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public static AppException unprocessable(String message, List<String> errors) {
        return new AppException(HttpStatus.UNPROCESSABLE_ENTITY, message, errors);
    }

    public static AppException tooManyRequests(String message) {
        return new AppException(HttpStatus.TOO_MANY_REQUESTS, message);
    }

    public static AppException tooManyRequests() {
        return tooManyRequests("Muitas requisições. Tente novamente mais tarde.");
    }

    // -------------------------------------------------------------------------
    // 5xx
    // -------------------------------------------------------------------------
    public static AppException internalError(String message) {
        return new AppException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static AppException internalError() {
        return internalError("Erro interno do servidor.");
    }

    public static AppException serviceUnavailable(String message) {
        return new AppException(HttpStatus.SERVICE_UNAVAILABLE, message);
    }

    public static AppException serviceUnavailable() {
        return serviceUnavailable("Serviço indisponível.");
    }

    // -------------------------------------------------------------------------
    // Accessor
    // -------------------------------------------------------------------------
    public ProblemDetail getProblem() {
        return problem;
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------
    private static String resolveToastType(HttpStatus status) {
        if (status.is5xxServerError()) return "error";
        if (status.is4xxClientError()) return "warning";
        return "info";
    }
}