package com.kinth.football.util;

import android.util.Log;

import com.kinth.football.config.Config;

/**
 *
 */
public class LogUtil {

	  public static void v(String tag, String info)
	  {
	    if (Config.DEBUG_MODE)
	      Log.v(tag, "1.0-->" + info);
	  }

	  public static void d(String tag, String info)
	  {
	    if (Config.DEBUG_MODE)
	      Log.d(tag, info);
	  }

	  public static void i(String tag, String info)
	  {
	    if (Config.DEBUG_MODE)
	      Log.i(tag, info);
	  }

	  public static void i(String paramString)
	  {
	    Log.i("Config", paramString);
	  }

	  public static void w(String tag, String info)
	  {
	    if (Config.DEBUG_MODE)
	      Log.w(tag, info);
	  }

	  public static void e(String tag, String info)
	  {
	    if (Config.DEBUG_MODE)
	      Log.e(tag, info);
	  }
}
