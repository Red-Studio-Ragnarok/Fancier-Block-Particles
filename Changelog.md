# Fancier Block Particles Version 0.8 Changelog

## Added

- Dynamic Weather system weather particles (rain & snow) are denser when a thunderstorm is happening
- Weather Particles Render Distance option

## Changed

- Fancy Rain and Fancy Snow config options have been replaced by Fancy Weather option
- Changed behavior of Fancy Block Placement with slabs placing slabs one on top of another should be a better experience
- Smoke particles are now slightly more transparent
- Updated `mcmod.info` to feature new description and better credits

## Fixed

- Fixed crashes with some moded blocks ([#7])
- Fixed Memory leak with `FBP#originalEffectRenderer` ([#139])
- Fixed Fancy Block Placement speed being broken because of an accidental change in 0.7

## Removed

- Removed Rest On Floor option ([#9])

## Optimization

- Vectors got a redesign as a result FBP should now be faster and use less VRAM
- Hex colors are now used instead of 4 separate R,G,B and A variables which makes rendering faster ([#10])
- As the result of a big code cleanup FBP as a whole should now be faster, load faster and use slightly less resources
- Optimized Rendering of Fancy Block Placement and all particles as a result, they should be faster and use slightly less GPU, RAM & VRAM
- FBP now uses its own math utilities and Jafama fast math library which should result in better performance
- Lossless textures compression resulting in 3.535 KB smaller mod size

## Internal

- Added documentation for Vector2D, Vector3D, and FBPRenderer
- Switched to [Anatawa] amazing [FG2.3 fork]
- Updated to Gradle 7.6
- Cleanup build.gradle & gradle.properties
- Gradle now automatically updates the version in `ModReference.java`
- Gradle now automatically increment the dev version by one each build
- Now use a forked version of [Universal Tweaks] for faster development environment loading times
- Cleanup the entire code
- Vectors redesign
- Major Refactors
- Renamed most of the variables from unreadable names to readable names
- Finished removing useless `@SideOnly`
- Removed useless `isRemote` checks
- Moved creation of particles to `ParticleUtil.java`

### Vectors Redesign

In 0.7 and before FBP was using Minecraft `Vec2f` and `Vec3d` but they have a few problems, by example `Vec3d` lack features like `set`, `copy` or even `scale` plus some of its features are written in a complex and slow manner.
Also, they have a `ZERO` variable which is useless and takes resource's for nothing this is even worst for `Vec2f` since it has tons of useless variables.
And having control over them is really nice, so we need to make our own.

Introducing `Vector2D` and `Vector3D` the brand-new vectors for FBP, which are faster easier to work with and come with great documentation!

### Changes in error handling

Non-critical try-catch blocks should now never throw a runtime exception or print a stack trace except when debug mode is added they should print a stack trace and count towards the issues counter and their respective counters (Rendering Issues, Physics Issues, Performance Issues, etc.)

#### Credits

- [WildMihai] for optimizations in `FBPRenderer`, `FBPConfigHandler` and deprecating Rest On Floor ([#9], [#10]) 

[#7]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/7
[#139]: https://github.com/TominoCZ/FancyBlockParticles/issues/139
[#9]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/pull/9
[#10]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/pull/10
[Anatawa]: https://github.com/anatawa12
[FG2.3 fork]: https://github.com/anatawa12/ForgeGradle-2.3
[Universal Tweaks]: https://www.curseforge.com/minecraft/mc-mods/universal-tweaks
[WildMihai]: https://github.com/WildMihai

---

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
- Draw calls are now batched which improve rendering performance by 2.5 times ([#5])
- Thanks to an immense code cleanup the FBP as a whole should be slightly faster and use slightly less RAM

## Internal

- General code cleanup

#### Credits

- [Rongmario] for batching draw calls ([#5])

[Rongmario]: https://github.com/Rongmario
[#5]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/pull/5

---

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

---

# Fancier Block Particles Version 0.5 Changelog

## Fixed

- Critical crash because ATs were not getting applied

---

# Fancier Block Particles Version 0.4 Changelog

## Changed

- Updated `mcmod.info`

---

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

---

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

---

# Fancier Block Particles Version 0.1 Changelog

## Added

- Logo to `mcmod.info`

## Changed

- Updated `mcmod.info` description & credits
- Updated default config
- Updated default bindings

## Fixed

- Fancy Block Placing ghost blocks when placing blocks rapidly when lagging ([#69])

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

[#69]: https://github.com/TominoCZ/FancyBlockParticles/issues/69
