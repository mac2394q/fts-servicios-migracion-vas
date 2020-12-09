
-- TODAS CUENTAS VIGENTES PREPAGO
select exgroup, status, creator+account+luhn as contrato, numberdoc as NIT, 
forename as Nombres, surname as Apellido1, surname2 as Apellido2,
email as eMail, creationtime as FechaCreacion, startdate as FechaInicio,*
from AACCOUNT 
where status <> 'C' and exgroup IS NULL
order by contrato

-- CUENTAS PREPAGO CON SUS VEHÍCULOS, CATEGORIA, TIPO DE CUENTA, DESCUENTO TAG
select a.exgroup, a.status, a.creator+a.account+a.luhn as contrato, a.BALANCE/100 as Saldo, a.numberdoc as NIT, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, b.plate, make, model, colour, pan, c.description, d.name, e.name, b.pan as TAG
from aaccount a, amember b, aclass c, atemplate d, astrattime e
where a.creator+a.account = b.creator+b.account
	and b.class = c.class
	and a.template = d.template
	and a.discstrat = e.strattime 
	and a.EXGROUP is null
	and a.status <> 'C' 
	order by contrato 

-- TODAS LAS CUENTAS EXENTAS
select exgroup, status, creator+account+luhn as contrato, numberdoc as NIT, 
forename as Nombres, surname as Apellido1, surname2 as Apellido2,
email as eMail, creationtime as FechaCreacion, startdate as FechaInicio, DESIGNATION 
from AACCOUNT 
where status <> 'C' and exgroup in (select [option] from AOPTIONCHOICE)
order by contrato


-- CUENTAS EXENTAS CON SUS VEHÍCULOS, CATEGORIA, TIPO DE EXENTO      
select a.exgroup, a.status, a.creator+a.account+a.luhn as contrato, a.numberdoc as NIT, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, b.plate, make, model, colour, pan, c.description, d.name
from aaccount a, amember b, aclass c, atemplate d
where a.creator+a.account = b.creator+b.account
	and b.class = c.class
	and a.template = d.template
	and a.status <> 'C' 
	and a.EXGROUP is not null
	order by contrato

-- CUENTAS PREPAGO QUE NO TIENEN CONFIGURADO UN TIPO DE DESCUENTO "00001"   TENGAN O NO VEHICULO ASIGNADO
select a.creator+a.account+a.luhn as contrato, a.numberdoc as NIT, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, fax
from aaccount a
where a.EXGROUP is null
	and a.DISCSTRAT is null
	and a.status <> 'C' 
	ORDER BY CONTRATO

-- CUENTAS PREPAGO QUE NO TIENEN ASIGNADO DESCUENTO DE TAG "00001, TIENEN VEHICULO ASIGNADO
select a.creator+a.account+a.luhn as contrato, a.numberdoc as NIT, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, b.plate, make, model, colour, pan
from aaccount a, amember b, aclass c
where a.creator+a.account = b.creator+b.account
	and b.class = c.class
	and a.EXGROUP is null
	and a.DISCSTRAT is null
	and a.status <> 'C' order by CONTRATO


-- CUENTAS CON VEHICULOS QUE NO TIENEN TAG ASIGNADO
select a.exgroup, a.status, a.creator+a.account+a.luhn as contrato, a.numberdoc as NIT, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, b.plate, make, model, colour, pan, c.description, d.name, e.name
from aaccount a, amember b, aclass c, atemplate d, astrattime e
where a.creator+a.account = b.creator+b.account
	and b.class = c.class
	and a.template = d.template
	and a.discstrat = e.strattime and a.status <> 'C' and b.pan is null

-- CUENTAS PREPAGO SIN VEHICULOS
select creator+account+LUHN as contrato, forename, SURNAME, CREATIONTIME, STARTDATE, fax 
from aaccount a
where a.EXGROUP is null 
	and a.status <> 'C'
	and a.CREATOR+a.ACCOUNT not in
(select creator+account from amember) 
order  by STARTDATE desc 

-- RECARGAS EN PUNTO DE VENTA,  Filtrado por Fecha o Usuario
select convert(datetime,substring(time,1,8),103), d.name as PV, g.creator+g.account+g.luhn as CUENTACLIENTE, amount as MONTORECARGA, balance as SALDOCUENTA, PAYMENTTYPE, amountsign, PAYMENTMEANS, a.description, c.posid, collector, b.FORENAME, b.SURNAME, e.name, f.COMMENT as Factura, description, guatefactreqid, amount, vatpercent, vatamount,*
from AMOVEMENT a, aoperator b, APOSMOVEMENT c, apointofsale d, ATOLLCOMPANY e, AEDOCGUATEFACTURAS f, aaccount g
where a.TOLLCOMPANY = b.TOLLCOMPANY and a.COLLECTOR = b.operator
	and c.movement = a.movement
	and d.POSID = c.POSID 
	and a.TOLLCOMPANY = e.TOLLCOMPANY 
	and a.GUATEFACTREQID = f.ID 
	and a.creator+a.account = g.creator+g.account
	and paymentmeans = 'CA' and amountsign = '+' and paymenttype = 'L'
	AND convert(datetime,SUBSTRING(a.time,1,8),103) = '2019-06-11'
	AND a.COLLECTOR = '00105'
--and g.creator+g.account+g.luhn = '000142018'
ORDER BY a.TIME DESC

-- RECARGAS DESDE BI, filtrado por Fecha
select PAYMENTTYPE, amountsign, PAYMENTMEANS, a.description, account, movement, collector, time, e.name, f.COMMENT as Factura, description, guatefactreqid, amount, vatpercent, vatamount, a.*  
from AMOVEMENT a, ATOLLCOMPANY e, AEDOCGUATEFACTURAS f
where a.TOLLCOMPANY = e.TOLLCOMPANY 
	and a.GUATEFACTREQID = f.ID 
	and paymentmeans = 'BI' 
	and amountsign = '+' and paymenttype = 'L'
	AND convert(datetime,SUBSTRING(a.time,1,8),103) = '2019-06-11'
ORDER BY a.TIME DESC

-- LISTA BLANCA DE TAG
select * from APASSTOKEN 
where PAN = 'E20034120139010000F12CA3' 

-- VEHICULO AL CUAL ESTA ASOCIADO EL TAG
select * from ATOKENHISTORY 
where panfrom = 'E20034120139010000F12CA3'

-- TRANSACCIONES CON EL TAG
select TAGPAN, substring(trtimestamp,1,4)+'/'+substring(trtimestamp,5,2)+'/'+substring(trtimestamp,7,2) AS FECHA, * 
from ATRANSACTION
where TAGPAN = 'E20034120139010000F12CA3' 
order by fecha desc

-- CONSULTA PARA VISUALIZAR UBICACIÓN DE EXPEDIENTES DE DOCUMENTOS ESCANEADOS EN PV
select * from AACCOUNTDOCUMENT
where path like '%C:\%'
select * from AACCOUNTDOCUMENT 
where description = 'VINCULACION 15-05-2017 PUNTO SMP'



-- OTROS
select * from AACCOUNT where creator+account = '00000568'
PAYMENTTYPE A= Ajuste
			L= Recarga
PAYMENTMEANS CA = Carga
			 BI = Recarga desde BI
			 NO = Ajustes
GUATEFACTREQID
		Este campo contiene la referencia para Guatefacturas.




