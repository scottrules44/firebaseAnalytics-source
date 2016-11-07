//Copyright Scott Harrison


package plugin.firebaseAnalytics;

import android.os.Bundle;
import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeListener;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.NamedJavaFunction;
import android.content.Context;
import com.google.firebase.analytics.*;



public class LuaLoader implements JavaFunction, CoronaRuntimeListener {
    private FirebaseAnalytics mFirebaseAnalytics;
	private int fListener;

	//private static final String EVENT_NAME = "firebaseAnalytics";

	/**
	 * Creates a new Lua interface to this plugin.
	 * <p>
	 * Note that a new LuaLoader instance will not be created for every CoronaActivity instance.
	 * That is, only one instance of this class will be created for the lifetime of the application process.
	 * This gives a plugin the option to do operations in the background while the CoronaActivity is destroyed.
	 */
	@SuppressWarnings("unused")
	public LuaLoader() {
		// Initialize member variables.
		fListener = CoronaLua.REFNIL;

		// Set up this plugin to listen for Corona runtime events to be received by methods
		// onLoaded(), onStarted(), onSuspended(), onResumed(), and onExiting().
		CoronaEnvironment.addRuntimeListener(this);
	}
	/**
	 * Called when this plugin is being loaded via the Lua require() function.
	 * <p>
	 * Note that this method will be called every time a new CoronaActivity has been launched.
	 * This means that you'll need to re-initialize this plugin here.
	 * <p>
	 * Warning! This method is not called on the main UI thread.
	 * @param L Reference to the Lua state that the require() function was called from.
	 * @return Returns the number of values that the require() function will return.
	 *         <p>
	 *         Expected to return 1, the library that the require() function is loading.
	 */
	@Override
	public int invoke(LuaState L) {
		// Register this plugin into Lua with the following functions.
		NamedJavaFunction[] luaFunctions = new NamedJavaFunction[] {
            new init( ), new LogEvent( ), new SetUserProperties (),
		};
		String libName = L.toString( 1 );
		L.register(libName, luaFunctions);

		// Returning 1 indicates that the Lua require() function will return the above Lua library.
		return 1;
	}

	/**
	 * Called after the Corona runtime has been created and just before executing the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been loaded/initialized.
	 *                Provides a LuaState object that allows the application to extend the Lua API.
	 */
	@Override
	public void onLoaded(CoronaRuntime runtime) {

		// Note that this method will not be called the first time a Corona activity has been launched.
		// This is because this listener cannot be added to the CoronaEnvironment until after
		// this plugin has been required-in by Lua, which occurs after the onLoaded() event.
		// However, this method will be called when a 2nd Corona activity has been created.

	}

	/**
	 * Called just after the Corona runtime has executed the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been started.
	 */
	@Override
	public void onStarted(CoronaRuntime runtime) {

	}

	/**
	 * Called just after the Corona runtime has been suspended which pauses all rendering, audio, timers,
	 * and other Corona related operations. This can happen when another Android activity (ie: window) has
	 * been displayed, when the screen has been powered off, or when the screen lock is shown.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been suspended.
	 */
	@Override
	public void onSuspended(CoronaRuntime runtime) {
	}

	/**
	 * Called just after the Corona runtime has been resumed after a suspend.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been resumed.
	 */
	@Override
	public void onResumed(CoronaRuntime runtime) {
	}
	/**
	 * Called just before the Corona runtime terminates.
	 * <p>
	 * This happens when the Corona activity is being destroyed which happens when the user presses the Back button
	 * on the activity, when the native.requestExit() method is called in Lua, or when the activity's finish()
	 * method is called. This does not mean that the application is exiting.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that is being terminated.
	 */
	@Override
	public void onExiting(CoronaRuntime runtime) {
		// Remove the Lua listener reference.
		CoronaLua.deleteRef( runtime.getLuaState(), fListener );
		fListener = CoronaLua.REFNIL;
	}


	static String getStringFrom( LuaState L, int index ) {
		String result = null;

		LuaType t = L.type( -2 );
		switch ( t ) {
			case NUMBER:
				result = String.valueOf( L.toNumber( index ) );
				break;
			default:
				result = L.toString( index );
				break;
		}
		return result;
	}
	private class init implements NamedJavaFunction {
        @Override
        public String getName() {
            return "init";
        }
        @Override
        public int invoke(LuaState L) {
            final Context context = CoronaEnvironment.getApplicationContext();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

            L.pushBoolean(true);
			return 1;

        }
	}
	private class LogEvent implements NamedJavaFunction {
		@Override
		public String getName() {
			return "LogEvent";
		}
		@Override
		public int invoke(LuaState L) {
			final String eventId = L.checkString( 1 );
			if ( null != eventId )
			{
				int paramsIndex = 2;
				Bundle params = new Bundle();
                L.pushNil();
					for ( int i = 0; L.next( paramsIndex ); i++ ) {
						String value1 = getStringFrom(L, -1);
						boolean isString = true;
						boolean isNum = false;
						boolean value2 = false;
						double myNum = 0;
						if (L.isBoolean(-1)){
							isString = false;
							value2 = L.checkBoolean(-1);
						}else if(L.isNumber(-1)){
							isNum = true;
							myNum = L.checkNumber(-1);
						}
						String key = getStringFrom(L, -2);

						if (null != key && null != value1 && isString == true && isNum == false) {
							params.putString(key, value1);
						}else if((null != key && isString == false && isNum == false)){
							params.putBoolean(key, value2);
						}else if (null != key && isString == false && isNum == true){
							params.putDouble(key, myNum);
						}

						L.pop(1);
                    }

				mFirebaseAnalytics.logEvent(eventId, params);

			}
			return 0;
		}

	}
	private class SetUserProperties implements NamedJavaFunction {
		@Override
		public String getName() {
			return "SetUserProperties";
		}
		@Override
		public int invoke(LuaState L) {
			mFirebaseAnalytics.setUserProperty(L.checkString( 1 ), L.checkString( 2 ));
			return 0;
		}

	}
}
