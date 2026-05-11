package br.api.neonvertex.shared.response;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Envelope padrão para respostas bem-sucedidas.
 * <p>
 * Uso:
 * return AppResponse.ok(data);
 * return AppResponse.created(data, "Criado com sucesso!");
 * return AppResponse.paginated(page);
 * return AppResponse.noContent();
 */
public class AppResponse {

    private AppResponse() {
    }

    // -------------------------------------------------------------------------
    // 200 OK
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<Envelope<T>> ok(T data) {
        return ResponseEntity.ok(Envelope.of(data, null));
    }

    public static <T> ResponseEntity<Envelope<T>> ok(T data, String message) {
        return ResponseEntity.ok(Envelope.of(data, ToastPayload.success(message)));
    }

    // -------------------------------------------------------------------------
    // 201 Created
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<Envelope<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelope.of(data, null));
    }

    public static <T> ResponseEntity<Envelope<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelope.of(data, ToastPayload.success(message)));
    }

    // -------------------------------------------------------------------------
    // 202 Accepted
    // -------------------------------------------------------------------------
    public static ResponseEntity<Envelope<Void>> accepted(String message) {
        return ResponseEntity.accepted().body(Envelope.of(null, ToastPayload.info(message)));
    }

    // -------------------------------------------------------------------------
    // 204 No Content
    // -------------------------------------------------------------------------
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // 206 Partial Content — paginação
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<PageEnvelope<T>> paginated(Page<T> page) {
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(PageEnvelope.of(page, null));
    }

    public static <T> ResponseEntity<PageEnvelope<T>> paginated(Page<T> page, String message) {
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(PageEnvelope.of(page, ToastPayload.info(message)));
    }

    // -------------------------------------------------------------------------
    // Envelopes internos
    // -------------------------------------------------------------------------
    public record Envelope<T>(T data, ToastPayload toast) {
        static <T> Envelope<T> of(T data, ToastPayload toast) {
            return new Envelope<>(data, toast);
        }
    }

    public record PageEnvelope<T>(java.util.List<T> data, Pagination pagination, ToastPayload toast) {

        static <T> PageEnvelope<T> of(Page<T> page, ToastPayload toast) {
            return new PageEnvelope<>(
                    page.getContent(),
                    new Pagination(
                            page.getTotalElements(),
                            page.getSize(),
                            page.getNumber() + 1,
                            page.getTotalPages()
                    ),
                    toast
            );
        }
    }

    public record Pagination(long total, int perPage, int currentPage, int lastPage) {}
}