package me.marioscalas.app.port;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class ErrorResponse {
    public static ErrorResponse fromException(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return new ErrorResponse(
            e.getMessage(), sw.toString()
        );
    }

    private final String errorMessage;
    private final String stackTrace;    
}
