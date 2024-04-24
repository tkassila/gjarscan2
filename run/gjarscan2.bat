call \java\jdk8fxcp.bat
java -version
set M2_HOME=C:\Users\tkassila\.m2\repository
set GJARSAN2_HOME=.
java -cp %GJARSAN2_HOME%\gjarscan2.jar;%GJARSAN2_HOME%\jarscan21.jar;%GJARSAN2_HOME%\jarscan.jar; gjarscan2.Main %*
