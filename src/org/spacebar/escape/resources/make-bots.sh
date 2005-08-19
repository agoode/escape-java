#!/bin/sh

montage -verbose -geometry +0+0 -tile +5 brokenbot8x8.png dalek8x8.png hugbot8x8.png sleepdalek8x8.png sleephugbot8x8.png /tmp/bots8x8.png

# bots
gimp -i -b '(convert-to-indexed-png "/tmp/bots8x8.png" "/tmp/botsi.png")' '(gimp-quit 0)'

~/pngcrush-1.5.10/pngcrush /tmp/botsi.png ../midp1/resources/b.png



# player
gimp -i -b '(convert-to-indexed-png "player8x8.png" "/tmp/playeri.png")' '(gimp-quit 0)'

~/pngcrush-1.5.10/pngcrush /tmp/playeri.png ../midp1/resources/p.png



ls -l /tmp/bots8x8.png ../midp1/resources/b.png
ls -l player8x8.png ../midp1/resources/p.png


