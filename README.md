# ![osumerIcon64px](http://mob41.github.io/images/osumer/osumerIcon_64px.png) osumer [![Download osumer](http://img.shields.io/github/downloads/mob41/osumer/latest/total.svg)](https://github.com/mob41/osumer/releases/latest)
Osu! beatmap download helper

>[Please donate to osu!. It is a great game, right?](https://osu.ppy.sh/p/support)

Put a star if you like this software! I would be happy to continue developing this software!

## Disclaimer
This software does not contain malicious code that will send username and password to another server other than osu!'s login server. You can feel free to look through the code. If you still feel uncomfortable with this software, you can simply stop using it. Thank you!

The application only sends the ```username``` and ```password``` parameters to ```https://osu.ppy.sh/forum/ucp.php?mode=login``` in [Osu.java](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/io/Osu.java) for logging in to osu! forum. And saves the username and password specified in the user interface to local configuration in [Config.java](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/Config.java). Unless the data are sniffed by a hacker or your friends want to play tricks on you, the data is not sent to anywhere other than osu! login server. (alright also sent to, proxies, firewalls, routers, switches, DNS servers, etc.)

By using this software, I take no responsibility here. Use in your own risk.

## Features

- This software can work **without specifying beatmap's URL manually**, and **activate downloads automatically in-game**. More details, check **downstairs** or [[click here for quick scroll]](https://github.com/mob41/osumer#installation-of-osumerexpress) and [[screenshots here]](https://github.com/mob41/osumer#screenshots).

- **Requires osu! user account to work.** The software will **download beatmaps from the osu! forum officially without mirroring.** If you are still uncomfortable with specifying your account to the software, you can simply stop using it. See [disclaimer](https://github.com/mob41/osumer#disclaimer).

- It will start beatmaps download automatically when an arugment of the beatmap URL is passed to the application.

- In the user interface, users can download beatmaps manually by pasting a beatmap URL, and press the ```Download & Import``` button.

## Screenshots

- osumerExpress - One-click activate download in-game (Requires installation [[click here]](https://github.com/mob41/osumer#installation-of-osumerexpress))

	![gif 1](http://mob41.github.io/images/osumer/osumerExpressGif.gif)

- Download beat-maps manually using a URL

	![Cap1](http://mob41.github.io/images/osumer/cap1.PNG)

- Downloading dialog

	![Cap2](http://mob41.github.io/images/osumer/cap2.PNG)

## Installation of osumerExpress
> :warning: This only applies on Windows Vista or newer. Non-Windows environment are currently not supported.

Now here goes to the most exciting part. One-click to activate downloads multiplayer in-game.

1. Download the [latest release of osumer](https://github.com/mob41/osumer/releases/latest), the Windows executable (```osumer.exe```) (newer or equal to ```0.0.2-SNAPSHOT```)

2. Start up ```osumer.exe``` with administrative privileges.

3. If you haven't installed ```osumerExpress```, press ```Install osumerExpress```.

4. Enter your osu! account login information, and press ```Save configuration for osumerExpress``` button.

5. Select your default browser to be redirected.

6. Tick Automatically switch to browser for non-beatmaps. This must be ticked otherwise non-beatmaps URLs (e.g. http://www.google.com) will not be opened.

7. Once you have set up the osumerExpress settings (e.g. default browser, optimizations). Press ```Save configuration```

8. Let's move to the ```Control Panel```. Search for ```default programs```. Click ```Default Programs```. Then, click ```Set program access and computer defaults```. Pull-down (click the arrow at the right hand side) the ```Custom``` configuration. Press ```osumerExpress``` in the ```Choose a default web browser:``` part. Click ```OK``` and you're done! This sets ```osumerExpress``` as the default web browser to receive beatmaps links from osu!
    ![Capture 3](http://mob41.github.io/images/osumer/osumer_defprgs_instruct.gif)
    
9. You can try it by starting up osu! and go to a multiplayer match with a beatmap you don't have. Do the same thing that you do in usual, click the beatmap icon. A downloading dialog will pop out.


## Development stage
- [x] Osu web-login
- [x] Graphical user interface
- [x] Quick download & import interface (osumerExpress)
- [ ] Command-line interface

These functions will include in next v2 version if download counts and stars interested me to continue to develop:

- [ ] Multi-downloading / Job Queuing
- [ ] Search beatmaps

Put a star if you like this software! I would be happy to continue developing this software!

## Issues
- Currently no issues found. If you encounter some, please post a [new issue](https://github.com/mob41/osumer/issues/new).
- Questions are also welcome.

## My osu! account

[![osu!account](http://osusig.ppy.sh/image1.png?uid=9125315&m=0)](https://osu.ppy.sh/u/9125315)

## License
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
 