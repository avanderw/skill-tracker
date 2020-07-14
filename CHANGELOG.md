# skill-tracker changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) 
and adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

See [README](README.md) for more project related information.

## [v1.0.1] (Maintenance release)
Released on 2020-07-14

### Changed
- Update 'match suggest' formatting
- Update team display on 'match quality' functionality
- Change file encoding so that it executes in UNIX

### Bug fixes
- Fix NullPointer when game is not found
- Fix ANSI bug with Discord when running in ANSI capable terminal

## [v1.0.0] (Major release, Update recommended)
Released on 2020-07-10

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