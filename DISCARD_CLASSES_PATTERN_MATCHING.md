# discardClasses Pattern Matching

The `discardClasses` configuration has been enhanced to support pattern matching using wildcards.

## Features

### Exact Matching (Backward Compatible)
Works exactly as before for exact class names:
```java
config.setDiscardClasses(new String[] {
    "com.example.CustomException",
    "java.io.IOException"
});
```

### Wildcard Patterns
Now supports glob-style wildcards:

**`*` - Matches any characters (including dots)**
```java
config.setDiscardClasses(new String[] {
    "com.example.*"  // Matches all classes in com.example package
});
// Matches: com.example.CustomException, com.example.OtherException, etc.
```

**`?` - Matches a single character**
```java
config.setDiscardClasses(new String[] {
    "com.example.Exception?"
});
// Matches: com.example.Exception1, com.example.ExceptionX
// Does not match: com.example.Exception, com.example.Exception12
```

### Combined Patterns
Mix exact matches and wildcards:
```java
config.setDiscardClasses(new String[] {
    "java.io.*",                      // All java.io exceptions
    "com.*.CustomException",          // CustomException in any com.* package
    "org.example.SpecificException"   // Exact match
});
```

## Appender Compatibility

The BugsnagAppender continues to work exactly as before. When setting discard classes through the appender:

```xml
<appender name="BUGSNAG" class="com.bugsnag.BugsnagAppender">
    <discardClasses>com.example.*,java.io.IOException</discardClasses>
</appender>
```

Or programmatically:
```java
appender.setDiscardClass("com.example.*");
appender.setDiscardClass("java.io.IOException");
```

The appender internally converts these to the Configuration's pattern set format.

## Implementation Details

- Patterns without wildcards are treated as exact matches (using `Pattern.quote()` internally)
- Special regex characters are properly escaped
- The `getDiscardClasses()` method returns the original pattern strings (not compiled regex patterns)
- Pattern matching is case-sensitive
- Empty or null patterns are safely ignored
