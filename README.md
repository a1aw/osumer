# ![osumerIcon64px](http://mob41.github.io/images/osumer/osumerIcon_64px.png) OsuMer
Osu! beatmap download helper (Now available for SNAPSHOT version testing, please post issues if things go wrong. [[Download]](https://github.com/mob41/osumer/releases))

>[Please donate to osu!. This software does not aim to lead people not to donate. osu! is a great game, right?](https://osu.ppy.sh/p/support)

![gif 1](http://mob41.github.io/images/osumer/osumerExpressGif.gif)

![Capture 1](http://mob41.github.io/images/osumer/cap1.PNG)

![Capture 2](http://mob41.github.io/images/osumer/cap2.PNG)

Requires osu! user account to work. This application will download beatmaps automatically when an arugment of the beatmap URL is passed to the application.

In the user interface, users can download beatmaps directly by pasting a beatmap URL, and press the ```Download & Import``` button.

## Disclaimer
This software does not contain malicious code that will send username and password to another server other than osu!'s login server. You can feel free to look through the code. If you still feel uncomfortable with this software, you can simply stop using it. Thank you!

The application only sends the ```username``` and ```password``` parameter to ```http://osu.ppy.sh/forum/ucp.php?mode=login``` in [Osu.java#L112](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/io/Osu.java#L112). And saves the username and password specified in the user interface to local configuration in [Config.java](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/Config.java). Unless the data are sniffed by a hacker or your friends want to play tricks on you, the data is not sent to anywhere other than osu! login server. (alright also sent to, proxies, firewalls, routers, switches, DNS servers, etc.)

By using this software, I take no responsibility here. Use in your own risk.

## Installation of osumerExpress
> :warning: This only applies on Windows Vista or newer. Non-Windows environment are currently not supported, portable version instead (.jar)

Now here goes to the most exciting part. One-click to activate downloads multiplayer in-game.

1. Download the [latest release of osumer](https://github.com/mob41/osumer/releases/latest), the Windows executable (```osumer.exe```) (newer or equal to ```0.0.2-SNAPSHOT```)

2. Start up ```osumer.exe``` with administrative privileges.

3. If you haven't installed ```osumerExpress```, press ```Install osumerExpress```.

4. Enter your osu! account login information, and press ```Save configuration for osumerExpress``` button.

5. Select your default browser to be redirected.

6. Tick Automatically switch to browser for non-beatmaps. This must be ticked otherwise non-beatmaps URLs (e.g. http://www.google.com) will not be opened.

7. Tick Switch to browser if no "-ui" argument specified. This must be ticked otherwise the all shortcuts will be broken. It will just open the osumer UI instead of the browser. But, becareful, from now on you have to start ```osumer.exe``` with the ```-ui``` argument. (e.g. ```C:\Program Files\osumer\osumer.exe -ui``` to have a user interface popped out)

8. Once you have set up the osumerExpress settings (e.g. default browser, optimizations). Press ```Save configuration```

9. Let's move to the ```Control Panel```. Search for ```default programs```. Click ```Default Programs```. Then, click ```Set program access and computer defaults```. Pull-down (click the arrow at the right hand side) the ```Custom``` configuration. Press ```osumerExpress``` in the ```Choose a default web browser:``` part. Click ```OK``` and you're done! This sets ```osumerExpress``` as the default web browser to receive beatmaps links from osu!
    ![Capture 3](http://mob41.github.io/images/osumer/osumer_defprgs_instruct.gif)
    
10. You can try it by starting up osu! and go to a multiplayer match with a beatmap you don't have. Do the same thing that you do in usual, click the beatmap icon. A downloading dialog will pop out.

## Development stage
- [x] Osu web-login
- [x] Graphical user interface
- [x] Quick download & import interface (osumerExpress)
- [ ] Command-line interface

## Issues
- Currently no issues found. If you encounter some, please post a [new issue](https://github.com/mob41/osumer/issues/new).
- Questions are also welcome.

## License
Based on MIT License.

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
 