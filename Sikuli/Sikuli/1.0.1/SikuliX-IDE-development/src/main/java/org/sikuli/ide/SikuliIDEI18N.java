/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide;

import java.util.*;
import java.text.MessageFormat;

import org.sikuli.script.Debug;

public class SikuliIDEI18N {
   static ResourceBundle i18nRB, i18nRB_en;
   static Locale curLocale;

   static {
      Locale locale_en = new Locale("en","US");
      i18nRB_en = ResourceBundle.getBundle("i18n/IDE",locale_en);
      Locale locale = PreferencesUser.getInstance().getLocale();
      if(!setLocale(locale)){
         locale = locale_en;
         PreferencesUser.getInstance().setLocale(locale);
      }
      Debug.log(2, "locale: " + locale);
   }

   public static boolean setLocale(Locale locale){
      curLocale = locale;
      try{
         i18nRB = ResourceBundle.getBundle("i18n/IDE",locale);
      }
      catch(MissingResourceException e){
         Debug.error("no locale for " + locale);
         return false;
      }
      return true;
   }

   public static String _I(String key, Object... args){
      String ret;
      if(i18nRB==null)
         ret = i18nRB_en.getString(key);
      else{
         try {
            ret = i18nRB.getString(key);
         } catch (MissingResourceException e) {
            ret = i18nRB_en.getString(key);
         }
      }
      if(args.length>0){
         MessageFormat formatter = new MessageFormat("");
         formatter.setLocale(curLocale);
         formatter.applyPattern(ret);
         ret = formatter.format(args);
      }
      return ret;
   }

}
