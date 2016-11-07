//
//  PluginLibrary.mm
//  TemplateApp
//
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "firebaseAnalytics.h"

#include "CoronaAssert.h"
#include "CoronaEvent.h"
#include "CoronaLibrary.h"
#include "CoronaLuaIOS.h"
#include "CoronaRuntime.h"
#import <UIKit/UIKit.h>

#import "Analytics/FirebaseAnalytics.h"
#import "Core/FirebaseCore.h"
#import "ID/FirebaseInstanceID.h"

// ----------------------------------------------------------------------------

class firebaseAnalytics
{
	public:
		typedef firebaseAnalytics Self;

	public:
		static const char kName[];
		static const char kEvent[];

	protected:
		firebaseAnalytics();

	public:
		bool Initialize( CoronaLuaRef listener );

	public:
		CoronaLuaRef GetListener() const { return fListener; }

	public:
		static int Open( lua_State *L );

	protected:
		static int Finalizer( lua_State *L );

	public:
		static Self *ToLibrary( lua_State *L );

	public:
		static int init( lua_State *L );
		static int LogEvent( lua_State *L );
        static int SetUserProperties( lua_State *L);

	private:
		CoronaLuaRef fListener;
};

// ----------------------------------------------------------------------------

// This corresponds to the name of the library, e.g. [Lua] require "plugin.library"
const char firebaseAnalytics::kName[] = "plugin.firebaseAnalytics";

// This corresponds to the event name, e.g. [Lua] event.name
const char firebaseAnalytics::kEvent[] = "firebaseAnalytics";

firebaseAnalytics::firebaseAnalytics()
:	fListener( NULL )
{
}

bool
firebaseAnalytics::Initialize( CoronaLuaRef listener )
{
	// Can only initialize listener once
	bool result = ( NULL == fListener );

	if ( result )
	{
		fListener = listener;
	}

	return result;
}

int
firebaseAnalytics::Open( lua_State *L )
{
	// Register __gc callback
	const char kMetatableName[] = __FILE__; // Globally unique string to prevent collision
	CoronaLuaInitializeGCMetatable( L, kMetatableName, Finalizer );

	// Functions in library
	const luaL_Reg kVTable[] =
	{
		{ "init", init },
		{ "LogEvent", LogEvent },

		{ NULL, NULL }
	};

	// Set library as upvalue for each library function
	Self *library = new Self;
	CoronaLuaPushUserdata( L, library, kMetatableName );

	luaL_openlib( L, kName, kVTable, 1 ); // leave "library" on top of stack

	return 1;
}

int
firebaseAnalytics::Finalizer( lua_State *L )
{
	Self *library = (Self *)CoronaLuaToUserdata( L, 1 );

	CoronaLuaDeleteRef( L, library->GetListener() );

	delete library;

	return 0;
}

firebaseAnalytics *
firebaseAnalytics::ToLibrary( lua_State *L )
{
	// library is pushed as part of the closure
	Self *library = (Self *)CoronaLuaToUserdata( L, lua_upvalueindex( 1 ) );
	return library;
}

// [Lua] library.init( listener )
int
firebaseAnalytics::init( lua_State *L )
{
    [FIRApp configure];
    lua_pushboolean( L, 1 );
    
    return 1;
}

static NSString *
ToNSString( lua_State *L, int index )
{
    NSString *result = nil;
    
    int t = lua_type( L, -2 );
    switch ( t )
    {
        case LUA_TNUMBER:
            result = [NSString stringWithFormat:@"%g", lua_tonumber( L, index )];
            break;
        default:
            result = [NSString stringWithUTF8String:lua_tostring( L, index )];
            break;
    }
    
    return result;
}

int
firebaseAnalytics::LogEvent( lua_State *L )
{
    NSDictionary *params = CoronaLuaCreateDictionary(L, 2);
    
    NSMutableDictionary *m = [params mutableCopy];
    [FIRAnalytics logEventWithName:[NSString stringWithUTF8String:lua_tostring(L, 1)]
                        parameters:m];
    return 0;
}
int
firebaseAnalytics::SetUserProperties( lua_State *L )
{
    
    
    [FIRAnalytics setUserPropertyString:[NSString stringWithUTF8String:lua_tostring(L, 1)] forName:[NSString stringWithUTF8String:lua_tostring(L, 2)]];
    return 0;
}



// ----------------------------------------------------------------------------

CORONA_EXPORT int luaopen_plugin_firebaseAnalytics( lua_State *L )
{
	return firebaseAnalytics::Open( L );
}
