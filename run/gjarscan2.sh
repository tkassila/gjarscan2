#!/bin/bash
#echo "$(dirname "$0")"
cd "$(dirname "$0")"
export GJARSAN2_HOME=.
export GDK_SCALE=2
#echo GJARSAN2_HOME=$GJARSAN2_HOME
# java -version
# -Dsun.java2d.uiScale=2.5
FXSCALE="-Dprism.allowhidpi=true -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0"
echo java $FXSCALE -cp $GJARSAN2_HOME/gjarscan2.jar:$GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar gjarscan2.Main $*
java -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.win.uiScaleX=2.5 -Dsun.java2d.win.uiScaleY=2.5 -cp  $GJARSAN2_HOME/gjarscan2.jar:$GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar gjarscan2.Main $*
# java -cp $GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar -jar $GJARSAN2_HOME/gjarscan2.jar



