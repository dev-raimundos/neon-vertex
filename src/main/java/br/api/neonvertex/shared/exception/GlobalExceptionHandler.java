package br.api.neonvertex.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // Erros de neg&oacute
    // -------------------------------------------------------------------------
    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException ex) {
        return ex.getProblem();
    }

    // -------------------------------------------------------------------------
    // Erros nativos do Spring
    // -------------------------------------------------------------------------
    /**
     * Disparado quando @Valid falha num request body ou parâmetro.
     * Extrai os erros por campo e adiciona o toast automaticamente.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Dados inválidos."
        );
        problem.setProperty("errors", errors);
        problem.setProperty("toast", Map.of(
                "type", "warning",
                "message", "Verifique os dados informados."
        ));

        return problem;
    }

    /**
     * Fallback para qualquer exceção não mapeada.
     * Evita vazar stack traces para o cliente.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor."
        );
        problem.setProperty("toast", Map.of(
                "type", "error",
                "message", "Erro interno do servidor."
        ));

        return problem;
    }
}