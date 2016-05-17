
@ECHO off

:init
SET workdir=%CD%
CD ..
SET ISS_HOME=%CD%
CD /d %workdir%

SET "VMOPTION=-server -Xms256m -Xmx2048m -Xss256k -XX:+AggressiveOpts -XX:+UseParallelGC -XX:+UseBiasedLocking -XX:NewSize=64m -XX:PermSize=256M"

FOR /f %%1 IN ('dir /b ..\lib') DO (
	CALL :addJar %%1
)

GOTO :checkJar

:main
IF [%1] == [start] (
	IF [%2] == [] GOTO :usage
	ECHO [INFO] �յ�ָ��,�����ڵ�:%2
	GOTO :START %2
)

IF [%1] == [stop]  (
	IF [%2] == [] GOTO :usage
	ECHO [INFO] �յ�ָ��,ֹͣ�ڵ�:%2
	GOTO :stop %2
)

IF [%1] == [list]  (
	ECHO [INFO] �յ�ָ��,��ʾ���нڵ�!
	GOTO :list
)

:usage
echo "useage: iss [start|stop] [nodename]	-- start/stop node" 
echo "             list         	  	-- list all node status"
GOTO :EOF

:START
ECHO [INFO] ���������ڵ�:%2

CALL %IPHARMACARE_JRE_HOME%/bin/java -Diss.config=%2.xml -Diss.path=%ISS_HOME% %VMOPTION% -jar %ISS_HOME%/lib/%jar%
ECHO [INFO] �����ڵ����!
GOTO :EOF

:stop
SET node=%1
ECHO [INFO] �ù�����ʱ��δʵ��
GOTO :EOF

:list
SET node=%1
ECHO [INFO] �ù�����ʱ��δʵ��
GOTO :EOF


:addJar
echo %1 |findstr "iss.core">NUL 2>nul
IF %ERRORLEVEL% == 1 GOTO :EOF 

IF NOT DEFINED "%jar%" (
	SET jar=%1 &	GOTO :EOF
) || SET jar=%jar% %1
GOTO :EOF

:checkJar
IF %jar% == '' (ECHO [INFO] ��ʼ��ʧ��:����jar��ɨ����� & GOTO :EOF)
ECHO [INFO] ������ʼ�����....
GOTO :main

:error
ECHO [INFO] %1 %2 %3 %4
GOTO :EOF

:EOF
return
