#! /usr/bin/sh
#echo !/bin/bash
#echo "$(dirname "$0")"
cd "$(dirname "$0")"
export GJARSAN2_HOME=.
export GDK_SCALE=2
export DISPLAY=:0

#echo GJARSAN2_HOME=$GJARSAN2_HOME
java -version
# exit 0
# -Dsun.java2d.uiScale=2.5
# FXSCALE="-Dprism.allowhidpi=true -Dsun.java2d.uiScale.enabled=true -Dsun.java2d.uiScale=2.0"
echo java -cp $GJARSAN2_HOME/gjarscan2.jar:$GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar gjarscan2.Main $*
DISPLAY=:0 java -cp  $GJARSAN2_HOME/gjarscan2.jar:$GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar gjarscan2.Main $*
# java -cp $GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar -jar $GJARSAN2_HOME/gjarscan2.jar




