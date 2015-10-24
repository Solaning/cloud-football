/***
 * Excerpted from "Hello, Android! 3e",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/

package com.kinth.football.database;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "tb_contact";
   public static final String GROUP_TABLE_NAME = "tb_group";
   public static final String PHONENUMBER_TABLE_NAME = "tb_phonenumber";
 
   // Columns in the Events database
   public static final String NAME = "name";
   public static final String SHORT_NUMBER = "shortNumber";
   public static final String LONG_NUMBER = "longNumber";
   public static final String APARTMENT = "apartment";
   public static final String HEAD_PATH = "headPath";
   
   public static final String GROUP_NAME = "groupName";
   
   public static final String PHONENUMBER = "phoneNumber";
   
   public static final String APP_ID = "wx22da024c51770bc7";
}
