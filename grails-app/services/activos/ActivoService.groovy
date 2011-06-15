package activos

import auditoria.activos.XActivo
import general.*
import contabilidad.*
import java.math.*
import java.text.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import groovy.sql.*

class ActivoService {

    static transactional = true

    def springSecurityService
    def folioActivoService
    def tipoActivoService
    def cuentaService
    def sessionFactory
    def dataSource

    List<Activo> lista(def params) {
        log.debug "Lista de activos $params"
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def activos = []
        def fechas = fechasDeBusqueda(params)
        def fechaInicial = fechas.fechaInicial
        def fechaFinal = fechas.fechaFinal
        def query = Activo.buscaPorEmpresa(usuario.almacen.empresa.id)
        if (params?.filtro) {
            query = query.buscaPorCodigo(params.filtro)
        }
        if (fechaInicial && fechaFinal) {
            query = query.buscaPorFecha(fechaInicial, fechaFinal)
        }
        if (params?.centroCosto) {
            query = query.buscaPorCentroCosto(params.centroCosto)
        }
        if (params?.proveedor) {
            query = query.buscaPorProveedor(params.proveedor)
        }
        if (params?.responsable) {
            query = Activo.buscaPorResponsable(params.responsable)
        }
        if (params?.tipoActivo) {
            query = query.buscaPorTipoActivo(params.tipoActivo)
        }
        if (params?.descripcion) {
            query = query.buscaPorDescripcion(params.descripcion)
        }
        if (params?.baja) {
            query = query.buscaPorBaja()
        }
        if (params?.reubicacion) {
            query = query.buscaPorReubicado()
        }
        if (params?.conTipoActivo) {
            query = query.conTipoActivo()
        } 
        if (params?.activo) {
            query = query.activo()
        }

        return query.list(params)
    }
	
    def listaConCantidad(def params) {
        log.debug "Lista de activos con cantidad $params"
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def activos = []
        def cantidad = 0
        def fechas = fechasDeBusqueda(params)
        def fechaInicial = fechas.fechaInicial
        def fechaFinal = fechas.fechaFinal
        def query = Activo.buscaPorEmpresa(usuario.almacen.empresa.id)
        if (params?.filtro) {
            query = query.buscaPorCodigo(params.filtro)
        }
        if (fechaInicial && fechaFinal) {
            query = query.buscaPorFecha(fechaInicial, fechaFinal)
        }
        if (params?.centroCosto) {
            query = query.buscaPorCentroCosto(params.centroCosto)
        }
        if (params?.proveedor) {
            query = query.buscaPorProveedor(params.proveedor)
        }
        if (params?.responsable) {
            query = query.buscaPorResponsable(params.responsable)
        }
        if (params?.tipoActivo) {
            query = query.buscaPorTipoActivo(params.tipoActivo)
        }
        if (params?.descripcion) {
            query = query.buscaPorDescripcion(params.descripcion)
        }
        if (params?.baja) {
            query = query.buscaPorBaja()
        }
        if (params?.reubicacion) {
            query = query.buscaPorReubicado()
        }
        def query2 = query.suma().list()
        log.debug "Resultado: $query2"
        def anual = query2[0][0]
        def mensual = query2[0][1]
        def acumulada = query2[0][2]
        def costoTotal = query2[0][3]

        return [lista:query.list(params), cantidad:query.count(), acumulada: acumulada, mensual: mensual, anual: anual, costoTotal: costoTotal]
    }

    def buscaPorResponsable(filtro) {
        filtro = "%${filtro.toUpperCase()}%"
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def resultados1 = Activo.executeQuery("select new map(ubicacion as nombre) from Activo where empresa = :empresa and ubicacion like upper(:filtro) group by ubicacion order by ubicacion",[empresa:usuario.almacen.empresa, filtro: filtro,max:5])
        def resultados2 = Activo.executeQuery("select new map(responsable as nombre) from Activo where empresa = :empresa and responsable like upper(:filtro) group by responsable order by responsable",[empresa:usuario.almacen.empresa, filtro: filtro,max:5]) 

        resultados1.addAll(resultados2)
        def resultados = [:] as TreeMap
        for (resultado in resultados1) {
            resultados[resultado.nombre] = resultado
        }

        return resultados.values()
    }
	
    Activo obtiene(String id) {
        return Activo.get(id)
    }
	
    Activo crea(Activo activo) {
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        activo.empresa = usuario.almacen.empresa
        
        activo = asignaciones(activo,true, new Date())
        activo.save()

        audita(activo,Constantes.CREAR)

        return activo
    }
	
    Activo creaConEmpresa(Activo activo) {
        activo = asignaciones(activo,true, new Date())
        activo.save()

        audita(activo,Constantes.CREAR)

        return activo
    }

    Activo depreciar(Activo activo, Date fecha) {
        if (activo) {
            log.info "Depreciando al activo $activo"
            activo = asignaciones(activo, false, fecha)
            actualiza(activo)
        } else {
            def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
            log.info "Depreciando todos los activos de la empresa $usuario.almacen.empresa"
            def activos = Activo.buscaPorEmpresa(usuario.almacen.empresa.id).list()
            def cont = 0
            def total = (activos)?activos.size():0
            def fechaSql = new java.sql.Date(fecha.time)
            def sql = Sql.newInstance(dataSource)
            def inicio = new Date()
            log.debug "Depreciando $cont / $total"
            sql.withTransaction {
                def conn = sql.getConnection()
                def ps = conn.prepareStatement('update activos set depreciacion_fecha = ?, depreciacion_anual = ?, depreciacion_mensual = ?, depreciacion_acumulada = ?, version = ? where id = ?')
                for(x in activos) {
                    def date = new java.sql.Timestamp((new Date()).time)
                    if (++cont % 1000 == 0) {
                        log.debug "Depreciando $cont / $total"
                    }
                    if (cont % 100 == 0 && cont > 0) {
                        ps.executeBatch()
                    }
                    asignaciones(x, false, fecha)
                    //actualiza(x)
                    // Modificacion para utilizar jdbc
                    x.discard()
                    ps.setDate 1, fechaSql
                    ps.setBigDecimal 2, x.depreciacionAnual
                    ps.setBigDecimal 3, x.depreciacionMensual
                    ps.setBigDecimal 4, x.depreciacionAcumulada
                    ps.setLong 5, x.version+1
                    ps.setLong 6, x.id
                    ps.addBatch()
                }
                ps.executeBatch()
                ps.close()
                sessionFactory.currentSession.flush()
            }
            sql.close()
            log.debug "Depreciando $cont / $total"
            log.debug "Termin\u00F3 en ${(new Date().time - inicio.time)/1000}"
            log.debug "Termino de depreciar"
        }
        return activo
    }
    
    Activo asignaciones(Activo activo, boolean conFolio, Date fecha) {
        if (conFolio) {
            activo = asignaFolio(activo)
        }
        activo = asignaDepreciacionAnual(activo)
        activo = asignaDepreciacionMensual(activo, fecha)
        activo = asignaDepreciacionAcumulada(activo, fecha)
        activo = asignaValorNeto(activo)
        activo.depreciacionFecha = new Date()
        return activo
    }

    Activo asignaFolio(activo) {
        def folio = folioActivoService.activo(activo.empresa.organizacion)
        activo.folio = "A-${activo.empresa.organizacion.codigo}${folioActivoService.numberFormat().format(++folio.valor)}"
        folio.save(validate:false)
        return activo
    }

    Activo asignaDepreciacionAnual(activo) {
        def porciento = activo.tipoActivo.porciento
        def depreciacionAnual = activo.moi * porciento
        activo.depreciacionAnual = new BigDecimal(depreciacionAnual)
        return activo
    }

    Activo asignaDepreciacionMensual(Activo activo, Date fecha) {
        activo.depreciacionMensual = obtieneDepreciacionMensual(activo, fecha)
        return activo
    }

    BigDecimal obtieneDepreciacionMensual(Activo activo, Date fecha) {
        def depreciacionMensual = new BigDecimal('0')
        def inicio = new GregorianCalendar()
        inicio.time = activo.fechaCompra
        def fin = new GregorianCalendar()
        fin.time = fecha
        if (inicio <= fin && days360(inicio, fin) / 30 < activo.tipoActivo.vidaUtil) {
            depreciacionMensual = new BigDecimal(activo.depreciacionAnual / 12)
        }
        return depreciacionMensual
    }

    Activo asignaDepreciacionAcumulada(activo, fecha) {
        activo.depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fecha, activo.depreciacionMensual)
        return activo
    }

    BigDecimal obtieneDepreciacionAcumulada(activo, fechaFin, depreciacionMensual) {
        if (!activo.inactivo) {
            def depreciacionAcumulada = new BigDecimal(activo.moi)
            def inicio = new GregorianCalendar()
            inicio.time = activo.fechaCompra
            def fin = new GregorianCalendar()
            fin.time = fechaFin
            if (inicio <= fin) {
                def meses = days360(inicio, fin) / 30
                if (meses < activo.tipoActivo.vidaUtil) {
                    depreciacionAcumulada = depreciacionMensual.multiply(new BigDecimal(meses))
                }
            } else {
                depreciacionAcumulada = new BigDecimal('0')
            }
            return depreciacionAcumulada
        } else {
            return new BigDecimal('0')
        }
    }

    BigDecimal obtieneDepreciacionAcumulada2(activo, fechaFin, depreciacionMensual) {
        def depreciacionAcumulada = new BigDecimal(activo.moi)
        def inicio = new GregorianCalendar()
        inicio.time = activo.fechaCompra
        def fin = new GregorianCalendar()
        fin.time = fechaFin
        if (inicio <= fin) {
            def meses = days360(inicio, fin) / 30
            if (meses < activo.tipoActivo.vidaUtil) {
                depreciacionAcumulada = depreciacionMensual.multiply(new BigDecimal(meses))
            }
        } else {
            depreciacionAcumulada = new BigDecimal('0')
        }
        return depreciacionAcumulada
    }

    Activo asignaValorNeto(activo) {
        activo.valorNeto = activo.moi.subtract(activo.depreciacionAcumulada)
        return activo
    }
	
    Activo actualiza(Activo activo) {
        activo.save()
        audita(activo,Constantes.ACTUALIZAR)
        return activo
    }
	
    String elimina(String id) {
        def activo = Activo.get(id)
        String nombre = activo.folio
        activo.delete()
        audita(activo,Constantes.ELIMINAR)
        return nombre
    }

    Activo baja(baja) {
        def activo = baja.activo
        activo.refresh()
        activo.inactivo = true
        //activo.fechaInactivo = new Date()
        activo.fechaInactivo = baja.fechaBaja
        baja.creador = springSecurityService.authentication.name
        baja.save()
        audita(activo, Constantes.BAJA)
        return activo
    }

    Activo reubicacion(reubicacion) {
        def activo = reubicacion.activo
        activo.refresh()
        activo.centroCosto = reubicacion.centroCosto
        reubicacion.creador = springSecurityService.authentication.name
        reubicacion.save()
        audita(activo, Constantes.REUBICACION)
        return activo
    }

    void audita(Activo activo, String actividad) {
        log.debug "[AUDITA] $actividad activo $activo"
        def creador = springSecurityService.authentication.name
        def xactivo = new XActivo(activo.properties)
        xactivo.activoId = activo.id
        xactivo.empresaId = activo.empresa.id
        xactivo.creador = creador
        xactivo.actividad = actividad
        xactivo.save(validate:false)
    }

    /**
	 * Calcula el número de días entre dos fechas basándose en un año de 360 días
	 * (doce meses de 30 días) que se utiliza en algunos cálculos contables. 
	 * Esta función facilita el cálculo de pagos si su
	 * sistema de contabilidad se basa en 12 meses de 30 días.
	 * @param dateBegin	The purchase date
	 * @param dateEnd	The depreciation date
	 */
	public Long days360(Calendar dateBegin, Calendar dateEnd) {
		long dference = 0;
		long yearsBetwen;
		long daysInMonths;
		long daysLastMonth;
		long serialBegin;
		long serialEnd;
		yearsBetwen = dateBegin.get(Calendar.YEAR) - 1900;
		daysInMonths = (dateBegin.get(Calendar.MONTH)+1) * 30;
		daysLastMonth = dateBegin.get(Calendar.DAY_OF_MONTH);
		if(daysLastMonth == 31) {
			daysLastMonth = 30;
        }
		
		serialBegin = yearsBetwen * 360 + daysInMonths + daysLastMonth;
		
		yearsBetwen = dateEnd.get(Calendar.YEAR) - 1900;
		daysInMonths = (dateEnd.get(Calendar.MONTH)+1) * 30;
		daysLastMonth = dateEnd.get(Calendar.DAY_OF_MONTH);
		if(daysLastMonth  == 31) {
				if(dateBegin.get(Calendar.DAY_OF_MONTH) < 30) {
					daysInMonths += 30;
					daysLastMonth = 1;
				} else {
					daysLastMonth = 30;
                }
        }
		
		serialEnd = yearsBetwen * 360 + daysInMonths + daysLastMonth;
		
		dference = serialEnd - serialBegin;
		return dference;
	}

    def listaEnHojaDeCalculo = { params ->
        try {
            params.remove 'max'
            params.remove 'offset'
            def activos = lista(params)
            log.debug "Creando excel con lista de activos"
            def wb = new HSSFWorkbook()

            def cs = wb.createCellStyle()
            cs.setDataFormat(wb.createDataFormat().getFormat('$#,##0.00'))
            def cs2 = wb.createCellStyle()
            cs2.setDataFormat(wb.createDataFormat().getFormat('dd/MM/yyyy'))
            def cs3 = wb.createCellStyle()
            cs3.setDataFormat(wb.createDataFormat().getFormat('0%'))

            def hoja1 = wb.createSheet("entradas")
            def fila = hoja1.createRow((short)0)
            fila.createCell((short) 0).setCellValue(new HSSFRichTextString("ID"));
            fila.createCell((short) 1).setCellValue(new HSSFRichTextString("FOLIO"));
            fila.createCell((short) 2).setCellValue(new HSSFRichTextString("F. REGISTRO"));
            fila.createCell((short) 3).setCellValue(new HSSFRichTextString("F. COMPRA"));
            fila.createCell((short) 4).setCellValue(new HSSFRichTextString("FACTURA"));
            fila.createCell((short) 5).setCellValue(new HSSFRichTextString("PROCEDENCIA"));
            fila.createCell((short) 6).setCellValue(new HSSFRichTextString("PEDIMENTO"));
            fila.createCell((short) 7).setCellValue(new HSSFRichTextString("MONEDA"));
            fila.createCell((short) 8).setCellValue(new HSSFRichTextString("TDC"));
            fila.createCell((short) 9).setCellValue(new HSSFRichTextString('CONDICION'));
            fila.createCell((short) 10).setCellValue(new HSSFRichTextString("POLIZA"));
            fila.createCell((short) 11).setCellValue(new HSSFRichTextString("CODIGO"));
            fila.createCell((short) 12).setCellValue(new HSSFRichTextString("DESCRIPCION"));
            fila.createCell((short) 13).setCellValue(new HSSFRichTextString("MARCA"));
            fila.createCell((short) 14).setCellValue(new HSSFRichTextString("MODELO"));
            fila.createCell((short) 15).setCellValue(new HSSFRichTextString("SERIAL"));
            fila.createCell((short) 16).setCellValue(new HSSFRichTextString("PRECIO"));
            fila.createCell((short) 17).setCellValue(new HSSFRichTextString("DEP. ANUAL"));
            fila.createCell((short) 18).setCellValue(new HSSFRichTextString("DEP. MENSUAL"));
            fila.createCell((short) 19).setCellValue(new HSSFRichTextString("DEP. ACUMULADA"));
            fila.createCell((short) 20).setCellValue(new HSSFRichTextString("VALOR NETO"));
            fila.createCell((short) 21).setCellValue(new HSSFRichTextString("RESCATE"));
            fila.createCell((short) 22).setCellValue(new HSSFRichTextString("INPC"));
            fila.createCell((short) 23).setCellValue(new HSSFRichTextString("UBICACION"));
            fila.createCell((short) 24).setCellValue(new HSSFRichTextString("INACTIVO"));
            fila.createCell((short) 25).setCellValue(new HSSFRichTextString("F. INACTIVO"));
            fila.createCell((short) 26).setCellValue(new HSSFRichTextString("GRUPO"));
            fila.createCell((short) 27).setCellValue(new HSSFRichTextString("PROVEEDOR"));
            fila.createCell((short) 28).setCellValue(new HSSFRichTextString("EMPRESA"));
            fila.createCell((short) 29).setCellValue(new HSSFRichTextString("RESPONSABLE"));
            fila.createCell((short) 30).setCellValue(new HSSFRichTextString("MOTIVO"));
            fila.createCell((short) 31).setCellValue(new HSSFRichTextString("GARANTIA"));
            fila.createCell((short) 32).setCellValue(new HSSFRichTextString("SEGURO"));

            def row = 1
            for(activo in activos) {
                fila = hoja1.createRow(row++);
                fila.createCell((short) 0).setCellValue(activo.id);
                fila.createCell((short) 1).setCellValue(new HSSFRichTextString(activo.folio));
                def celda = fila.createCell((short) 2)
                celda.setCellValue(activo.dateCreated)
                celda.setCellStyle(cs2)
                celda = fila.createCell((short) 3)
                celda.setCellValue(activo.fechaCompra)
                celda.setCellStyle(cs2)
                fila.createCell((short) 4).setCellValue(new HSSFRichTextString(activo.factura));
                fila.createCell((short) 5).setCellValue(new HSSFRichTextString(activo.procedencia));
                fila.createCell((short) 6).setCellValue(new HSSFRichTextString(activo.pedimento));
                fila.createCell((short) 7).setCellValue(new HSSFRichTextString(activo.moneda));
                celda = fila.createCell((short) 8)
                celda.setCellValue(activo.tipoCambio)
                celda.setCellStyle(cs)
                fila.createCell((short) 9).setCellValue(new HSSFRichTextString(activo.condicion));
                fila.createCell((short) 10).setCellValue(new HSSFRichTextString(activo.poliza));
                fila.createCell((short) 11).setCellValue(new HSSFRichTextString(activo.codigo));
                fila.createCell((short) 12).setCellValue(new HSSFRichTextString(activo.descripcion));
                fila.createCell((short) 13).setCellValue(new HSSFRichTextString(activo.marca));
                fila.createCell((short) 14).setCellValue(new HSSFRichTextString(activo.modelo));
                fila.createCell((short) 15).setCellValue(new HSSFRichTextString(activo.serial));
                celda = fila.createCell((short) 16)
                celda.setCellValue(activo.moi)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 17)
                celda.setCellValue(activo.depreciacionAnual)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 18)
                celda.setCellValue(activo.depreciacionMensual)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 19)
                celda.setCellValue(activo.depreciacionAcumulada)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 20)
                celda.setCellValue(activo.valorNeto)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 21)
                celda.setCellValue(activo.valorRescate)
                celda.setCellStyle(cs)
                celda = fila.createCell((short) 22)
                celda.setCellValue(activo.inpc)
                celda.setCellStyle(cs)
                fila.createCell((short) 23).setCellValue(new HSSFRichTextString(activo.ubicacion));
                fila.createCell((short) 24).setCellValue(new HSSFRichTextString(activo.inactivo.toString()));
                celda = fila.createCell((short) 25)
                celda.setCellValue(activo.fechaInactivo)
                celda.setCellStyle(cs2)
                fila.createCell((short) 26).setCellValue(new HSSFRichTextString(activo.tipoActivo.nombre));
                fila.createCell((short) 27).setCellValue(new HSSFRichTextString(activo.proveedor.nombre));
                fila.createCell((short) 28).setCellValue(new HSSFRichTextString(activo.empresa.nombre));
                fila.createCell((short) 29).setCellValue(new HSSFRichTextString(activo.responsable));
                fila.createCell((short) 30).setCellValue(new HSSFRichTextString(activo.motivo));
                fila.createCell((short) 31).setCellValue(activo.mesesGarantia);
                fila.createCell((short) 32).setCellValue(new HSSFRichTextString(activo.seguro.toString()));
            }

            return wb
        } catch(Exception e) {
            throw new RuntimeException("No se pudo crear el excel con estos parametros",e)
        }
    }

    def fechasDeBusqueda(params) {
        def fechaInicial
        def fechaFinal
        if (params?.fechaInicial) {
            fechaInicial = new Date().parse('dd/MM/yyyy',params.fechaInicial)
        }
        if (params?.fechaFinal) {
            fechaFinal = new Date().parse('dd/MM/yyyy',params.fechaFinal)
        }
        def cal = Calendar.instance
        if (fechaInicial && fechaFinal) {
            cal.time = fechaInicial
            cal.set(Calendar.HOUR,0)
            cal.set(Calendar.MINUTE,0)
            cal.set(Calendar.SECOND,0)
            fechaInicial = cal.time
            cal.time = fechaFinal
            cal.set(Calendar.HOUR,23)
            cal.set(Calendar.MINUTE,59)
            cal.set(Calendar.SECOND,59)
            fechaFinal = cal.time
        } else if (fechaInicial) {
            cal.time = fechaInicial
            cal.set(Calendar.HOUR,0)
            cal.set(Calendar.MINUTE,0)
            cal.set(Calendar.SECOND,0)
            fechaInicial = cal.time
            cal.set(Calendar.HOUR,23)
            cal.set(Calendar.MINUTE,59)
            cal.set(Calendar.SECOND,59)
            fechaFinal = cal.time
        } else if (fechaFinal) {
            cal.time = fechaFinal
            cal.set(Calendar.HOUR,0)
            cal.set(Calendar.MINUTE,0)
            cal.set(Calendar.SECOND,0)
            fechaInicial = cal.time
            cal.set(Calendar.HOUR,23)
            cal.set(Calendar.MINUTE,59)
            cal.set(Calendar.SECOND,59)
            fechaFinal = cal.time
        }
        return [fechaInicial: fechaInicial, fechaFinal: fechaFinal]
    }

    def depreciacionAcumuladaPorCentroCosto(params) {
        log.debug "Creando lista de activos agrupada por centros de costo"
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
            log.debug "Por fecha $fechaInicial"
        }
        if (params?.fechaFinal) {
            log.debug "y fecha $fechaFinal"
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def activos = lista(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
        def centrosDeCosto = [:] as TreeMap
        def totales = [total:new BigDecimal('0')]

        def cont = 0
        def max = (activos)?activos.size():0
        def fecha
        def tiposDeActivo = tipoActivoService.lista(null)
        for(tipoActivo in tiposDeActivo) {
            totales[tipoActivo.nombre] = new BigDecimal(0)
        }
        log.debug "Activos 0 / $max"
        for(activo in activos) {
            if (++cont % 1000 == 0) {
                log.debug "Activos $cont / $max"
            }
            if (!fecha) {
                if (fechaFinal) {
                    fecha = fechaFinal
                } else {
                    fecha = activo.depreciacionFecha
                }
            }
            def depreciacionAcumulada = new BigDecimal('0')
            if (fechaFinal) {
                def depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time)
                depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fechaFinal.time, depreciacionMensual )
            } else {
                depreciacionAcumulada = activo.depreciacionAcumulada
            }
            def centroDeCosto = centrosDeCosto[activo.centroCosto.nombre]
            if (!centroDeCosto) {
                centroDeCosto = new Expando()
                centroDeCosto.id = activo.centroCosto.id
                centroDeCosto.nombre = activo.centroCosto.nombre
                centroDeCosto.descripcion = activo.centroCosto.descripcion
                centroDeCosto.fecha = activo.depreciacionFecha
                for(tipoActivo in tiposDeActivo) {
                    centroDeCosto[tipoActivo.nombre] = new BigDecimal('0')
                }
            }
            centroDeCosto[activo.tipoActivo.nombre] = centroDeCosto[activo.tipoActivo.nombre].add(depreciacionAcumulada)
            totales[activo.tipoActivo.nombre] = totales[activo.tipoActivo.nombre].add(depreciacionAcumulada)
            totales.total = totales.total.add(depreciacionAcumulada)
            centrosDeCosto[centroDeCosto.nombre] = centroDeCosto
        }
        log.debug "Activos $cont / $max"
        def lista = centrosDeCosto.values()
        return [activos: lista, totales: totales, tiposDeActivo: tiposDeActivo, fecha: fecha]
    }

    def depreciacionAcumuladaConCentroCosto(params) {
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
        }
        if (params?.fechaFinal) {
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def resultado = listaConCantidad(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
        if (fechaFinal) {
            log.debug "Tiene la fechaFinal $fechaFinal"
            def lista = resultado.lista
            for(activo in lista) {
                activo.depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time)
                activo.depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fechaFinal.time, activo.depreciacionMensual)
                activo.discard()
            }
        }

        return resultado
    }
	
	def depreciacionAcumuladaPorTipoActivo(params) {
		log.debug "Creando lista de activos agrupada por tipos de activo"
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
        }
        if (params?.fechaFinal) {
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def activos = lista(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
		def resultados = ['depreciacionAcumulada':new BigDecimal(0), 'depreciacionMensual': new BigDecimal('0')]
        def tiposDeActivo = [:] as TreeMap
		def tipos = tipoActivoService.lista(null)
		for (tipoActivo in tipos) {
			def resultado = new Expando()
			resultado.id = tipoActivo.id
			resultado.nombre = tipoActivo.nombre
			resultado.depreciacionAcumulada = new BigDecimal(0)
			resultado.depreciacionMensual = new BigDecimal(0)
			tiposDeActivo[tipoActivo.nombre] = resultado
		}
		def cont = 0
        def fecha
		def total = (activos)?activos.size():0
        def depreciacionAcumulada = new BigDecimal('0')
        def depreciacionMensual = new BigDecimal('0')
		log.debug "Activos $cont / $total"
		for(activo in activos) {
			if (++cont % 2000 == 0) {
				log.debug "Activos $cont / $total"
			}
            if (!fecha) {
                if (fechaFinal) {
                    fecha = fechaFinal
                } else {
                    fecha = activo.depreciacionFecha
                }
            }
            if (fechaFinal) {
                depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time).setScale(2,RoundingMode.HALF_UP)
                depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fechaFinal.time, depreciacionMensual ).setScale(2,RoundingMode.HALF_UP)
            } else {
                depreciacionMensual = activo.depreciacionMensual.setScale(2,RoundingMode.HALF_UP)
                depreciacionAcumulada = activo.depreciacionAcumulada.setScale(2,RoundingMode.HALF_UP)
            }
			def resultado = tiposDeActivo[activo.tipoActivo.nombre]
			resultado.depreciacionAcumulada = resultado.depreciacionAcumulada.add(depreciacionAcumulada)
			resultado.depreciacionMensual = resultado.depreciacionMensual.add(depreciacionMensual)
            tiposDeActivo[activo.tipoActivo.nombre] = resultado
            // Totales
			resultados.depreciacionAcumulada = resultados.depreciacionAcumulada.add(depreciacionAcumulada)
			resultados.depreciacionMensual = resultados.depreciacionMensual.add(depreciacionMensual)
		}
        resultados.activos = tiposDeActivo
        resultados.fecha = fecha
		log.debug "Activos $cont / $total"
		return resultados
	}

    def depreciacionAcumuladaConTipoActivo(params) {
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
        }
        if (params?.fechaFinal) {
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def resultado = listaConCantidad(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
        if (fechaFinal) {
            log.debug "Tiene la fechaFinal $fechaFinal"
            def lista = resultado.lista
            for(activo in lista) {
                activo.depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time)
                activo.depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fechaFinal.time, activo.depreciacionMensual)
                activo.discard()
            }
        }

        return resultado
    }
	
    def depreciacionMensualPorCentroCosto(params) {
        log.debug "Creando lista de activos con depreciacion mensual agrupada por centros de costo"
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
        }
        if (params?.fechaFinal) {
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def activos = lista(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
        def centrosDeCosto = [:] as TreeMap
        def totales = [total:new BigDecimal('0')]

        def cont = 0
        def max = (activos)?activos.size():0
        def fecha
        def tiposDeActivo = tipoActivoService.lista(null)
        for(tipoActivo in tiposDeActivo) {
            totales[tipoActivo.nombre] = new BigDecimal('0')
        }
        log.debug "Activos 0 / $max"
        for(activo in activos) {
            if (++cont % 1000 == 0) {
                log.debug "Activos $cont / $max"
            }
            if (!fecha) {
                if (fechaFinal) {
                    fecha = fechaFinal
                } else {
                    fecha = activo.depreciacionFecha
                }
            }
            def depreciacionMensual = new BigDecimal('0')
            if (fechaFinal) {
                depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time)
            } else {
                depreciacionMensual = activo.depreciacionMensual
            }
            def centroDeCosto = centrosDeCosto[activo.centroCosto.nombre]
            if (!centroDeCosto) {
                centroDeCosto = new Expando()
                centroDeCosto.id = activo.centroCosto.id
                centroDeCosto.nombre = activo.centroCosto.nombre
                centroDeCosto.descripcion = activo.centroCosto.descripcion
                centroDeCosto.fecha = activo.depreciacionFecha
                for(tipoActivo in tiposDeActivo) {
                    centroDeCosto[tipoActivo.nombre] = new BigDecimal(0)
                }
            }
            centroDeCosto[activo.tipoActivo.nombre] = centroDeCosto[activo.tipoActivo.nombre].add(depreciacionMensual)
            totales[activo.tipoActivo.nombre] = totales[activo.tipoActivo.nombre].add(depreciacionMensual)
            totales.total = totales.total.add(depreciacionMensual)
            centrosDeCosto[centroDeCosto.nombre] = centroDeCosto
        }
        log.debug "Activos $cont / $max"
        def lista = centrosDeCosto.values()
        return [activos: lista, totales: totales, tiposDeActivo: tiposDeActivo, fecha: fecha]
    }

	def depreciacionMensualPorTipoActivo(params) {
		log.debug "Creando lista de activos agrupada por tipos de activo"
        def fechaInicial
        def fechaFinal
        def fechaInicialParam
        def fechaFinalParam
        if (params?.fechaInicial) {
            fechaInicial = Calendar.instance
            fechaInicial.time = new Date().parse('dd/MM/yyyy',params.fechaInicial)
            fechaInicialParam = params.fechaInicial
        }
        if (params?.fechaFinal) {
            fechaFinal = Calendar.instance
            fechaFinal.time = new Date().parse('dd/MM/yyyy',params.fechaFinal)
            fechaFinalParam = params.fechaFinal
        }
        params.remove 'fechaInicial'
        params.remove 'fechaFinal'
        params.conTipoActivo = true
        params.activo = true
        def activos = lista(params)
        params.fechaInicial = fechaInicialParam
        params.fechaFinal = fechaFinalParam
		def resultados = ['depreciacionMensual': new BigDecimal('0')]
        def tiposDeActivo = [:] as TreeMap
		def tipos = tipoActivoService.lista(null)
		for (tipoActivo in tipos) {
			def resultado = new Expando()
			resultado.id = tipoActivo.id
			resultado.nombre = tipoActivo.nombre
			resultado.depreciacionMensual = new BigDecimal(0)
			tiposDeActivo[tipoActivo.nombre] = resultado
		}
		def cont = 0
        def fecha
		def total = (activos)?activos.size():0
        def depreciacionMensual = new BigDecimal('0')
		log.debug "Activos $cont / $total"
		for(activo in activos) {
			if (++cont % 2000 == 0) {
				log.debug "Activos $cont / $total"
			}
            if (!fecha) {
                if (fechaFinal) {
                    fecha = fechaFinal
                } else {
                    fecha = activo.depreciacionFecha
                }
            }
            if (fechaFinal) {
                depreciacionMensual = obtieneDepreciacionMensual(activo, fechaFinal.time).setScale(2,RoundingMode.HALF_UP)
            } else {
                depreciacionMensual = activo.depreciacionMensual.setScale(2,RoundingMode.HALF_UP)
            }
			def resultado = tiposDeActivo[activo.tipoActivo.nombre]
			resultado.depreciacionMensual = resultado.depreciacionMensual.add(depreciacionMensual)
            tiposDeActivo[activo.tipoActivo.nombre] = resultado
            // Totales
			resultados.depreciacionMensual = resultados.depreciacionMensual.add(depreciacionMensual)
		}
        resultados.activos = tiposDeActivo
        resultados.fecha = fecha
		log.debug "Activos $cont / $total"
		return resultados
	}

    def subeActivos(archivo) {
        Activo.withTransaction {
            def sdf = new SimpleDateFormat('d/M/yyyy')
            def sdf2 = new SimpleDateFormat('d/MM/yy')
            def sdf3 = new SimpleDateFormat('d-MM-yyyy')
            def ccostos = [:]
            def tipos = [:]
            def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
            log.debug "Subiendo los activos de $usuario.almacen.empresa"
            def proveedor = Proveedor.findByEmpresaAndBase(usuario.almacen.empresa,true)
            def nivel = Nivel.findByNombreAndOrganizacion('ACTIVOS',usuario.almacen.empresa.organizacion)
            if (!nivel) {
                nivel = new Nivel(
                    nombre : 'ACTIVOS'
                    , descripcion : 'ACTIVOS'
                    , organizacion : usuario.almacen.empresa.organizacion
                    , clasificacion : Clasificacion.findByNombre('clasificacion.activo')
                )
                nivel.save()
            }
            POIFSFileSystem fs = new POIFSFileSystem(new java.io.ByteArrayInputStream(archivo.bytes))
            HSSFWorkbook workbook = new HSSFWorkbook(fs)
            def evaluator = workbook.getCreationHelper().createFormulaEvaluator()
            def numeroDePaginas = workbook.getNumberOfSheets()
            //def numeroDePaginas = 6

            def sql = Sql.newInstance(dataSource)
            sql.withTransaction {
                def conn = sql.connection
                def ps = conn.prepareStatement('insert into activos (version, centro_costo_id, codigo, condicion, date_created, depreciacion_acumulada, depreciacion_anual, depreciacion_fecha, depreciacion_mensual, descripcion, empresa_id, factura, fecha_compra, fecha_inactivo, folio, garantia, inactivo, inpc, marca, meses_garantia, modelo, moi, moneda, motivo, pedimento, poliza, procedencia, proveedor_id, responsable, seguro, serial, tipo_activo_id, tipo_cambio, ubicacion, valor_neto, valor_rescate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)', java.sql.Statement.RETURN_GENERATED_KEYS)
                def ps2 = conn.prepareStatement('insert into xactivos (version, actividad, activo_id, centro_costo_id, codigo, condicion, creador, date_created, depreciacion_acumulada, depreciacion_anual, depreciacion_fecha, depreciacion_mensual, descripcion, empresa_id, factura, fecha_compra, fecha_inactivo, folio, garantia, inactivo, inpc, last_updated, marca, meses_garantia, modelo, moi, moneda, motivo, pedimento, poliza, procedencia, proveedor_id, responsable, seguro, serial, tipo_activo_id, tipo_cambio, ubicacion, valor_neto, valor_rescate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)')
                def date = new java.sql.Timestamp((new Date()).time)
                def cont = 0;
                def fecha = new Date()
                for(int idx = 0; idx < numeroDePaginas; idx++) {
                    log.debug "#######################################"
                    log.debug "#######################################"
                    log.debug "#######################################"
                    log.debug "############ Pagina $idx"
                    log.debug "#######################################"
                    log.debug "#######################################"
                    HSSFSheet sheet = workbook.getSheetAt(idx)
                    def rows = sheet.getPhysicalNumberOfRows()
                    for(i in 8..rows) {
                        if (i % 100 == 0) {
                            log.debug "#######################################"
                            log.debug "#######################################"
                            log.debug "#######################################"
                            log.debug "####                 Renglon $i / $rows"
                            log.debug "#######################################"
                            log.debug "#######################################"
                            sessionFactory.currentSession.flush()
                        }
                        HSSFRow row = sheet.getRow(i)
                        if (row) {
                            try {
                                log.debug("Buscando centro de costo ${row.getCell(1)}")
                                def nombreCuenta = row.getCell(1)?.toString()?.trim()
                                if (row.getCell(1) && !"".equals(nombreCuenta)) {
                                    def ccosto = ccostos[nombreCuenta]
                                    if(!ccosto) {
                                        ccosto = Cuenta.findByNombreLikeAndOrganizacion("%$nombreCuenta", usuario.almacen.empresa.organizacion)
                                        if (!ccosto) {
                                            def nombreCCosto = row.getCell(0)?.toString()?.trim()
                                            if (nombreCCosto) {
                                                ccosto = Cuenta.findByDescripcionIlikeAndOrganizacion("%$nombreCCosto%", usuario.almacen.empresa.organizacion)
                                                if (!ccosto) {
                                                    log.debug("Creando centro de costo $nombreCuenta")
                                                    ccosto = new Cuenta(
                                                        nombre : nombreCuenta
                                                        , descripcion : nombreCCosto
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                        , centroCosto : true
                                                    )
                                                    ccosto = cuentaService.crea(ccosto)
                                                }
                                            }
                                        }
                                        ccostos[nombreCuenta] = ccosto
                                    }

                                    log.debug("Buscando tipo de cuenta ${row.getCell(2)}")
                                    def nombreTipo = row.getCell(2)?.toString().trim()
                                    if (nombreTipo == 'A') {
                                        nombreTipo = 'EQUIPO DE COMPUTO'
                                    } else if (nombreTipo == 'B') {
                                        nombreTipo = 'EQUIPO DE RESTAURANTE'
                                    } else if (nombreTipo == 'C') {
                                        nombreTipo = 'MOBILIARIO Y EQUIPO DE OFICINA'
                                    } else if (nombreTipo == 'D') {
                                        nombreTipo = 'MAQUINARIA Y EQUIPO'
                                    } else if (nombreTipo == 'E') {
                                        nombreTipo = 'EQUIPO DE TRANSPORTE'
                                    } else if (nombreTipo == 'F') {
                                        nombreTipo = 'EQUIPO DE TELECOMUNICACIONES'
                                    } 
                                    def tipo = tipos[nombreTipo]
                                    if (!tipo) {
                                        tipo = TipoActivo.findByNombreAndEmpresa(nombreTipo,usuario.almacen.empresa)
                                        if (!tipo) {
                                            log.debug("Creando tipo porque no lo encontre ($nombreTipo)")
                                            if (nombreTipo.equals('F')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.06', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.06'
                                                        , descripcion : 'EQUIPO DE TELECOMUNICACIONES'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                    )
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'EQUIPO DE TELECOMUNICACIONES'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.25')
                                                    , vidaUtil : 48L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            } else if (nombreTipo.equals('E')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.03', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.03'
                                                        , descripcion : 'EQUIPO DE TRANSPORTE'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                    )
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'EQUIPO DE TRANSPORTE'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.25')
                                                    , vidaUtil : 48L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            } else if (nombreTipo.equals('D')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.04', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.04'
                                                        , descripcion : 'MAQUINARIA Y EQUIPO'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                    )
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'MAQUINARIA Y EQUIPO'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.10')
                                                    , vidaUtil : 120L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            } else if (nombreTipo.equals('C')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.01', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.01'
                                                        , descripcion : 'MOBILIARIO Y EQUIPO DE OFICINA'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                    )
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'MOBILIARIO Y EQUIPO DE OFICINA'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.10')
                                                    , vidaUtil : 120L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            } else if (nombreTipo.equals('B')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.05', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.05'
                                                        , descripcion : 'EQUIPO DE RESTAURANTE'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel
                                                    )
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'EQUIPO DE RESTAURANTE'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.20')
                                                    , vidaUtil : 60L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            } else if (nombreTipo.equals('A')) {
                                                def cuenta = Cuenta.findByNombreAndOrganizacion('1.3.01.02', usuario.almacen.empresa.organizacion)
                                                if (!cuenta) {
                                                    cuenta = new Cuenta (
                                                        nombre : '1.3.01.02'
                                                        , descripcion : 'EQUIPO DE COMPUTO'
                                                        , organizacion : usuario.almacen.empresa.organizacion
                                                        , nivel : nivel)
                                                    cuenta = cuentaService.crea(cuenta)
                                                }
                                                tipo = new TipoActivo(
                                                    nombre : nombreTipo
                                                    , descripcion : 'EQUIPO DE COMPUTO'
                                                    , cuenta : cuenta
                                                    , porciento : new BigDecimal('0.30')
                                                    , vidaUtil : 40L
                                                    , empresa : usuario.almacen.empresa
                                                )
                                                tipo = tipoActivoService.crea(tipo)
                                            }
                                        }
                                        tipos[nombreTipo] = tipo
                                    }

                                    log.debug("El tipo es $tipo")
                                    if (tipo) {
                                        def fechaCompra
                                        try {
                                            fechaCompra = row.getCell(7).getDateCellValue()
                                        } catch(Exception e) {
                                            def fechaCompraString = row.getCell(7).toString().trim()
                                            try {
                                                fechaCompra = sdf.parse(fechaCompraString)
                                            } catch(Exception ex) {
                                                try {
                                                    fechaCompra = sdf2.parse(fechaCompraString)
                                                } catch(Exception ex2) {
                                                    fechaCompra = sdf3.parse(fechaCompraString)
                                                }
                                            }
                                        }
                                        def seguro = false
                                        if (!"".equals(row.getCell(6).toString().trim())) {
                                            seguro = true
                                        }

                                        def poliza = row.getCell(3).toString().trim()
                                        def codigo = row.getCell(8).toString().trim()
                                        def descripcion = row.getCell(9).toString().trim()
                                        def marca = row.getCell(10).toString().trim()
                                        def modelo = row.getCell(11).toString().trim()
                                        def serie = row.getCell(12).toString().trim()
                                        def ubicacion = row.getCell(13).toString().trim()
                                        def responsable = row.getCell(14).toString().trim()
                                        def costo
                                        switch (row.getCell(15).getCellType()) {
                                            case HSSFCell.CELL_TYPE_FORMULA:
                                            log.debug("############## Es de tipo formula")
                                            costo = evaluator.evaluateInCell(row.getCell(15)).getNumericCellValue()
                                            break
                                            case HSSFCell.CELL_TYPE_NUMERIC:
                                            costo = row.getCell(15).getNumericCellValue()
                                            break
                                            default:
                                            costo = row.getCell(15).toString().trim()
                                        }
                                        if (!costo) {
                                            costo = new BigDecimal('0')
                                        }

                                        def x = new Activo(
                                            centroCosto:ccosto
                                            ,tipoActivo:tipo
                                            ,proveedor:proveedor
                                            ,poliza:poliza
                                            ,fechaCompra:fechaCompra
                                            ,codigo:codigo
                                            ,descripcion:descripcion
                                            ,marca:marca
                                            ,modelo:modelo
                                            ,serie:serie
                                            ,ubicacion:ubicacion
                                            ,moi:costo
                                            ,responsable:responsable
                                            ,seguro:seguro
                                            ,empresa:usuario.almacen.empresa
                                        )

                                        if (++cont % 1000 == 0) {
                                            log.debug "Depreciando $cont"
                                        }
                                        if (cont % 100 == 0 && cont > 0) {
                                            //ps.executeBatch()
                                            ps2.executeBatch()
                                        }
                                        asignaciones(x, true, fecha)
                                        //actualiza(x)
                                        // Modificacion para utilizar jdbc
                                        x.discard()

                                        ps.setLong(1, 0)
                                        ps.setLong(2, ccosto.id)
                                        ps.setString(3, x.codigo)
                                        ps.setString(4, x.condicion)
                                        ps.setTimestamp(5, date)
                                        ps.setBigDecimal(6, x.depreciacionAcumulada)
                                        ps.setBigDecimal(7, x.depreciacionAnual)
                                        ps.setDate(8, new java.sql.Date(fecha.time))
                                        ps.setBigDecimal(9, x.depreciacionMensual)
                                        ps.setString(10, x.descripcion)
                                        ps.setLong(11, usuario.almacen.empresa.id)
                                        ps.setString(12, x.factura)
                                        ps.setDate(13, new java.sql.Date(x.fechaCompra.time))
                                        ps.setDate(14, null)
                                        ps.setString(15, x.folio)
                                        ps.setBoolean(16, x.garantia)
                                        ps.setBoolean(17, x.inactivo)
                                        ps.setBigDecimal(18, x.inpc)
                                        ps.setString(19, x.marca)
                                        ps.setInt(20, x.mesesGarantia)
                                        ps.setString(21, x.modelo)
                                        ps.setBigDecimal(22, x.moi)
                                        ps.setString(23, x.moneda)
                                        ps.setString(24, x.motivo)
                                        ps.setString(25, x.pedimento)
                                        ps.setString(26, x.poliza)
                                        ps.setString(27, x.procedencia)
                                        ps.setLong(28, proveedor.id)
                                        ps.setString(29, x.responsable)
                                        ps.setBoolean(30, x.seguro)
                                        ps.setString(31, x.serial)
                                        ps.setLong(32, tipo.id)
                                        ps.setBigDecimal(33, x.tipoCambio)
                                        ps.setString(34, x.ubicacion)
                                        ps.setBigDecimal(35, x.valorNeto)
                                        ps.setBigDecimal(36, x.valorRescate)
                                        ps.executeUpdate()
                                        def keys = ps.getGeneratedKeys()
                                        keys.next()
                                        x.id = keys.getInt(1)
                                        keys.close()

                                        ps2.setLong(1, 0)
                                        ps2.setString(2, Constantes.CREAR)
                                        ps2.setLong(3, x.id)
                                        ps2.setLong(4, ccosto.id)
                                        ps2.setString(5, x.codigo)
                                        ps2.setString(6, x.condicion)
                                        ps2.setString(7, springSecurityService.authentication.name)
                                        ps2.setTimestamp(8, date)
                                        ps2.setBigDecimal(9, x.depreciacionAcumulada)
                                        ps2.setBigDecimal(10, x.depreciacionAnual)
                                        ps2.setDate(11, new java.sql.Date(fecha.time))
                                        ps2.setBigDecimal(12, x.depreciacionMensual)
                                        ps2.setString(13, x.descripcion)
                                        ps2.setLong(14, usuario.almacen.empresa.id)
                                        ps2.setString(15, x.factura)
                                        ps2.setDate(16, new java.sql.Date(x.fechaCompra.time))
                                        ps2.setDate(17, null)
                                        ps2.setString(18, x.folio)
                                        ps2.setBoolean(19, x.garantia)
                                        ps2.setBoolean(20, x.inactivo)
                                        ps2.setBigDecimal(21, x.inpc)
                                        ps2.setTimestamp(22, date)
                                        ps2.setString(23, x.marca)
                                        ps2.setInt(24, x.mesesGarantia)
                                        ps2.setString(25, x.modelo)
                                        ps2.setBigDecimal(26, x.moi)
                                        ps2.setString(27, x.moneda)
                                        ps2.setString(28, x.motivo)
                                        ps2.setString(29, x.pedimento)
                                        ps2.setString(30, x.poliza)
                                        ps2.setString(31, x.procedencia)
                                        ps2.setLong(32, proveedor.id)
                                        ps2.setString(33, x.responsable)
                                        ps2.setBoolean(34, x.seguro)
                                        ps2.setString(35, x.serial)
                                        ps2.setLong(36, tipo.id)
                                        ps2.setBigDecimal(37, x.tipoCambio)
                                        ps2.setString(38, x.ubicacion)
                                        ps2.setBigDecimal(39, x.valorNeto)
                                        ps2.setBigDecimal(40, x.valorRescate)
                                        ps2.addBatch()

                                    } else {
                                        log.debug("ERROR en Pagina: $idx | Renglon: $i")
                                        throw new RuntimeException("No se encontro el tipo $nombreTipo")
                                    }
                                }
                                ps2.executeBatch()
                            } catch(Exception e) {
                                log.debug("Pagina: $idx | Renglon: $i")
                                log.error("No se pudo crear un registro",e)
                                throw new RuntimeException("Hubo un problema",e)
                            }
                        }
                    }
                }
            }
            sql.close()
        }// Transaccion
        sessionFactory.currentSession.flush()
    }

    def reporteDia2(anio) {
        def fecha = Calendar.instance
        fecha.timeInMillis = 0
        fecha.set(Calendar.DAY_OF_YEAR,1)
        fecha.set(Calendar.YEAR,anio)
        fecha.set(Calendar.HOUR_OF_DAY,0)
        log.debug("Armando reporte dia con el anio: $anio : ${fecha.time}")
        def grupos = [:] as TreeMap
        def totalCosto = new BigDecimal('0')
        def totalCompras = new BigDecimal('0')
        def totalBajas = new BigDecimal('0')
        def costoFinal = new BigDecimal('0')
        def totalDepreciacionAcumulada = new BigDecimal('0')
        def totalComprasAcumuladas = new BigDecimal('0')
        def totalBajasAcumuladas = new BigDecimal('0')
        def totalDepreciacionFinal = new BigDecimal('0')
        def valorNeto = new BigDecimal('0')

        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def tipoActivos = TipoActivo.findAllByEmpresa(usuario.almacen.empresa)
        for(tipoActivo in tipoActivos) {
            grupos[tipoActivo.nombre] = new Expando(
                cuenta : tipoActivo.cuenta.nombre
                , nombre : tipoActivo.nombre
                , costo: new BigDecimal('0')
                , compras : new BigDecimal('0')
                , bajas : new BigDecimal('0')
                , costoFinal : new BigDecimal('0')
                , depreciacionAcumulada : new BigDecimal('0')
                , comprasAcumuladas : new BigDecimal('0')
                , bajasAcumuladas : new BigDecimal('0')
                , depreciacionFinal : new BigDecimal('0')
                , valorNeto : new BigDecimal('0')
            )
        }
        def activos = Activo.executeQuery("select new map(ta.nombre as nombre, sum(a.moi) as costo, sum(a.depreciacionAcumulada) as depreciacionAcumulada, sum(a.depreciacionAnual) as depreciacionAnual) from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.fechaCompra < :fecha group by ta.nombre order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time])
        for(activo in activos) {
            log.debug "Grupo: $activo"
            def resultado = grupos[activo.nombre]
            resultado.costo = activo.costo
            resultado.depreciacionAcumulada = activo.depreciacionAcumulada
            resultado.comprasAcumuladas = activo.depreciacionAnual
            totalCosto = totalCosto.add(activo.costo)
            totalDepreciacionAcumulada = totalDepreciacionAcumulada.add(activo.depreciacionAcumulada)
            totalComprasAcumuladas = totalComprasAcumuladas.add(activo.depreciacionAnual)
        }

        def activos2 = Activo.executeQuery("select new map(ta.nombre as nombre, sum(a.moi) as costo, sum(a.depreciacionAcumulada) as comprasAcumuladas) from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.fechaCompra >= :fecha and (a.fechaInactivo is null or a.fechaInactivo < :fecha) group by ta.nombre order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time])
        for(activo in activos2) {
            log.debug "Compras: $activo"
            def resultado = grupos[activo.nombre]
            resultado.compras = activo.costo
            resultado.comprasAcumuladas = activo.comprasAcumuladas
            totalCompras = totalCompras.add(activo.costo)
            totalComprasAcumuladas = totalComprasAcumuladas.add(activo.comprasAcumuladas)
        }

        def activos3 = Activo.executeQuery("select new map(ta.nombre as nombre, sum(a.moi) as costo, sum(a.depreciacionAcumulada) as bajasAcumuladas) from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.inactivo = true and a.fechaInactivo >= :fecha group by ta.nombre order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time])
        for(activo in activos3) {
            log.debug "Bajas: $activo"
            def resultado = grupos[activo.nombre]
            resultado.bajas = activo.costo
            //resultado.comprasAcumuladas = resultado.comprasAcumuladas.add(activo.comprasAcumuladas)
            resultado.bajasAcumuladas = activo.bajasAcumuladas
            totalBajas = totalBajas.add(activo.costo)
            //totalComprasAcumuladas = totalComprasAcumuladas.add(activo.comprasAcumuladas)
            totalBajasAcumuladas = totalBajasAcumuladas.add(activo.bajasAcumuladas)
        }

        def activos4 = Activo.executeQuery("select new map(ta.nombre as nombre, sum(a.moi) as costo, sum(a.depreciacionAcumulada) as bajasAcumuladas) from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.inactivo = true and a.fechaInactivo < :fecha group by ta.nombre order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time])
        for(activo in activos4) {
            log.debug "BajasAnteriores: $activo"
            def resultado = grupos[activo.nombre]
            resultado.costo = resultado.costo.subtract(activo.costo)
            resultado.depreciacionAcumulada = resultado.depreciacionAcumulada.subtract(activo.bajasAcumuladas)
            totalCosto = totalCosto.subtract(activo.costo)
            totalDepreciacionAcumulada = totalDepreciacionAcumulada.subtract(activo.bajasAcumuladas)
        }

        for(tipoActivo in tipoActivos) {
            def grupo = grupos[tipoActivo.nombre]
            grupo.costoFinal = grupo.costo.add(grupo.compras.subtract(grupo.bajas))
            costoFinal = costoFinal.add(grupo.costoFinal)
            grupo.depreciacionFinal = grupo.depreciacionAcumulada.add(grupo.comprasAcumuladas.subtract(grupo.bajasAcumuladas))
            totalDepreciacionFinal = totalDepreciacionFinal.add(grupo.depreciacionFinal)
            grupo.valorNeto = grupo.costoFinal.subtract(grupo.depreciacionFinal)
            valorNeto = valorNeto.add(grupo.valorNeto)
        }

        return [lista:grupos.values(), totalCosto: totalCosto, totalCompras: totalCompras, totalBajas: totalBajas, costoFinal: costoFinal, totalDepreciacionAcumulada: totalDepreciacionAcumulada, totalComprasAcumuladas: totalComprasAcumuladas, totalBajasAcumuladas: totalBajasAcumuladas, totalDepreciacionFinal: totalDepreciacionFinal, valorNeto: valorNeto]
    }

    def reporteDia(anio) {
        def fecha = Calendar.instance
        fecha.timeInMillis = 0
        fecha.set(Calendar.DAY_OF_YEAR,1)
        fecha.set(Calendar.YEAR,anio)
        fecha.set(Calendar.HOUR_OF_DAY,0)
        log.debug("Armando reporte dia con el anio: $anio : ${fecha.time}")
        def fecha2 = Calendar.instance
        fecha2.timeInMillis = 0
        fecha2.set(Calendar.DAY_OF_YEAR,1)
        fecha2.set(Calendar.YEAR,(anio+1))
        fecha2.set(Calendar.HOUR_OF_DAY,0)
        def grupos = [:] as TreeMap
        def totalCosto = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def totalCompras = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def totalBajas = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def costoFinal = new BigDecimal('0')
        def totalDepreciacionAcumulada = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def totalComprasAcumuladas = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def totalBajasAcumuladas = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def totalDepreciacionFinal = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
        def valorNeto = new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)

        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def tipoActivos = TipoActivo.findAllByEmpresa(usuario.almacen.empresa)
        for(tipoActivo in tipoActivos) {
            grupos[tipoActivo.nombre] = new Expando(
                cuenta : tipoActivo.cuenta.nombre
                , nombre : tipoActivo.nombre
                , costo: new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , compras : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , bajas : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , costoFinal : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , depreciacionAcumulada : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , comprasAcumuladas : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , bajasAcumuladas : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , depreciacionFinal : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
                , valorNeto : new BigDecimal('0').setScale(2,RoundingMode.HALF_UP)
            )
        }

        // Activos
        log.debug("Activos")
        int cont = 0
        def activos = Activo.executeQuery("select a from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.fechaCompra < :fecha and (a.inactivo = false or a.fechaInactivo > :fecha) order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time])
        for(activo in activos) {
            if (++cont % 1000 == 0) {
                log.debug "Activos $cont"
            }
            def resultado = grupos[activo.tipoActivo.nombre]
            def depreciacionMensual = obtieneDepreciacionMensual(activo, fecha.time)
            def depreciacionAcumulada = obtieneDepreciacionAcumulada2(activo, fecha.time, depreciacionMensual )
            resultado.costo = resultado.costo.add(activo.moi)
            resultado.depreciacionAcumulada = resultado.depreciacionAcumulada.add( depreciacionAcumulada )
            totalCosto = totalCosto.add(activo.moi)
            totalDepreciacionAcumulada = totalDepreciacionAcumulada.add(depreciacionAcumulada)

            depreciacionMensual = obtieneDepreciacionMensual(activo, fecha2.time)
            depreciacionAcumulada = obtieneDepreciacionAcumulada2(activo, fecha2.time, depreciacionMensual )
            resultado.depreciacionFinal = resultado.depreciacionFinal.add(depreciacionAcumulada)
            totalDepreciacionFinal = totalDepreciacionFinal.add(depreciacionAcumulada)
        }

        // Compras
        log.debug("COMPRAS")
        cont = 0
        def compras = Activo.executeQuery("select a from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.fechaCompra between :fecha and :fecha2  and a.inactivo = false order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time, fecha2: fecha2.time])
        for(activo in compras) {
            if (++cont % 100 == 0) {
                log.debug "Activos $cont"
            }
            def resultado = grupos[activo.tipoActivo.nombre]
            resultado.compras = resultado.compras.add(activo.moi)
            totalCompras = totalCompras.add(activo.moi)
            def depreciacionMensual = obtieneDepreciacionMensual(activo, fecha2.time)
            def depreciacionAcumulada = obtieneDepreciacionAcumulada(activo, fecha2.time, depreciacionMensual )
            resultado.comprasAcumuladas = resultado.comprasAcumuladas.add(depreciacionAcumulada)
            totalComprasAcumuladas = totalComprasAcumuladas.add(depreciacionAcumulada)

            resultado.depreciacionFinal = resultado.depreciacionFinal.add(depreciacionAcumulada)
            totalDepreciacionFinal = totalDepreciacionFinal.add(depreciacionAcumulada)
        }

        // Bajas
        log.debug("BAJAS")
        cont = 0
        def bajas = Activo.executeQuery("select a from Activo a inner join a.tipoActivo ta where a.empresa = :empresa and a.inactivo = true and a.fechaInactivo between :fecha and :fecha2 order by ta.nombre", [empresa: usuario.almacen.empresa, fecha: fecha.time, fecha2: fecha2.time])
        for(activo in bajas) {
            if (++cont % 10 == 0) {
                log.debug "Activos $cont"
            }
            def resultado = grupos[activo.tipoActivo.nombre]
            def depreciacionMensual = obtieneDepreciacionMensual(activo, activo.fechaInactivo)
            def depreciacionAcumulada = obtieneDepreciacionAcumulada2(activo, activo.fechaInactivo, depreciacionMensual )
            resultado.bajas = resultado.bajas.add(activo.moi)
            resultado.bajasAcumuladas = resultado.bajasAcumuladas.add(depreciacionAcumulada)
            totalBajas = totalBajas.add(activo.moi)
            totalBajasAcumuladas = totalBajasAcumuladas.add(depreciacionAcumulada)
        }

        for(tipoActivo in tipoActivos) {
            def grupo = grupos[tipoActivo.nombre]
            grupo.costoFinal = grupo.costo.add(grupo.compras.subtract(grupo.bajas))
            costoFinal = costoFinal.add(grupo.costoFinal)

            //grupo.depreciacionFinal = grupo.depreciacionAcumulada.add(grupo.comprasAcumuladas.subtract(grupo.bajasAcumuladas))
            //totalDepreciacionFinal = totalDepreciacionFinal.add(grupo.depreciacionFinal)
            grupo.comprasAcumuladas = grupo.depreciacionFinal.subtract(grupo.depreciacionAcumulada)
            totalComprasAcumuladas = totalDepreciacionFinal.subtract(totalDepreciacionAcumulada)

            grupo.valorNeto = grupo.costoFinal.subtract(grupo.depreciacionFinal)
            valorNeto = valorNeto.add(grupo.valorNeto)
        }

        return [lista:grupos.values(), totalCosto: totalCosto, totalCompras: totalCompras, totalBajas: totalBajas, costoFinal: costoFinal, totalDepreciacionAcumulada: totalDepreciacionAcumulada, totalComprasAcumuladas: totalComprasAcumuladas, totalBajasAcumuladas: totalBajasAcumuladas, totalDepreciacionFinal: totalDepreciacionFinal, valorNeto: valorNeto]
    }
}
