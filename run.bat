
@echo off
Title Migracion - Vas
cls

:top
echo ########################################################
echo .
echo . SOFTWARE DE MIGRACION PARA VAS
echo . (C) FTS TECNOLOGIA 2020
echo .
echo ########################################################
echo .
echo Ingrese clave de acceso :
echo.
echo ----------------------------------------------



set /p pass=
if %pass%==ftsuser420 goto correct
rem            
goto penalty



:penalty
echo  Por favor ingrese la contrase�a asignada de FTSTECNOLOGIA.
pause
cls
goto top
exit




:correct
cls
echo   Consola : FTS TECNOLOGIA.
goto Start



@echo off

Title Password Page
:question
set /a tries=2
:top
echo ########################################################
echo .
echo . SOFTWARE DE MIGRACION PARA VAS
echo . (C) FTS TECNOLOGIA 2020
echo .
echo ########################################################
echo .
echo Ingrese clave de acceso :
echo.
echo ----------------------------------------------

set /p pass=
if %pass%==ftsuser420 goto correct
rem            !!!!!!!!!!!!!!!!!!!!4123 can be substituted with anything, cause it's your password!!!!!!!!!!!!!!!!!

if %tries%==0 goto penalty

cls
goto top

:penalty
echo Sorry, too many incorrect passwords, initiating shutdown.
::start shutdown -s -f -t 300 -c "Should have asked for permission...SHUTDOWN INITIATED"
pause
exit

:Start
Title Menu of Awesomeness   \,,/(-.-)\,,/
cls
echo                                   %TIME%
echo  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
echo  -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo           ! Listado de opciones disponibles:  !
echo  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
echo  -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo  Escriba el n�mero de la opci�n que desea ejecutar, seguido de la tecla [ENTER]
echo  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
echo  -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo              [ALT] + [ENTER] Activar / Desactivar Modo Pantalla Completa
echo  -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
echo.
echo                         1 - Creacion y Configuracion de db -
echo                         2 - Ejecutar ETL de migracion
echo                         3 - Ejecutar Cliente de migracion - VAS


set Choice=
set /p Choice=""

if '%Choice%'=='1' goto database_creation_configuration
if '%Choice%'=='2' goto execute_etl_migration
if '%Choice%'=='3' goto execute_cliente_vas


cls
echo '%Choice%' is not valid
ping localhost -n 5 >Nul
echo Try again
ping localhost -n 5 >nul
cls
goto Start

:database_creation_configuration
cls
mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE easyroad_temp;"
mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE easyroad_db;"
mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE as2;"
mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE dw;"
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_temp.xml
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_vas.xml
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_as2.xml
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_dw.xml
pause
echo "Creacion y configuracion de base de datos finalizado correctamente"
pause
goto Start

:execute_etl_migration
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\ETL_PROCESO_PREPAGO.xml
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\ETL_PROCESO_EXENTOS.xml

pause
echo "Ejecucion de migracion TOLLHOST VAS a EASYROAD TEMP Finalizado correctamente"
pause
goto Start

:execute_cliente_vas
java -jar bin\spring_vas_program\target\MigracionVas-0.0.1-SNAPSHOT.jar

pause
goto Start


:exit
msg * See You Soon!
exit


:exit2
exit



:correct
cls
echo                                        Bienvenido FTS TECNOLOGIA 
ping localhost -n 3 >nul
goto Start



:echoon
@echo on
goto Start
