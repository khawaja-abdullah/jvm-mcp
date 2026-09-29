package dev.jvmmcp.core.model;

/**
 * Individual frame in a thread execution stack trace.
 */
public record ThreadStackFrame(
    String className,
    String methodName,
    String fileName,
    int lineNumber,
    boolean isNativeMethod
) {
    public static ThreadStackFrame from(StackTraceElement element) {
        if (element == null) {
            return new ThreadStackFrame("Unknown", "unknown", null, -1, false);
        }
        return new ThreadStackFrame(
            element.getClassName(),
            element.getMethodName(),
            element.getFileName(),
            element.getLineNumber(),
            element.isNativeMethod()
        );
    }

    @Override
    public String toString() {
        if (isNativeMethod) {
            return className + "." + methodName + "(Native Method)";
        }
        if (fileName != null && lineNumber >= 0) {
            return className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
        }
        return className + "." + methodName + "(Unknown Source)";
    }
}
