# OsuMer
Osu! beatmap download helper

![Capture 1](http://mob41.github.io/images/osumer/cap1.PNG)

![Capture 2](http://mob41.github.io/images/osumer/cap1.PNG)

Requires osu! user account to work. This application will download beatmaps automatically when an arugment of the beatmap URL is passed to the application.

In the user interface, users can download beatmaps directly by pasting a beatmap URL, and press the ```Download & Import``` button.

## Disclaimer
This software does not contain malicious code that will send username and password to another server other than osu!'s login server. You can feel free to look through the code. If you still feel uncomfortable with this software, you can simply stop using it. Thank you!

The application only sends the ```username``` and ```password``` parameter to ```http://osu.ppy.sh/forum/ucp.php?mode=login``` in [Osu.java#L112](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/io/Osu.java#L112). And saves the username and password specified in the user interface to local configuration in [Config.java](https://github.com/mob41/osumer/blob/master/src/main/java/com/github/mob41/osumer/Config.java). Unless the data are sniffed by a hacker or your friends want to play tricks on you, the data is not anywhere other than osu! login server.

By using this software, I take no responsibility here. Use in your own risk.

## Tricks (osumerExpress)
>This only applies on Windows Vista, 7, 8, 8.1, 10

Someone must wonder how this helps. Well, integration to web browsers are needed. As mentioned above, this application will download beatmaps automatically when an arugment of the beatmap URL is passed to the application.

In fact, once you press the download link of beatmap in multiplayer in osu!, osu! will find the default browser and starts the browser with the URL in the arguments.

This application also includes a function to start the browser instead of the UI if a ```-ui``` argument is not specified.

We are here to move the default browser, and replace it with this application (osumer).

1. First, find your default web browser. There're so many tutorials [on the web](https://www.google.com.hk/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=how%20to%20find%20your%20default%20web%20browser) about this.

2. Get to your web browser's working directory. You can also right-click your default web browser's shortcut, and click ```Open file location``` to achieve this. If you set ```Google Chrome``` as your default web browser, the directory would be ```C:\Program Files (x86)\Google\Chrome\Application``` or ```C:\Program Files\Google\Chrome\Application```.

3. Download the latest ```osumer.exe``` from the [releases](https://github.com/mob41/osumer/releases). Don't download a ```.jar``` file. We need a ```.exe``` executable here, in order to replace the original browser's executable.

4. Start the ```osumer.exe```.

5. Enter your osu! account login information, and press ```Save configuration for osumerExpress``` button. Your osu! account login information will be saved in a ```osumer_configuration.json``` file next to the application (```osumer.exe```).

6. Enter the default browser executable's path, with the another file name, which will be used to link the browser that we replaced. (e.g. ```C:\Program Files (x86)\Google\Chrome\Application\chrome_bak.exe```) Don't put the original one, if you don't understand what are you doing, don't continue and sit down and think about it. We are replacing the original one (e.g. ```C:\Program Files (x86)\Google\Chrome\Application\chrome.exe```) with ```osumer.exe```, so we need to specify a different file name, which will be used in step.

7. Tick ```Automatically switch to browser for non-beatmaps```. This must be ticked otherwise non-beatmaps URLs (e.g. ```http://www.google.com```) will not be opened.

8. Tick ```Switch to browser if no "-ui" argument specified```. This must be ticked otherwise the all shortcuts will be broken.

9. Once you setup all the osumer settings (e.g. osu! user account, default browser .exe path, optimizations), place the ```osumer.exe``` (Must, .jar won't work) and ```osumer_configuration.json``` to the directory.

10. Rename the original browser executable to the file name that specified in the osumer configuration. (e.g. ```chrome.exe``` to ```chrome_bak.exe```).

11. Rename ```osumer.exe``` to the the original browser file name (e.g. ```chrome.exe```)

12. That's it! Something might break (e.g. the shortcut icons on the desktop), but fixes are not mentioned here. Please refer to the wiki.



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
 