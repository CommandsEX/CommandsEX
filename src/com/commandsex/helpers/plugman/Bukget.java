package com.commandsex.helpers.plugman;

public class Bukget {

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

        @Override
        public String toString() { return slug; }

        public static Category getFromString(String category) {
            for(Category cat : Category.values()) {
                if(cat.toString().equalsIgnoreCase(category))
                    return cat;
            }
            return null;
        }
    }

    public enum Version {
        LATEST("latest"),
        LATEST_STABLE_RELEASE("release"),
        LATEST_BETA("beta"),
        LATEST_ALPHA("alpha");

        private String version;

        Version(String version) {
            this.version = version;
        }

        @Override
        public String toString() { return version; }
    }

    public enum Field {
        SLUG("slug"),
        PLUGIN_NAME("plugin_name"),
        CATEGORIES("categories"),
        AUTHORS("authors"),
        WEBSITE("webpage"),
        BUKKIT_DEV_PAGE("dbo_page"),
        DESCRIPTION("description"),
        VERSION("versions.version"),
        FILE_LINK("versions.link"),
        DOWNLOAD_LINK("versions.downloads"),
        BUKKIT_VERSION("versions.status"),
        CHANGELOG("versions.changelog"),
        DATE_RELEASED("versions.date"),
        VERSION_SLUG("versions.slug"),
        HARD_DEPENDENCIES("versions.hard_dependencies"),
        SOFT_DEPENDENCIES("versions.soft_dependencies");

        private String field;

        Field(String field) {
            this.field = field;
        }

        @Override
        public String toString() { return field; }

        public String negate() { return "-" + field; }

    }

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

        @Override
        public String toString() { return action; }

    }

}
