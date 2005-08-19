#!/bin/sh

montage -verbose -geometry +0+0 -tile +5 brokenbot8x8.png dalek8x8.png hugbot8x8.png sleepdalek8x8.png sleephugbot8x8.png bots8x8.png

gimp -i -b '(convert-to-indexed-png "bots8x8.png" "botsi.png")' '(gimp-quit 0)'

~/pngcrush-1.5.10/pngcrush botsi.png ../midp1/resources/botsi.png

ls -l bots8x8.png ../midp1/resources/botsi.png
