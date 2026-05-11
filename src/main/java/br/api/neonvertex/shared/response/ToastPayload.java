package br.api.neonvertex.shared.response;

/**
 * Contrato do toast consumido pelo front-end.
 * Compartilhado entre AppException (erros) e AppResponse (sucessos).
 * <p>
 * Tipos possíveis: "success", "info", "warning", "error"
 */
public record ToastPayload(String type, String message) {

    public static ToastPayload success(String message) {
        return new ToastPayload("success", message);
    }

    public static ToastPayload info(String message) {
        return new ToastPayload("info", message);
    }

    public static ToastPayload warning(String message) {
        return new ToastPayload("warning", message);
    }

    public static ToastPayload error(String message) {
        return new ToastPayload("error", message);
    }
}