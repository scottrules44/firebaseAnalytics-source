#ifndef _del_H__
#define _del_H__

#include "CoronaDelegate.h"
#import "CoronaLua.h"

@interface myAppDelegate : NSObject<CoronaDelegate>
@property(retain) id<CoronaRuntime> runtime;
@end
#endif
