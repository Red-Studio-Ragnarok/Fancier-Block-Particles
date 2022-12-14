# Fancier Block Particles Version 0.8 Changelog

## Fixed

- Fixed crashes with some moded blocks (#7)

## Optimization

## Internal

- Finished removing useless "@SideOnly"

### Changes in error handling

Non critical try-catch blocks should now never throw a runtime exception or print a stack trace except when debug mode is added they should print a stack trace and count towards the issues counter and their respective counters (Rendering Issues, Physics Issues, Performance Issues, etc.)

# Fancier Block Particles Version 0.7 Changelog

## Showcase

- Particles now renders 2.5 times faster (Immense thanks to Rongmario!)

## Changed

- Default key for the blacklist menu changed from none to B

## Fixed

- Fixed GUI not saving config to file

## Removed

- (Testing) Removed some checks in FBPModelHelper because I am assuming mods aren't weird
- Removed FBPVertexUtil as it was unused

## Optimization

- Optimized main class as a result, it should be slightly faster and use slightly less RAM
- Draw calls are now batched thanks to Rongmario which improve rendering performance by a ton
- Thanks to a immense code cleanup the FBP as a whole should be slightly faster and use slightly less RAM

## Internal

- General code cleanup

# Fancier Block Particles Version 0.6 Changelog

## Changed

- Disabled buttons now appears greyed out in the menu
- Changed Blacklist GUI bar and cursor
- Changed some Blacklist GUI from green to white
- Changed description text from green to white
- Changed confirmation GUI text to be clearer about the warning and changed the color from yellow to white & red
- Changed page order so that the page with only one slider in it is the last page

## Fixed

- Description getting behind back and next buttons
- Menu showing Fancy Block Particles instead of Fancier Block Particles
- Being able to click disabled buttons in the menu

## Removed

- Removed the sliding on text when the screen is too small

## Optimization

- Reworked all the GUI code, which is now faster, smaller, and easier to work with

## Internal

- Switched every bit of text to .lang which allows anyone to create a translation for their language

# Fancier Block Particles Version 0.5 Changelog

## Fixed

- Critical crash because ATs were not getting applied

# Fancier Block Particles Version 0.4 Changelog

## Changed

- Updated mcmod.info

# Fancier Block Particles Version 0.3 Changelog

## Optimization

- Switched to AT's instead of Method Handle which leads to increased performance and cleaner code
- Optimized Particle Digging as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Smoke as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Rain as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Snow as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Manager as a result, it should be slightly faster and use slightly less RAM
- Optimized the Event Handler as a result, it should be faster and use slightly less RAM
- Optimized the Renderer as a result, it should be faster and use less RAM and VRAM

All these optimizations result in a 9% faster mod loading time, which makes Fancier Block Particles load 25% faster than Fancy Block Particles

## Internal

- General code cleanup

# Fancier Block Particles Version 0.2 Changelog

## Fixed

- Fixed bug report button linking to the wrong repository
- Filled empty catch blocks
- Fixed potential NullPointerException when checking for blacklisted blocks name

## Removed

- Removed cartoon mode
- Removed smooth/fast animation lighting button

## Optimization

- Made the renderer faster
- Made the Fancy Block Placing faster
- Made the particle manager slightly faster
- Made FBP PreInit slightly faster
- Made removing blacklisted blocks slighty faster

## Internal

- Finished changing the syntax
- Refactored FBPRenderUtil (In Util) to FBPRenderer (in Renderer)
- Code Cleanup

# Fancier Block Particles Version 0.1 Changelog

## Added

- Logo to mcmod.info

## Changed

- Updated mcmod.info description & credits
- Updated default config
- Updated default bindings

## Fixed

- Fancy Block Placing ghost blocks when placing blocks rapidly when lagging

## Optimization

- Made the particles use slightly less ram
- Made the particle renderer slightly faster
- Made the particle manager slightly faster
- Made the particle manager use slightly less ram
- Made the Fancy Block Placing slightly faster
- Lossless compression

## Internal

- Slowly changing the syntax
- Created ModReference.java and moved everything to it
- Rename some variables
- General code cleanup
