﻿-- CUENTAS EXENTAS VIGENTES CON SUS VEH�CULOS, CATEGORIA, TIPO DE EXENTO      
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

-- CUENTAS PREPAGO VIGENTES CON SUS VEH�CULOS, CATEGORIA, TIPO DE CUENTA, DESCUENTO TAG
select a.exgroup, a.status, a.creator+a.account+a.luhn as contrato, a.numberdoc as NIT, a.BALANCE as Saldo, a.forename as Nombres, 
a.surname as Apellido1, a.surname2 as Apellido2, a.email as eMail, a.creationtime as FechaCreacion, 
a.startdate as FechaInicio, b.plate, make, model, colour, pan, c.description, d.name, e.name,b.CLASS
from aaccount a, amember b, aclass c, atemplate d, astrattime e
where a.creator+a.account = b.creator+b.account
	and b.class = c.class
	and a.template = d.template
	and a.discstrat = e.strattime 
	and a.EXGROUP is null
	and a.status <> 'C' order by contrato


-- TODAS LAS CUENTAS EXENTAS VIGENTES
select exgroup, status, creator+account+luhn as contrato, numberdoc as NIT, 
forename as Nombres, surname as Apellido1, surname2 as Apellido2,
email as eMail, creationtime as FechaCreacion, startdate as FechaInicio
from AACCOUNT 
where status <> 'C' and exgroup in (select [option] from AOPTIONCHOICE)
order by contrato

-- TODAS CUENTAS VIGENTES PREPAGO
select exgroup, status, creator+account+luhn as contrato, numberdoc as NIT, balance as Saldo,
forename as Nombres, surname as Apellido1, surname2 as Apellido2,
email as eMail, creationtime as FechaCreacion, startdate as FechaInicio,*
from AACCOUNT 
where status <> 'C' and exgroup IS NULL
order by contrato