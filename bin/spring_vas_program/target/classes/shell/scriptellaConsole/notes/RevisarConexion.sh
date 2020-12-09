#!/bin/bash
ID_CARRIL=$2
ID_PEAJE=$3
PAROK=1
USERBD="ftsuserremote"
PASSBD="ftsuserremote1234"
USER_SERV="root"
PASS_SERV="R00tmysql"


if [ -z $1 ]; then
echo "falta el primer parametro Direccion IP"
PAROK=1

else
PAROK=2
DIRIP=$1

fi


if [ -z $ID_CARRIL  ]; then
echo "falta el segundo parametro Id Carril"
PAROK=1
else
PAROK=2
fi


if [ -z $ID_PEAJE ] ; then
echo "falta el tercer parametro Id Peaje"
PAROK=1
else
PAROK=2
fi

if [ $PAROK = 2 ];then


echo "************************************"
echo "**************Parametros************"
echo "************************************"
echo "**DIRECCION IP:"$DIRIP"********"
echo "**ID CARRIL:"$ID_CARRIL"***********************"
echo "**ID PEAJE:"$ID_PEAJE"************************"


mysql easyroad -u$USERBD -p$PASSBD --connect-timeout=30 -h$DIRIP -e "select count(numeroTurno) as conexion from TURNOS LIMIT 1" &> testcon.txt

CONEXION=$(head -1 testcon.txt | tail -1)
DATO=$(head -2 testcon.txt | tail -1)

  if [ $CONEXION = "conexion" ];then

        if [ $DATO -ne 0 ];then

                echo CONEXION OK
                mysql easyroad_db -u$USER_SERV -p$PASS_SERV  --connect-timeout=2 -e "UPDATE sincronizaciones SET Conexion=1 where carril_codigo=$ID_CARRIL and peaje_codigo=$ID_PEAJE and Conexion=0"
        else
        echo hay conexion pero el dato esta mal
        fi

else
echo "error de conexion"
 mysql easyroad_db -u$USER_SERV -p$PASS_SERV  --connect-timeout=2 -e "UPDATE sincronizaciones SET Conexion=0 where carril_codigo=$ID_CARRIL and peaje_codigo=$ID_PEAJE and Conexion=1"

fi



else

echo "los parametros estan mal configurados"
fi

