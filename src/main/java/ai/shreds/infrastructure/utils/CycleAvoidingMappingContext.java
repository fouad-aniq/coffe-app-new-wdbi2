package ai.shreds.infrastructure.utils;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * CycleAvoidingMappingContext is a utility class used to prevent infinite recursion during the mapping process 
 * between domain entities and infrastructure entities. It keeps track of already mapped objects to prevent cycles.
 * This is crucial when dealing with bidirectional relationships or cyclic dependencies in object graphs,
 * such as the parentCategory and subcategories in InfrastructureCategoryEntity.
 *
 * Note: This class is not thread-safe. If an instance of this class is accessed by multiple threads concurrently,
 * and at least one of the threads modifies it structurally, it must be synchronized externally.
 */
public class CycleAvoidingMappingContext {

    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    /**
     * Retrieves an already mapped instance for the given source object.
     *
     * @param source the source object
     * @param <T>    the type of the target object
     * @return the mapped instance or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getMappedInstance(Object source, Class<T> targetType) {
        Object instance = knownInstances.get(source);
        if (targetType.isInstance(instance)) {
            return (T) instance;
        } else {
            return null;
        }
    }

    /**
     * Stores a mapped instance for the given source object.
     *
     * @param source the source object
     * @param target the mapped instance
     */
    public void storeMappedInstance(Object source, Object target) {
        knownInstances.put(source, target);
    }
}
