::start "C:\Windows\system32\notepad.exe"
::mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE easyroad_temp;"
::mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE easyroad_vas;"
::mysql -hlocalhost -uroot -pR00tmysql -e "CREATE DATABASE as2;"
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_temp.xml
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_vas.xml
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\database_creation_as2.xml
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\ETL_CUENTAS_CLIENTES.xml
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\\ETL_SALDOS_CUENTAS.xml  
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\ETL_VEHICULOS.xml  
::java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_vas\ETL_TAGS.xml
echo "PRUEBAS UNITARIAS DE MIGRACION - VAS"
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_test\UTlistado_clientes_cuentas_tags_vehiculos.xml
java -jar bin\scriptella\scriptella.jar bin\scriptella\bin_test\UTlistado_clientes_cuentas.xml
pause