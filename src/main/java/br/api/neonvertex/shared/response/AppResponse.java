package br.api.neonvertex.shared.response;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class AppResponse {

    private AppResponse() {
    }

    // -------------------------------------------------------------------------
    // 200 OK
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<Body<T>> ok(T data) {
        return ResponseEntity.ok(Body.of(data, null));
    }

    public static <T> ResponseEntity<Body<T>> ok(T data, String message) {
        return ResponseEntity.ok(Body.of(data, ToastPayload.success(message)));
    }

    // -------------------------------------------------------------------------
    // 201 Created
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<Body<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Body.of(data, null));
    }

    public static <T> ResponseEntity<Body<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Body.of(data, ToastPayload.success(message)));
    }

    // -------------------------------------------------------------------------
    // 202 Accepted
    // -------------------------------------------------------------------------
    public static ResponseEntity<Body<Void>> accepted(String message) {
        return ResponseEntity.accepted().body(Body.of(null, ToastPayload.info(message)));
    }

    // -------------------------------------------------------------------------
    // 204 No Content
    // -------------------------------------------------------------------------
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent()
            .build();
    }

    // -------------------------------------------------------------------------
    // 206 Partial Content — pagination
    // -------------------------------------------------------------------------
    public static <T> ResponseEntity<PageBody<T>> paginated(Page<T> page) {
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(PageBody.of(page, null));
    }

    public static <T> ResponseEntity<PageBody<T>> paginated(Page<T> page, String message) {
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(PageBody.of(page, ToastPayload.info(message)));
    }

    // -------------------------------------------------------------------------
    // Internal records
    // -------------------------------------------------------------------------
    public record Body<T>(T data, ToastPayload toast) {
        static <T> Body<T> of(T data, ToastPayload toast) {
            return new Body<>(data, toast);
        }
    }

    public record PageBody<T>(List<T> data, Pagination pagination, ToastPayload toast) {
        static <T> PageBody<T> of(Page<T> page, ToastPayload toast) {
            return new PageBody<>(page.getContent(),
                new Pagination(page.getTotalElements(), page.getSize(), page.getNumber() + 1, page.getTotalPages()),
                toast);
        }
    }

    public record Pagination(long total, int perPage, int currentPage, int lastPage) {
    }
}
