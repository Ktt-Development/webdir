package com.kttdevelopment.webdir.client.permissions;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.webdir.client.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public final class PermissionsUser {

    private final InetAddress user;
    private final String[] groups, permissions;
    private final Map<?,?> options;

    public PermissionsUser(final String user, final Map value) throws UnknownHostException{
        this(InetAddress.getByName(user),value);
    }

    @SuppressWarnings("unchecked")
    public PermissionsUser(final InetAddress user, final Map value){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("permissions"));
        {
            // if user uses loop back address (127.0.0.1) then use local address instead; (server uses machine address instead of 127.0.0.1)
            this.user = ExceptionUtil.requireNonExceptionElse(() -> user.isLoopbackAddress() ? InetAddress.getLocalHost() : user, user);
        }
        {
            String[] tGroups = new String[0];
            try{
                final Object groups = Objects.requireNonNull(value.get("groups"));
                if(groups instanceof List)
                    tGroups = Arrays.copyOf(((List<?>) groups).toArray(), ((List<?>) groups).size(), String[].class);
                else
                    tGroups = new String[]{groups.toString()};
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsUser.invalidGroups", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsUser.missingGroups", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.groups = tGroups;
        }
        {
            Map tOptions = new HashMap();
            try{
                tOptions = Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get("options")));
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsUser.invalidOptions", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsUser.missingOptions", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.options = tOptions;
        }
        {
            String[] tPermissions = new String[0];
            try{
                tPermissions = ((List<String>) value.get("permissions")).toArray(new String[0]);
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsUser.invalidPermissions", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsUser.missingPermissions", user) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.permissions = tPermissions;
        }
    }

    public final InetAddress getUser(){
        return user;
    }

    public final String[] getGroups(){
        return groups;
    }

    public final Map getOptions(){
        return options;
    }

    public final String[] getPermissions(){
        return permissions;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(o == null || getClass() != o.getClass())
            return false;
        final PermissionsUser other = ((PermissionsUser) o);
        return (other.getUser().equals(getUser()) || (other.getUser().isLoopbackAddress() && getUser().isLoopbackAddress())) &&
               Arrays.equals(other.getGroups(),getGroups()) &&
               other.getOptions().equals(getOptions()) &&
               Arrays.equals(other.getPermissions(),getPermissions());
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("user",getUser())
            .addObject("groups",getGroups())
            .addObject("options",getOptions())
            .addObject("permissions",getPermissions())
            .toString();
    }

}
