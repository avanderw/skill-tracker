# Changelog for skill-tracker
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See [README](README.md) for more project related information.

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