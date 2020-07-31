package com.kttdevelopment.webdir.server;

public abstract class ServerVars {

    public static class Config {
        
        @Deprecated
        public static final String fileRenderersKey = "fileRenderers"; 
        @Deprecated
        public static final String defaultFileRenderers = ".fileRenderers"; // file renderers can use the existing default system to operate
        
        public static final String permissionsKey     = "permissions";
        public static final String defaultPermissions = "permissions.yml";

    }

    public static class Permissions {
        
        public static final String groupsKey        = "groups";
        public static final String inheritanceKey   = "inheritance";
        public static final String optionsKey       = "options";
        public static final String defaultKey       = "default";
        public static final String permissionsKey   = "permissions";

        public static final String usersKey = "users";

    }

    public static class Renderer {

        public static final String exchangeRendererKey = "exchangeRenderer";

    }

}
