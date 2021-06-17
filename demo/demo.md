 
**$ --version** 
``` 
1.2.0
``` 
**$ --help** 
``` 
Usage: skill-tracker [-hV] [COMMAND]
Player skill tracker for competitive games
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  game       Query game statistics
  match      Manage matches and outcomes
  player     Player feature
  changelog  Show last changelog
``` 
**$ game** 
``` 
Usage: skill-tracker game [-hV] [<game>] [COMMAND]
Query game statistics
      [<game>]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  ls    List registered games
  add   Register a game to play
  view  View the details of a game
  rm    Remove a registered game
``` 
**$ game ls** 
``` 
UnrealTournament
AgeOfEmpires2
Northgard
OrderOfTheGildedCompass
TableTennisVR
ToothAndTail
AshesOfSingularity
GalaxyTrucker
``` 
**$ game view AgeOfEmpires2** 
``` 
Top 5 players:
>  1: (μ)=33,1 (σ)=2,2 JK
>  2: (μ)=39,1 (σ)=4,2 BOT-Hardest
>  3: (μ)=29,3 (σ)=2,1 JDK
>  4: (μ)=27,4 (σ)=2,1 BOT-Hard
>  5: (μ)=27,2 (σ)=2,5 Wicus

Statistics:
> Most wins: JK
> Dominator: JK

Last 3 matches:
> 2021-04-20 (9cacfb0b) #1:JK & JDK & Wicus vs. #2:BOT-Hard & BOT-Hard & BOT-Hard
> 2021-04-05 (2e740fea) #1:JDK & JK vs. #2:BOT-Hard & BOT-Hard
> 2021-02-08 (2d5df6cb) #1:JK & JDK & Wicus vs. #2:BOT-Hard & BOT-Hard

``` 
**$ game view UnrealTournament** 
``` 
Top 5 players:
>  1: (μ)=33,7 (σ)=1,6 JK
>  2: (μ)=33,9 (σ)=1,7 Zeo
>  3: (μ)=31,5 (σ)=1,5 Andrew
>  4: (μ)=26,3 (σ)=2,0 Karl
>  5: (μ)=21,0 (σ)=1,5 Wicus

Statistics:
> Most wins: JK
> Dominator: BOT-Masterful

Last 3 matches:
> 2020-07-09 (deba86f7) #1:JK vs. #2:Zeo vs. #3:Andrew vs. #4:Karl vs. #5:Wicus vs. #6:GingerNinja
> 2020-07-09 (caf082b9) #1:Zeo vs. #2:Andrew vs. #3:JK vs. #4:Vikus vs. #5:Karl vs. #6:GingerNinja vs. #7:JDK
> 2020-07-05 (5063f995) #1:JK vs. #2:GingerNinja vs. #3:BOT-Skilled vs. #4:BOT-Skilled vs. #5:BOT-Skilled

``` 
**$ game view TableTennisVR** 
``` 
Top 3 players:
>  1: (μ)=36,0 (σ)=4,7 Etienne
>  2: (μ)=19,7 (σ)=5,9 Andrew
>  3: (μ)=16,6 (σ)=5,6 Jaco

Statistics:
> Most wins: Etienne
> Enthusiast: Etienne
> Dominator: Etienne

Last 3 matches:
> 2020-07-23 (ef6f043f) #1:Etienne vs. #2:Andrew
> 2020-07-23 (19efb9f4) #1:Etienne vs. #2:Andrew
> 2020-07-23 (b306481c) #1:Etienne vs. #2:Andrew

``` 
**$ match** 
``` 
Usage: skill-tracker match [-hV] [COMMAND]
Manage matches and outcomes
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  ls       List last few matches
  add      Add a match with an outcome
  view     View match information
  rm       Remove matches
  quality  Determine the quality of a match
  suggest  Calculate quality for every team setup
``` 
**$ match ls** 
``` 
Showing the last 10 matches:
> 2021-05-25 GalaxyTrucker #1:Andrew vs. #2:Marius vs. #3:Karl (8b59f711)
> 2021-04-20 AgeOfEmpires2 #1:JK & JDK & Wicus vs. #2:BOT-Hard & BOT-Hard & BOT-Hard (9cacfb0b)
> 2021-04-05 AgeOfEmpires2 #1:JDK & JK vs. #2:BOT-Hard & BOT-Hard (2e740fea)
> 2021-03-10 Northgard #1:Karl & BOT-Medium vs. #2:BOT-Medium & Andrew vs. #3:BOT-Medium & Marius (675b0d6d)
> 2021-03-10 Northgard #1:Andrew vs. #2:Marius vs. #2:Karl vs. #3:Rymert vs. #3:Shifty (85c921d4)
> 2021-02-08 AgeOfEmpires2 #1:JK & JDK & Wicus vs. #2:BOT-Hard & BOT-Hard (2d5df6cb)
> 2021-02-04 AgeOfEmpires2 #2:JDK vs. #1:BOT-Hardest (811b8edf)
> 2021-02-04 AgeOfEmpires2 #1:JK & JDK vs. #2:BOT-Hardest (4e162173)
> 2021-02-04 AgeOfEmpires2 #1:JK & JDK vs. #2:BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate (dd82f42b)
> 2021-02-04 AgeOfEmpires2 #1:Wicus & JDK & JK vs. #2:BOT-Hard & BOT-Hard & BOT-Hard (805f86b7)
``` 
**$ match view 9cacfb0b** 
``` 
2021-04-20 AgeOfEmpires2 (9cacfb0b)
> #2: (μ)=33 (σ)=2 JK
> #2: (μ)=29 (σ)=2 JDK
> #2: (μ)=27 (σ)=2 Wicus
> #3: (μ)=27 (σ)=2 BOT-Hard
> #3: (μ)=27 (σ)=2 BOT-Hard
> #3: (μ)=27 (σ)=2 BOT-Hard
``` 
**$ match suggest --help** 
``` 
Usage: skill-tracker match suggest [-hV] -g=<game> -s=<teamSize>
                                   [v<teamSize>...] [-s=<teamSize>
                                   [v<teamSize>...]]... <playerList>[,
                                   <playerList>...]...
Calculate quality for every team setup
      <playerList>[,<playerList>...]...
                      Players in the game
  -g, --game=<game>
  -h, --help          Show this help message and exit.
  -s, --setup=<teamSize>[v<teamSize>...]
                      Team setup (e.g. 2v1v4)
  -V, --version       Print version information and exit.
``` 
**$ match suggest Andrew,JK,JDK,Wicus -g=AgeOfEmpires2 -s=2v2** 
``` 
2v2 AgeOfEmpires2 setups:
> (μ)=26 (σ)=4 	 Andrew
> (μ)=33 (σ)=2 	 JK
> (μ)=29 (σ)=2 	 JDK
> (μ)=27 (σ)=2 	 Wicus

Well-balanced games:
> 80% (Andrew, JK) vs. (JDK, Wicus)
> 75% (Andrew, JDK) vs. (Wicus, JK)

Fair games:
> 56% (Andrew, Wicus) vs. (JK, JDK)
``` 
**$ match quality --help** 
``` 
Usage: skill-tracker match quality [-hV] -g=<game> <teams>...
Determine the quality of a match
      <teams>...
  -g, --game=<game>   Game to calculate for
  -h, --help          Show this help message and exit.
  -V, --version       Print version information and exit.
``` 
**$ match quality Andrew,JK Wicus,JDK -g UnrealTournament** 
``` 
UnrealTournament match quality: 1% (unfair/unknown)
Team (μ)=33 (σ)=2:
> (μ)=32 (σ)=1 Andrew
> (μ)=34 (σ)=2 JK

Team (μ)=18 (σ)=4:
> (μ)=21 (σ)=2 Wicus
> (μ)=14 (σ)=6 JDK

``` 
**$ player** 
``` 
Usage: skill-tracker player [-hV] [COMMAND]
Player feature
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  view  View player detail
  ls    List all players
  mv    Change name / combine players
``` 
**$ player ls** 
``` 
Andrew          BOT-Adept       BOT-Average     BOT-Experienced BOT-Hard        
BOT-Hardest     BOT-Inhuman     BOT-Masterful   BOT-Medium      BOT-Moderate    
BOT-Novice      BOT-Skilled     ChallengingAI   Charlemagne     Charles         
David           Etienne         GingerNinja     JDK             JK              
Jaco            JacoWiese       Julia           Karl            Marius          
NikRich         Pieter          Pyroet          Rymert          Shifty          
Stephan         Tim             Vikus           Wicus           Zeo             

``` 
**$ player view JK** 
``` 
JK has played a total of 3 games over 71 matches
Last played: AgeOfEmpires2, 2021-04-20 (2 months ago)

Top 3 skilled games:
> UnrealTournament at 29,0
> AgeOfEmpires2 at 26,5
> ToothAndTail at 11,7

Top 3 ranked games:
> #1 in UnrealTournament
> #1 in ToothAndTail
> #1 in AgeOfEmpires2

Statistics
> Most played: AgeOfEmpires2
> Comrade: Wicus
> Camaraderie: Andrew, BOT-Hard, GingerNinja, JDK, Stephan, Wicus
> Dominating: AgeOfEmpires2

``` 
**$ player view Andrew** 
``` 
Andrew has played a total of 7 games over 44 matches
Last played: GalaxyTrucker, 2021-05-25 (3 weeks ago)

Top 3 skilled games:
> UnrealTournament at 27,2
> AgeOfEmpires2 at 15,1
> Northgard at 14,2

Top 3 ranked games:
> #1 in Northgard
> #1 in GalaxyTrucker
> #2 in ToothAndTail

Statistics
> Most played: UnrealTournament
> Comrade: JK

``` 
**$ player view Wicus** 
``` 
Wicus has played a total of 2 games over 53 matches
Last played: AgeOfEmpires2, 2021-04-20 (2 months ago)

Top 2 skilled games:
> AgeOfEmpires2 at 19,7
> UnrealTournament at 16,4

Top 2 ranked games:
> #5 in UnrealTournament
> #5 in AgeOfEmpires2

Statistics
> Most played: AgeOfEmpires2
> Comrade: JK
> Camaraderie: JK, Zeo

``` 
**$ player view Wicus -g=AgeOfEmpires2** 
``` 
Wicus is #5 in AgeOfEmpires2 with 28 matches
Last played: 2021-04-20 (2 months ago)
      Skill: 19,7
       Mean: 27,2μ
     Stddev:  2,5σ

Statistics
> Nemesis: Andrew
> Comrade: JDK
> Guardian: JK

``` 
**$ player view JK -g=UnrealTournament** 
``` 
JK is #1 in UnrealTournament with 29 matches
Last played: 2020-07-09 (11 months ago)
      Skill: 29,0
       Mean: 33,7μ
     Stddev:  1,6σ

Statistics
> Nemesis: BOT-Masterful
> Comrade: Wicus
> Camaraderie: GingerNinja, Wicus
> Wards: GingerNinja

``` 
**$ player view JK -g=AgeOfEmpires2** 
``` 
JK is #1 in AgeOfEmpires2 with 40 matches
Last played: 2021-04-20 (2 months ago)
      Skill: 26,5
       Mean: 33,1μ
     Stddev:  2,2σ

Statistics
> Minions: BOT-Moderate, JDK
> Comrade: JDK
> Camaraderie: Andrew, BOT-Hard, JDK, Stephan
> Wards: BOT-Hard, JDK, Stephan, Wicus

``` 
**$ player view Etienne** 
``` 
Etienne has played a total of 1 games over 9 matches
Last played: TableTennisVR, 2020-07-23 (11 months ago)

Top 1 skilled games:
> TableTennisVR at 21,8

Top 1 ranked games:
> #1 in TableTennisVR

Statistics
> Most played: TableTennisVR
> Obsession: TableTennisVR
> Dominating: TableTennisVR

``` 
**$ changelog** 
``` 
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
``` 
