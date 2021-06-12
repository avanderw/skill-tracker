 
**$ skilltracker --version** 
``` 
1.2.0
``` 
**$ skilltracker --help** 
``` 
Usage: skill-tracker [-hV] [COMMAND]
Player skill tracker for competitive games
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  game       Manage game information
  match      Manage matches and outcomes
  player     Player feature
  changelog  Show last changelog
``` 
**$ skilltracker game** 
``` 
Usage: skill-tracker game [-hV] [<game>] [COMMAND]
Manage game information
      [<game>]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  ls    List registered games
  add   Register a game to play
  view  View the details of a game
  rm    Remove a registered game
``` 
**$ skilltracker game ls** 
``` 
UnrealTournament (Draw Probability: )
AgeOfEmpires2 (Draw Probability: )
Northgard (Draw Probability: )
OrderOfTheGildedCompass (Draw Probability: )
TableTennisVR (Draw Probability: )
ToothAndTail (Draw Probability: )
AshesOfSingularity (Draw Probability: )
GalaxyTrucker (Draw Probability: )
``` 
**$ skilltracker game view AgeOfEmpires2** 
``` 
Top 10 players:
>  1: (μ)=33 (σ)=2 JK
>  2: (μ)=39 (σ)=4 BOT-Hardest
>  3: (μ)=29 (σ)=2 JDK
>  4: (μ)=27 (σ)=2 BOT-Hard
>  5: (μ)=27 (σ)=2 Wicus
>  6: (μ)=26 (σ)=4 Andrew
>  7: (μ)=27 (σ)=4 Stephan
>  8: (μ)=17 (σ)=4 Pieter
>  9: (μ)=27 (σ)=8 Charlemagne
> 10: (μ)=18 (σ)=6 Tim

Showing the last 5 matches:
> 2021-04-20 (9cacfb0b) #0:JK & JDK & Wicus vs. #1:BOT-Hard & BOT-Hard & BOT-Hard
> 2021-04-05 (2e740fea) #0:JDK & JK vs. #1:BOT-Hard & BOT-Hard
> 2021-02-08 (2d5df6cb) #0:JK & JDK & Wicus vs. #1:BOT-Hard & BOT-Hard
> 2021-02-04 (811b8edf) #1:BOT-Hardest vs. #0:JDK
> 2021-02-04 (4e162173) #0:JK & JDK vs. #1:BOT-Hardest
``` 
**$ skilltracker game view UnrealTournament** 
``` 
Top 10 players:
>  1: (μ)=34 (σ)=2 JK
>  2: (μ)=34 (σ)=2 Zeo
>  3: (μ)=32 (σ)=1 Andrew
>  4: (μ)=26 (σ)=2 Karl
>  5: (μ)=21 (σ)=2 Wicus
>  6: (μ)=27 (σ)=4 Vikus
>  7: (μ)=16 (σ)=2 GingerNinja
>  8: (μ)=25 (σ)=5 NikRich
>  9: (μ)=26 (σ)=5 BOT-Adept
> 10: (μ)=15 (σ)=3 BOT-Skilled

Showing the last 5 matches:
> 2020-07-09 (deba86f7) #0:JK vs. #1:Zeo vs. #2:Andrew vs. #3:Karl vs. #4:Wicus vs. #5:GingerNinja
> 2020-07-09 (caf082b9) #0:Zeo vs. #1:Andrew vs. #2:JK vs. #3:Vikus vs. #4:Karl vs. #5:GingerNinja vs. #6:JDK
> 2020-07-05 (5063f995) #0:JK vs. #1:GingerNinja vs. #2:BOT-Skilled vs. #3:BOT-Skilled vs. #4:BOT-Skilled
> 2020-07-04 (3be77054) #0:BOT-Masterful & BOT-Masterful & BOT-Masterful & BOT-Masterful & BOT-Masterful vs. #1:Andrew & Zeo & BOT-Masterful & Wicus & JK
> 2020-07-04 (31d749dc) #0:Zeo & Andrew & JK & Wicus & GingerNinja vs. #1:BOT-Adept & BOT-Adept & BOT-Adept & BOT-Adept & BOT-Adept
``` 
**$ skilltracker match** 
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
**$ skilltracker match ls** 
``` 
Showing the last 10 matches:
> 2021-05-25 GalaxyTrucker #0:Andrew vs. #1:Marius vs. #2:Karl (8b59f711)
> 2021-04-20 AgeOfEmpires2 #0:JK & JDK & Wicus vs. #1:BOT-Hard & BOT-Hard & BOT-Hard (9cacfb0b)
> 2021-04-05 AgeOfEmpires2 #0:JDK & JK vs. #1:BOT-Hard & BOT-Hard (2e740fea)
> 2021-03-10 Northgard #0:Karl & BOT-Medium vs. #1:BOT-Medium & Andrew vs. #2:BOT-Medium & Marius (675b0d6d)
> 2021-03-10 Northgard #0:Andrew vs. #2:Karl & Marius vs. #3:Rymert & Shifty (85c921d4)
> 2021-02-08 AgeOfEmpires2 #0:JK & JDK & Wicus vs. #1:BOT-Hard & BOT-Hard (2d5df6cb)
> 2021-02-04 AgeOfEmpires2 #1:BOT-Hardest vs. #0:JDK (811b8edf)
> 2021-02-04 AgeOfEmpires2 #0:JK & JDK vs. #1:BOT-Hardest (4e162173)
> 2021-02-04 AgeOfEmpires2 #0:JK & JDK vs. #1:BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate & BOT-Moderate (dd82f42b)
> 2021-02-04 AgeOfEmpires2 #0:Wicus & JDK & JK vs. #1:BOT-Hard & BOT-Hard & BOT-Hard (805f86b7)
``` 
**$ skilltracker match view 9cacfb0b** 
``` 
2021-04-20 AgeOfEmpires2 (9cacfb0b)
> #0: (μ)=33 (σ)=2 JK
> #0: (μ)=29 (σ)=2 JDK
> #0: (μ)=27 (σ)=2 Wicus
> #1: (μ)=27 (σ)=2 BOT-Hard
> #1: (μ)=27 (σ)=2 BOT-Hard
> #1: (μ)=27 (σ)=2 BOT-Hard
``` 
**$ skilltracker match suggest --help** 
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
**$ skilltracker match suggest Andrew,JK,JDK,Wicus -g=AgeOfEmpires2 -s=2v2** 
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
**$ skilltracker player** 
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
**$ skilltracker player ls** 
``` 
Andrew          BOT-Adept       BOT-Average     BOT-Experienced BOT-Hard        
BOT-Hardest     BOT-Inhuman     BOT-Masterful   BOT-Medium      BOT-Moderate    
BOT-Novice      BOT-Skilled     ChallengingAI   Charlemagne     Charles         
David           Etienne         GingerNinja     JDK             JK              
Jaco            JacoWiese       Julia           Karl            Marius          
NikRich         Pieter          Pyroet          Rymert          Shifty          
Stephan         Tim             Vikus           Wicus           Zeo             

``` 
**$ skilltracker player view JK** 
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

``` 
**$ skilltracker player view Andrew** 
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

``` 
**$ skilltracker changelog** 
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
