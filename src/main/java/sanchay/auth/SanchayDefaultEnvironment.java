/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.shiro.env.DefaultEnvironment;
import org.apache.shiro.env.NamedObjectEnvironment;
import org.apache.shiro.util.Destroyable;

/**
 *
 * @author User
 */
public class SanchayDefaultEnvironment extends DefaultEnvironment implements NamedObjectEnvironment, Destroyable  {

    /**
     * The default name under which the application's {@code SecurityManager} instance may be acquired, equal to
     * {@code securityManager}.
     */
    public static final String DEFAULT_SECURITY_MANAGER_KEY = "securityManager";

    protected final Map<String, Object> objects;
    private String securityManagerName;

    /**
     * Creates a new instance with a thread-safe {@link ConcurrentHashMap} backing map.
     */
    public SanchayDefaultEnvironment() {
        this(new ConcurrentHashMap<String, Object>());
    }

    /**
     * Creates a new instance with the specified backing map.
     *
     * @param seed backing map to use to maintain Shiro objects.
     */
    @SuppressWarnings({"unchecked"})
    public SanchayDefaultEnvironment(Map<String, ?> seed) {
        this.securityManagerName = DEFAULT_SECURITY_MANAGER_KEY;
        if (seed == null) {
            throw new IllegalArgumentException("Backing map cannot be null.");
        }
        this.objects = (Map<String, Object>) seed;
    }
    
}
