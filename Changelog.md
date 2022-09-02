# Fancier Block Particles Version 0.3 Changelog

## Optimization

- Switched to AT's instead of Method Handle which leads to increased performance and cleaner code
- Optimized Particle Digging as a result it should use slightly faster and use slighty less RAM
- Optimized Particle Smoke as a result it should use slightly faster and use slighty less RAM
- Optimized Particle Rain as a result it should use slightly faster and use slighty less RAM
- Optimized Particle Snow as a result it should use slightly faster and use slighty less RAM
- Optimized Particle Manager as a result it should use slightly faster and use slighty less RAM

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
