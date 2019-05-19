# ![osumerIcon64px](http://mob41.github.io/images/osumer/osumerIcon_64px.png) osumer2 [![Download osumer](http://img.shields.io/github/downloads/mob41/osumer/latest/total.svg)](https://github.com/mob41/osumer/releases/latest) [![Download osumer](http://img.shields.io/github/downloads/mob41/osumer/total.svg)](https://github.com/mob41/osumer/releases/latest)
The easiest,express osu! beatmap downloader helper!

>[Please donate to osu!. It is a great game, right?](https://osu.ppy.sh/p/support)

Put a star if you like this software! I would be happy to continue developing this software!

## Disclaimer
This software does not contain malicious code that will send username and password to another server other than osu!'s login server. You can feel free to look through the code. If you still feel uncomfortable with this software, you can simply stop using it.

All osu! credentials are **ONLY** used for logging into osu! forum. No data are programmed to send outside osu! servers. ```osumer-osums``` module is the only one to send credentials to osu! servers. ```osumer-lib``` module contains code to save the credentials into the file-system.

osumer2 is licensed under the MIT License. You may not be able to hold the owner liable for any claims and damages. By using osumer2 Overlay, you agree with the terms and license of osumer2.

## Features

- Works **without specifying beatmap's link manually**, and **activates downloads automatically in-game**.

- **Requires osu! user account to work.**, and **downloads beatmaps from the osu! forum officially without mirroring.**

- Downloads automatically when a beatmap link argument is passed to the application.

- Users can download beatmaps manually by pasting a beatmap link or ID into the user interface.

- Listed background downloading, queuing.

- Supports multi-downloading.

- Customizable preferences.

- Download sounds/tones.

- Estimated Time Arrival (ETA) and elapsed time.

- Beatmap search and management.

## Screenshots

- osumerExpress - One-click activate download in-game (Requires installation [[click here]](https://github.com/mob41/osumer#installation-of-osumerexpress))

	![gif 1](http://mob41.github.io/images/osumer/osumerExpressGif.gif)

- Download beatmaps manually using a beatmap link or ID

	![Cap1](http://mob41.github.io/images/osumer/cap1.PNG)

- Multi-downloading and queuing

	![Cap2](http://mob41.github.io/images/osumer/cap2.PNG)

## Installation instructions:

> :warning: <b>osumer is no longer cross-platform compatible. It only supports Windows, and tested on Windows 10 Build 1809.</b>

> ```osumer-setup.msi``` is for advanced users only.

1. Install osumer2
        
    1. Via ```osumer-updater``` (Recommended)
    
        [:checkered_flag: **Click here to install directly using osumer-updater** :checkered_flag:](https://github.com/mob41/osumer-updater/releases/download/legacy/osumer-updater.exe)
    
    2. Via GitHub Releases Assets (Only for first-time/advanced users)
    
        1. Download <b>Release.zip</b> from the [latest releases](https://github.com/mob41/osumer/releases/latest). and extract it.
        
        2. Execute <b>setup.exe</b>

2. Make some modifications to the system settings.

    - Windows 10:

        1. Navigate to ```Windows Start > Settings > Apps > Default apps```
        2. Set ```Web browser``` as ```osumer2```
        3. If it does not work, please visit [this issue](https://github.com/mob41/osumer/issues/22#issuecomment-487303717) to see if it can help.

    - Windows 7 or older:

        1. Navigate to ```Control Panel```.
        2. Search for ```default programs``` and click on ```Default Programs```.
        3. Click ```Set program access and computer defaults```.
        4. Pull-down the ```Custom``` configuration.
        5. Press ```osumer2``` in the ```Choose a default web browser``` section, and click ```OK```.
    
4. You can try it by starting up osu!, and go to a multiplayer match with a beatmap you don't have.

5. Do the same thing that you do in usual, click the beatmap icon. A overlay will pop out.

Put a star if you like this software! I would be happy to continue developing this software!

## My osu! account

[![osu!account](http://lemmmy.pw/osusig/sig.php?colour=pink&uname=mob41)](https://osu.ppy.sh/u/9125315)

## Known issues:

- Beatmap searching features are removed currently
- ```View Dumps``` does not work at the moment. Navigate to ```%localappdata%\osumerExpress\dumps``` instead for viewing dumps.
- ```Start```, ```Pause```, ```Stop``` buttons in ```Queues``` are not implemented to work at the moment.

## Compiling

```osumer2``` is completely open-source and can be compiled easily. It is programmed in several languages so it requires multiple compilers to be installed in your machine. It can **ONLY** be compiled in **Windows** environment.

1. Prepare the development environment

    1. Java
        
        1. Java Development Kit Standard Edition 8 Version 212 (32 bit)
        
        2. Eclipse IDE (if necessary)
        
        3. Apache Maven (only if Eclipse IDE not installed)
        
    2. C++
    
        1. Visual Studio 2017 (With ```Desktop Application, ```Visual C++ CMake Tools``` installed)
        
        2. Python 3.7 (for compiling ```gl3w```)
        
    3. Windows Setup
    
        1. "Visual Studio Installer Projects" Module
        
2. Clone this repository via Git or download zip.
        
3. Compiling Java

    1. Via Eclipse IDE
    
        1. Import the parent project pom. ```osumer/pom.xml```
        
        2. Build the project. ```osumer > Run As... > Maven install```
        
    2. Via Apache Maven
    
        1. Navigate to the parent project
        
        2. Execute ```mvn package```
        
4. Compiling C++

    1. Start up Visual Studio 2017. From the menubar, navigate ```File > Open... > CMake...```.
    
    2. Navigate to the root folder of the ```osumer2``` repository. Wait the CMake cache to be generated.
    
    3. Build the project. ```CMake > Build All```.
    
    4. If you experience ```...src/gl3w/gl3w.c``` not found error, comment all ```add_subdirectory``` except for ```add_subdirectory(gl3w)``` in the root ```CMakeLists.txt```. Build it once, and uncomment back the lines. And try to build it again.

5. All compiled files are stored in the ```target``` folder in the repository.    

6. Packaging Setup

    1. Navigate to ```osumer-setup``` module, and open the ```osumer-setup.sln``` solution.
    
    2. Build the project. ```Release``` configuration takes more time to compress the files.
    
7. The resultant setup is stored in ```Release``` or ```Debug``` folder.

## JRE License

The installer is bundled with Java Runtime Environment SE 8 Version 212 (32-bit). By installing osumer2, you agree with the terms and license of Oracle Java SE 8.

Please refer to http://java.com/licensereadme and https://java.com/bc_license for more details.

## Application License

[tl;dr](https://tldrlegal.com/license/mit-license) Based on MIT License. 

```
MIT License

Copyright (c) 2016 mob41

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
 