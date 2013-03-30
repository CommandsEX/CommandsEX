package com.commandsex.api.annotations;

/**
 * Class to handle various functionality with the Builder
 */
public @interface Builder {
    
    /**
     * @return Name of the feature to be shown in the Builder
     */
    String name();
    
    /**
     * @return The description to show in the Builder
     */
    String description();
    
    /**
     * @return Whether we should show this as an available feature to add
     */
    boolean show() default true;

    /**
     * Classes this may depend on, e.g. /balance would depend on the Economy class
     * Separated by #####
     * @return Classes this may depend on
     */
    String depends() default "";
    
    /**
     * Commands, Events, EnableJob's this may depend on, e.g. /discodog might depend on the entity selector event
     * Separated by #####
     * @return Commands, Events, EnableJob's this may depend on
     */
    String linked() default "";
}
