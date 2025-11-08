package org.dmgarcia.app.model;

public class ImportError {
    private int lineNumber;
    private String rawLine;
    private String message;

    public ImportError(int lineNumber, String rawLine, String message) {
        this.lineNumber = lineNumber;
        this.rawLine = rawLine;
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getMessage() {
        return message;
    }
}
