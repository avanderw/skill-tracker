# Changelog for skill-tracker
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See [README](README.md) for more project related information.

## [v1.3.1] (Maintenance release)
*Released on 2021-06-23*

### Fixed
- Fix database not upgrading with latest changesets

## [v1.3.0] (Feature release)
*Released on 2021-06-23*

### Added
- Add first match to the player detail view
- Add HIndex statistic to the player detail view
- Add collection services for the new categories
- Add mustache template for player detail view
- Add win streak statistic
- Add achievement command to describe achievements
- Add dominator stat to player
- Add Guardian stat to the player
- Add Enthusiast stat to player and game
- Add Comrade stat to player
- Add most played stat to player view
- Add most wins stat to the game view
- Add nemesis and minion statistic

### Changed
- Change the player game view to align with player detail
- Change packaging to cater for Badge, Challenge, Achievement, and Trophy
- Change game CamelCase to Title Case
- Change rule that the dominator must have the highest skill instead of win ratio
- Change formatting of the stats to align on the name

### Removed
- Remove dead code
- Remove most win stat
- Remove generated file
- Remove Comaraderie statistic

### Fixed
- Fix plural not correctly showing for matches
- Fix incorrect ranking on match view
- Fix database migrating wrong column
- Fix Guardian to have higher skill than player

## [v1.2.0] (Feature release)
*Released on 2020-08-13*

### Added
- Add command to view last entry in the changelog
- Add a reconnect ability for when the connection is down to discord
- Add JaCoCo to build

### Changed
- Change discord filename attachment to refer to the command used

### Fixed
- Fix NullPointer by making --setup option required

## [v1.1.0] (Feature release)
*Released on 2020-07-21*

### Added
- Add wildcard to game names to allow shorter commands
- Add rank option to 'match add' to reduce errors on input
- Add link between pom version and picocli main version


### Changed
- Update 'match suggest' to select NvM from a pool larger than N+M
- Update 'player ls' output to use screen space more efficiently
- Change team setup on 'match suggest' to be an option
- Update formatting for names to not escape
- Change name of assembly definition

### Bug fixes
- Fix NullPointer with 'match add' when no game is found

## [v1.0.1] (Maintenance release)
*Released on 2020-07-14*

### Changed
- Update 'match suggest' formatting
- Update team display on 'match quality' functionality
- Change file encoding so that it executes in UNIX

### Bug fixes
- Fix NullPointer when game is not found
- Fix ANSI bug with Discord when running in ANSI capable terminal

## [v1.0.0] (Major release, Update recommended)
*Released on 2020-07-10*

### Added
- Discord interface
- Commandline interface

#### Game
- Register a game
- View the details of a game
- View leaderboard
- Remove a game
- List games

#### Match
- Add a match
- Remove a match
- View the details of a match
- List matches
- Determine the quality of a match
- Calculate the quality of a match for every team combination

#### Player
- View the details of a player
- Combine player statistics
- List players