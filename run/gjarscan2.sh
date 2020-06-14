#!/bin/bash
export GJARSAN2_HOME=.
#echo GJARSAN2_HOME=$GJARSAN2_HOME
java -cp $GJARSAN2_HOME/gjarscan2.jar:$GJARSAN2_HOME/jarscan21.jar:$GJARSAN2_HOME/jarscan.jar gjarscan2.Main $*



