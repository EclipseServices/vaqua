/*
 * Copyright (c) 2015 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

#import "AquaSidebarBackground.h"
#import <Availability.h>

@implementation AquaSidebarBackground {
    NSVisualEffectView *backgroundView;
    NSMutableArray<NSVisualEffectView*> *selectionViews;
    NSVisualEffectMaterial material;
    NSVisualEffectMaterial selectionMaterial;
}

- (AquaSidebarBackground *) initWithFrame: (NSRect) frameRect {
    self = [super initWithFrame: frameRect];

    if (self) {
        if (floor(NSAppKitVersionNumber) <= NSAppKitVersionNumber10_10_Max) {
            material = NSVisualEffectMaterialAppearanceBased;
        } else {
            material = NSVisualEffectMaterialSidebar;
        }
        selectionMaterial = 4;

        self.wantsLayer = YES;
        self.appearance = [NSAppearance appearanceNamed: NSAppearanceNameVibrantLight];
        self.autoresizesSubviews = YES;
        backgroundView = [[NSVisualEffectView alloc] initWithFrame: self.bounds];
        selectionViews = [[NSMutableArray alloc] init];
        backgroundView.wantsLayer = YES;
        backgroundView.autoresizingMask = NSViewWidthSizable+NSViewHeightSizable;
        backgroundView.blendingMode = NSVisualEffectBlendingModeBehindWindow;
        [backgroundView setMaterial:material];
        [self addSubview: backgroundView];
    }

    return self;
}

- (void) updateSelectionViews: (int*) data {
    int *p = data;
    int count = p ? *p++ : 0;
    int index = 0;
    int currentCount = (int) selectionViews.count;
    float width = self.bounds.size.width;

//    NSLog(@"Updating selection views %@ %f %f %@ %f %f",
//        [self description],
//        self.frame.size.width,
//        self.frame.size.height,
//        [backgroundView description],
//        backgroundView.frame.size.width,
//        backgroundView.frame.size.height);

    for (index = 0; index < count; index++) {
        int y = *p++;
        int h = *p++;
        NSRect frame = NSMakeRect(0, y, width, h);
        NSVisualEffectView *v = index < currentCount ? [selectionViews objectAtIndex:index] : nil;
        if (v) {
            v.frame = frame;
        } else {
            v = [[NSVisualEffectView alloc] initWithFrame: frame];
            v.wantsLayer = YES;
            v.autoresizingMask = NSViewWidthSizable;
            v.blendingMode = NSVisualEffectBlendingModeBehindWindow;
            v.material = selectionMaterial;
            [selectionViews addObject: v];
            [self addSubview: v];
        }
    }

    for (int i = index; i < currentCount; i++) {
        NSVisualEffectView *v = selectionViews.lastObject;
        [selectionViews removeLastObject];
        [v removeFromSuperview];
    }

    self.needsDisplay = YES;
}

- (NSVisualEffectView *) createVisualEffectViewWithFrame: (NSRect) frame {
    NSVisualEffectView *v = [[NSVisualEffectView alloc] initWithFrame: frame];
    v.blendingMode = NSVisualEffectBlendingModeBehindWindow;
    v.wantsLayer = YES;
    v.material = material;
    return v;
}

- (BOOL) isFlipped {
    return YES;
}

@end
