package br.api.neonvertex.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // Erros de negócio — AppException
    // -------------------------------------------------------------------------

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException ex) {
        return ex.getProblem();
    }

    // -------------------------------------------------------------------------
    // Spring Security
    // -------------------------------------------------------------------------

    /**
     * Credenciais inválidas — login com email/senha errados.
     * Tratado aqui para não vazar informação sobre qual campo está errado.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas.",
                "warning",
                "E-mail ou senha incorretos."
        );
    }

    /**
     * Usuário autenticado, mas sem permissão para o recurso.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return problem(
                HttpStatus.FORBIDDEN,
                "Acesso negado.",
                "warning",
                "Você não tem permissão para acessar este recurso."
        );
    }

    /**
     * Conta desativada.
     */
    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException ex) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "Conta desativada.",
                "warning",
                "Sua conta está desativada."
        );
    }

    /**
     * Conta bloqueada.
     */
    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked(LockedException ex) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "Conta bloqueada.",
                "warning",
                "Sua conta está bloqueada."
        );
    }

    /**
     * Fallback para qualquer outra exceção de autenticação do Spring Security.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "Não autenticado.",
                "warning",
                "Autenticação necessária."
        );
    }

    // -------------------------------------------------------------------------
    // Spring MVC — erros de request
    // -------------------------------------------------------------------------

    /**
     * @Valid falhou — erros por campo extraídos e incluídos na resposta.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inválidos.");
        problem.setProperty("errors", errors);
        problem.setProperty("toast", Map.of("type", "warning", "message", "Verifique os dados informados."));
        return problem;
    }

    /**
     * Violação de constraint fora de @Valid (ex: @PathVariable, @RequestParam).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Dados inválidos."
        );
        problem.setProperty("errors", errors);
        problem.setProperty("toast", Map.of("type", "warning", "message", "Verifique os dados informados."));
        return problem;
    }

    /**
     * JSON malformado ou tipo incompatível no body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpMessageNotReadableException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida.",
                "warning",
                "O corpo da requisição está malformado."
        );
    }

    /**
     * Parâmetro de query obrigatório ausente.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Parâmetro ausente: " + ex.getParameterName(),
                "warning",
                "Parâmetro obrigatório não informado."
        );
    }

    /**
     * Tipo do parâmetro incompatível (ex: string onde se espera UUID).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido: " + ex.getName(),
                "warning",
                "Valor inválido para o parâmetro informado."
        );
    }

    /**
     * Method HTTP não permitido (ex: POST numa rota que só aceita GET).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return problem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método não permitido.",
                "warning",
                "Método HTTP não suportado para este endpoint."
        );
    }

    /**
     * Content-Type não suportado (ex: text/plain onde se espera application/json).
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return problem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de mídia não suportado.",
                "warning",
                "Use application/json."
        );
    }

    /**
     * Rota não encontrada.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Rota não encontrada.",
                "warning",
                "O endpoint solicitado não existe."
        );
    }

    // -------------------------------------------------------------------------
    // Fallback
    // -------------------------------------------------------------------------

    /**
     * Qualquer exceção não mapeada.
     * Evita vazar stack traces para o cliente.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor.",
                "error",
                "Erro interno do servidor."
        );
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private ProblemDetail problem(HttpStatus status, String detail, String toastType, String toastMessage) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty(
                "toast",
                Map.of(
                        "type",
                        toastType,
                        "message",
                        toastMessage
                )
        );
        return problem;
    }
}