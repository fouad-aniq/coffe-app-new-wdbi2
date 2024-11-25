\npackage ai.shreds.shared;\n\nimport javax.validation.ConstraintValidator;\nimport javax.validation.ConstraintValidatorContext;\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport java.util.Arrays;\nimport org.springframework.stereotype.Component;\nimport ai.shreds.shared.ValidMetadata;\n\n/**\n * Validator class for the @ValidMetadata annotation.\n * Validates that the metadata map does not contain disallowed keys or values,\n * and that keys and values are not null or empty.\n */\n@Component\npublic class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {\n\n    // Replaced Set.of(...) with Java 8 compatible code\n    private static final Set<String> DISALLOWED_KEYS = new HashSet<>(Arrays.asList(\"password\", \"creditCardNumber\", \"disallowedKey1\", \"disallowedKey2\"));\n    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>(Arrays.asList(\"disallowedValue1\", \"disallowedValue2\"));\n\n    @Override\n    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {\n        if (metadata == null || metadata.isEmpty()) {\n            return true; // Assuming null or empty metadata is acceptable\n        }\n        boolean isValid = true;\n        for (Map.Entry<String, Object> entry : metadata.entrySet()) {\n            String key = entry.getKey();\n            Object value = entry.getValue();\n\n            // Replaced key.isBlank() with key.trim().isEmpty() for Java 8 compatibility\n            if (key == null || key.trim().isEmpty()) {\n                context.disableDefaultConstraintViolation();\n                context.buildConstraintViolationWithTemplate(\"Metadata key cannot be null or blank.\")\n                       .addConstraintViolation();\n                isValid = false;\n            }\n            if (DISALLOWED_KEYS.contains(key)) {\n                context.disableDefaultConstraintViolation();\n                context.buildConstraintViolationWithTemplate(\"Metadata contains disallowed key: \" + key)\n                       .addConstraintViolation();\n                isValid = false;\n            }\n            // Check if value is null or, if it's a String, if it's empty or blank\n            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {\n                context.disableDefaultConstraintViolation();\n                context.buildConstraintViolationWithTemplate(\"Metadata value for key '\" + key + \"' cannot be null or blank.\")\n                       .addConstraintViolation();\n                isValid = false;\n            }\n            if (DISALLOWED_VALUES.contains(value)) {\n                context.disableDefaultConstraintViolation();\n                context.buildConstraintViolationWithTemplate(\"Metadata contains disallowed value: \" + value)\n                       .addConstraintViolation();\n                isValid = false;\n            }\n            // Additional structure validation logic can be added here\n        }\n        return isValid;\n    }\n}\n