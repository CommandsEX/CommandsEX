package com.commandsex.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/**
 * Class to handle various functionality with the Builder
 */
public @interface Builder {
    
    /**
     * @return Name of the feature to be shown in the Builder
     */
    String name() default "";
    
    /**
     * @return The description to show in the Builder
     */
    String description() default "";

    /**
     * @return The type of feature this is, can be COMMAND, EVENT, PACKAGE or MISC
     */
    String type();
    
    /**
     * @return Whether this is a core feature that must be added, will show as greyed out in the Builder
     */
    boolean core() default false;

    /**
     * Classes this may depend on, e.g. /balance would depend on the Economy class
     * Separated by #####
     * @return Classes this may depend on
     */
    String depends() default "";
}
