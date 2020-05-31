package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.permissions.Permissions;

import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.*;

@SuppressWarnings("rawtypes")
public final class PermissionsService {

    private static final Logger logger = Logger.getLogger("WebDir / PermissionsService");

    private final File permissionsFile;
    private Permissions permissions;

    private final Permissions defaultPermissions;

    //

    public final Permissions getPermissions(){
        return permissions;
    }

    //

    PermissionsService(final File permissionsFile, final File defaultPermissionsFile){
        this.permissionsFile = permissionsFile;
        
        logger.info(locale.getString("permissions.init.start"));

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new FileReader(defaultPermissionsFile));
            defaultPermissions = new Permissions((Map) IN.read());
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe(locale.getString("permissions.init.notFound"));
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(locale.getString("permissions.init.stream") + '\n' + LoggerService.getStackTraceAsString(e));
                }
        }

        read();
        logger.info(locale.getString("permissions.init.finished"));
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final boolean read(){
        logger.info(locale.getString("permissions.read.start"));

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(permissionsFile));
            permissions = new Permissions((Map) IN.read());
            logger.info(locale.getString("permissions.read.finished"));
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(locale.getString("permissions.read.notFound"));
            permissions = defaultPermissions;
            if(!write())
                logger.severe(locale.getString("permissions.read.notCreate"));
            else
                logger.info(locale.getString("permissions.read.created"));
        }catch(final ClassCastException | YamlException e){
            logger.severe(locale.getString("permissions.read.badSyntax") + '\n' + LoggerService.getStackTraceAsString(e));
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(locale.getString("permissions.read.stream"));
                }
        }
        return false;
    }

    public synchronized final boolean write(){
        logger.info(locale.getString("permissions.write.start"));

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(permissionsFile));
            OUT.write(permissions.toMap());
            logger.info(locale.getString("permissions.write.finished"));
            return true;
        }catch(final IOException e){
            logger.severe(locale.getString("permissions.write.failed") + '\n' + LoggerService.getStackTraceAsString(e));
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe(locale.getString("permissions.write.stream") + '\n' + LoggerService.getStackTraceAsString(e));
                }
        }
        return false;
    }

}
