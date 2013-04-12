package com.commandsex.helpers.plugman;

import org.apache.commons.lang.WordUtils;

/**
 * Holder of all Bukget related enums
 * @author Kezz101
 */
public class Bukget {

    /**
     * Enum containing all possible Categories a plugin could be
     */
    public enum Category {
        ADMIN_TOOLS("Admin Tools"),
        DEVELOPER_TOOLS("Developer Tools"),
        FUN("Fun"),
        MECHANICS("Mechanics"),
        TELEPORTATION("Teleportation"),
        WORLD_GENERATORS("World Generators"),
        ANTI_GRIEFING_TOOLS("Anti-Griefing Tools"),
        ECONOMY("Economy"),
        GENERAL("General"),
        WEBSITE_ADMINISTRATION("Website Administration"),
        CHAT_RELATED("Chat Related"),
        FIXES("Fixes"),
        INFORMATIONAL("Informational"),
        ROLE_PLAYING("Role Playing"),
        WORLD_EDITING_AND_MANAGEMENT("World Editing and Management");

        private String slug;

        Category(String slug) {
            this.slug = slug;
        }

        /**
         * Gets the Bukget API friendly version of the category
         */
        @Override
        public String toString() { return slug.replace(" ", "%20"); }

        /**
         * Gets the user friendly name of the category
         */
        public String getFriendlyName() { return slug; }

        /**
         * Gets a category from an entered string
         * @param category the string
         * @return the category or <code>null</code> if not found
         */
        public static Category getFromString(String category) {
            for(Category cat : Category.values()) {
                if(cat.getFriendlyName().equalsIgnoreCase(category))
                    return cat;
            }
            return null;
        }

    }

    /**
     * Enum containing all possible plugin versions
     */
    public enum Version {
        LATEST("latest"),
        LATEST_STABLE_RELEASE("release"),
        LATEST_BETA("beta"),
        LATEST_ALPHA("alpha");

        private String version;

        Version(String version) {
            this.version = version;
        }

        /**
         * Gets the Bukget API friendly version of the version
         */
        @Override
        public String toString() { return version; }

        /**
         * Gets the user friendly version of the version
         */
        public String getFriendlyName() { return WordUtils.capitalize(toString()); }

        /**
         * Gets a version from an entered string
         * @param version the string
         * @return the version or <code>null</code> if not found
         */
        public static Version getFromString(String version) {
            for(Version cat : Version.values()) {
                if(cat.getFriendlyName().equalsIgnoreCase(version))
                    return cat;
            }
            return null;
        }

    }

    /**
     * Enum containing all retrievable and searchable plugin fields. <br />
     * <b>Note:</b> if searching, fields can be negated (e.g don't include foo) using <code>field.negate()</code>
     */
    public enum Field {
        SLUG("slug"),
        PLUGIN_NAME("plugin_name"),
        CATEGORIES("categories"),
        AUTHORS("authors"),
        WEBSITE("website"),
        BUKKIT_DEV_PAGE("dbo_page"),
        DESCRIPTION("description"),
        VERSION("versions.version"),
        FILE_LINK("versions.link"),
        DOWNLOAD_LINK("versions.downloads"),
        BUKKIT_VERSION("versions.game_versions"),
        CHANGELOG("versions.changelog"),
        DATE_RELEASED("versions.date"),
        VERSION_SLUG("versions.slug"),
        HARD_DEPENDENCIES("versions.hard_dependencies"),
        SOFT_DEPENDENCIES("versions.soft_dependencies"),
        POPULARITY_DAILY("popularity.daily"),
        POPULARITY_WEEKLY("popularity.weekly"),
        POPULARITY_MONTHLY("popularity.monthly");

        private String field;

        Field(String field) {
            this.field = field;
        }

        /**
         * Gets the Bukget API friendly version of the field
         */
        @Override
        public String toString() { return field; }

        /**
         * Returns the negated Bukget API friendly version of the field
         */
        public String negate() { return "-" + field; }

        /**
         * Returns a  user friendly version of the field
         */
        public String getFriendlyName() {
            if(field.startsWith("versions."))
                return WordUtils.capitalize(field.replace("versions.", "").replace("_", " "));
            if(field.startsWith("popularity."))
                return WordUtils.capitalize(field.replace("popularity.", "")) + "Popularity";
            return WordUtils.capitalize(field.replace("_", " ").replace("dbo", "bukkit dev"));
        }

        /**
         * Gets a field from an entered string. <br />
         * This will match the entered string to the user friendly name
         * @param field the string
         * @return the field or <code>null</code> if not found
         */
        public static Field getFromString(String field) {
            for(Field f : Field.values())
                if(f.getFriendlyName().equalsIgnoreCase(field))
                    return f;
            return null;
        }

    }

    /**
     * Enum containing all available actions for use in Bukget searches
     */
    public enum SearchAction {
        EQUALS("="),
        NOT_EQUAL("!="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL_TO("<="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL_TO(">="),
        LIKE("like");

        private String action;

        SearchAction(String action) {
            this.action = action;
        }

        /**
         * Returns the Bukget API and user friendly version of the action
         */
        @Override
        public String toString() { return action; }

        /**
         * Gets an action from an entered string
         * @param action the string
         * @return the action or <code>null</code> if not found
         */
        public static SearchAction getFromString(String action) {
            for(SearchAction a : SearchAction.values())
                if(action.toString().equalsIgnoreCase(action))
                    return a;
            return null;
        }

    }

}
