package ch.swiftapp.erp.shared.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;

/**
 * Global exception handler for REST API controllers.
 * Returns RFC 7807 Problem Details for all errors.
 */
@RestControllerAdvice(basePackages = "ch.swiftapp.erp")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://swiftapp.ch/errors/validation"));

        var fieldErrors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        problem.setProperty("fieldErrors", fieldErrors);

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Bad Request");
        problem.setType(URI.create("https://swiftapp.ch/errors/bad-request"));
        return problem;
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://swiftapp.ch/errors/not-found"));
        return problem;
    }
}

