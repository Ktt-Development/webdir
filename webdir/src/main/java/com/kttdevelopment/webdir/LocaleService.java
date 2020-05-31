package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.util.*;
import java.util.logging.Logger;

public final class LocaleService {

    private static final Logger logger = Logger.getLogger("WebDir / LocaleService");

    private final LocaleBundle localeBundle = new LocaleBundleImpl();

    public final LocaleBundle getLocale(){
        return localeBundle;
    }

    //

    public final String getString(final String key){
        try{
            return Objects.requireNonNull(localeBundle.getString(key));
        }catch(final ClassCastException | NullPointerException | MissingResourceException ignored){
            logger.warning(getString("locale.getString.notFound"));
            try{
                return localeBundle.getString(Locale.ENGLISH, key); // use english as fallback value
            }catch(final ClassCastException | NullPointerException | MissingResourceException ignored2){
                return null;
            }
        }
    }

    public final String getString(final String key, final Object... param){
        final String value = getString(key);

        try{
            return String.format(Objects.requireNonNull(value), param);
        }catch(final NullPointerException ignored){
            // logger handled in above
        }catch(final IllegalFormatException ignored){
            logger.warning( getString("locale.getString.param"));
        }
        return value;
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final void setLocale(final String locale){
        setLocale(new Locale(locale));
    }

    public synchronized final void setLocale(final Locale locale){
        final LocaleBundle bundle = localeBundle;

        logger.info(
            getString(
                "locale.setLocale.initial",
                bundle.getLocale().getDisplayName(),
                locale.getDisplayName()
            )
        );

        if(localeBundle.hasLocale(locale)){
            setLocale(locale);
            logger.info(
                getString("locale.setLocale.finished",bundle.getLocale().getDisplayName(),locale.getDisplayName())
            );
        }else{
            logger.warning(
                getString("locale.setLocale.notFound",locale.getDisplayName())
            );
        }
    }

    //
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] localeCodes = {"en"};
    @SuppressWarnings("SameParameterValue")
    LocaleService(final String resource){
        logger.info("Started locale initialization");

        for(final String code : localeCodes){
            try{
                final Locale locale = new Locale(code);
                localeBundle.addLocale(ResourceBundle.getBundle(
                    resource,
                    locale,
                    LocaleService.class.getClassLoader(),
                    ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
                ));
                logger.finest('+' + code);
            }catch(final NullPointerException | MissingResourceException | IllegalArgumentException e){
                if(code.equalsIgnoreCase("en")){
                    logger.severe("Failed to load default locale");
                    throw new RuntimeException(e);
                }
            }
        }

        setLocale(Application.config.getConfig().getString("locale","en"));

        logger.info(getString("locale.init.finished"));
    }

}
