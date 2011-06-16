package activos

import general.*
import grails.plugins.springsecurity.Secured
import groovy.sql.*

@Secured(['ROLE_EMP'])
class ActivoController {

    def activoService
    def tipoActivoService
    def proveedorService
    def cuentaService
    def springSecurityService
    def dataSource

    def index = {
        redirect(action: lista, params: params)
    }

  def lista = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def resultado = activoService.listaConCantidad(params.id)
        return [activos: resultado.lista, totalDeActivos: resultado.cantidad, acumulada: resultado.acumulada, mensual: resultado.mensual, anual: resultado.anual, costoTotal: resultado.costoTotal]
    }
    
   
    

    def listaEnHojaDeCalculo = {
        try {
            def wb = activoService.listaEnHojaDeCalculo(params)
            def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
            def reporte = "activos${usuario.almacen.codigo}"

            response.contentType = "application/vnd.ms-excel"
            response.setHeader("Content-disposition","attachment; filename=\"${reporte}\"")
            def out = response.getOutputStream()
            wb.write(out)
            out.flush()
        } catch(Exception e) {
            log.error("No se pudo crear el reporte",e)
            flash.message = message(code:'activo.noReporte')
            redirect(action:'lista')
        }
    }

    def nuevo = {
        Activo activo = new Activo(params)
        //def tiposDeActivo = tipoActivoService.lista(null)
        activo.properties = params
        return [activo:activo, tiposDeActivo:tiposDeActivo, motivos: motivos()]
    }

    def crea = {
        def activo

        try {
            Activo.withTransaction {
                def fechaCompra = new Date().parse('dd/MM/yyyy',params.fechaCompra)
                params.remove 'fechaCompra'
                activo = new Activo(params)
                activo.fechaCompra = fechaCompra
                //activo = activoService.crea(activo)
                activo = Activo.save(activo)

                flash.message = message(code:"activo.crea",args:[activo.folio])
                redirect(action:"ver", id:activo.id)
           }
        } catch(Exception e) {
            log.error("No se pudo crear la activo",e)
            if (activo) {
                activo.discard()
            }
            flash.message = message(code:"activo.noCrea")
            //def tiposDeActivo = tipoActivoService.lista(null)
            render(view:"nuevo", model: [activo: activo, tiposDeActivo:tiposDeActivo, motivos: motivos()])
        }
    }

    def ver = {
        if (params?.activo?.id) {
            params.id = params.activo.id
        }
        //def activo = activoService.obtiene(params.id)
        def activo = Activo.get(params.id)
        return [activo:activo]
    }

    def edita = {
        //def activo = activoService.obtiene(params.id)
        def activo = Activo.get(params.id)
        def tiposDeActivos = TiposDeActivos.get(params.id)
        def motivos = Motivos.get(params.id)
        def tiposDeActivo = tipoActivoService.lista(null)
        return [activo:activo, tiposDeActivo:tiposDeActivo, motivos: motivos()]
    }

    def actualiza = {
        log.debug "Actualizando activo $params.id"
        def activo = activoService.obtiene(params.id)
        try {
            Activo.withTransaction {
                def archivo = request.getFile('archivo')
                if (!archivo.empty) {
                    //archivo.transferTo( new File("/tmp/${archivo.originalFilename}") )
                    byte[] f = archivo.bytes
                    log.debug "${f.length}"
                    def imagen = new Imagen(
                        nombre : archivo.originalFilename
                        , tipoContenido : archivo.contentType
                        , tamano : archivo.size
                        , archivo : f
                    )
                    activo.imagenes.clear()
                    activo.imagenes << imagen
                }

                activo.properties = params
                activo = activoService.actualiza(activo)
                flash.message = message(code:"activo.actualiza",args:[activo.folio])
                redirect(action:"ver",id:activo.id)
            }
        } catch(Exception e) {
            log.error("No se pudo actualizar la activo",e)
            if (activo) {
                activo.discard()
            }
            flash.message = message(code:"activo.noActualiza")
            def tiposDeActivo = tipoActivoService.lista(null)
            render(view:"edita",model:[activo:activo, tiposDeActivo:tiposDeActivo, motivos: motivos()])
        }

    }

    def elimina = {
        try {
            Activo.withTransaction {
                log.debug "Buscando el activo $params.id"
                Activo activo = activoService.obtiene(params.id)
                log.debug "Creando la baja"
                BajaActivo bajaActivo = new BajaActivo(
                    activo:activo
                    ,fechaBaja: new Date()
                )
                bajaActivo.discard()
                activo.discard()
                log.debug "Mandandolo a baja"
                render(view:'baja', model: [bajaActivo:bajaActivo,motivos:motivosBaja()])
            }
        } catch(Exception e) {
            log.error("No se pudo dar de baja la activo",e)
            flash.message = message(code:"activo.noBaja")
            render(view:'ver',model:[activo:activoService.obtiene(params.id)])
        }
    }

    def baja = {
        def bajaActivo
        try {
            Activo.withTransaction {
                def fechaBaja = new Date().parse('dd/MM/yyyy',params.fechaBaja)
                params.remove 'fechaBaja'
                bajaActivo = new BajaActivo(params)
                bajaActivo.fechaBaja = fechaBaja
                def activo = activoService.baja(bajaActivo)
                flash.message = message(code:"activo.baja",args:[activo.folio])
                redirect(action:"ver", id:bajaActivo.activo.id)
            }
        } catch(Exception e) {
            log.error("No se pudo dar de baja el activo",e)
            if (bajaActivo) {
                bajaActivo.discard()
            }
            flash.message = message(code:"activoController.noBaja")
            render(view:"baja", model: [bajaActivo: bajaActivo, motivos: motivosBaja()])
        }
    }

    def reubica = {
        log.debug "Buscando el activo $params.id"
        Activo activo = activoService.obtiene(params.id)
        def reubicacionActivo = new ReubicacionActivo(
            activo:activo
            ,centroCosto:activo.centroCosto
        )
        activo.discard()
        reubicacionActivo.discard()
        return [reubicacionActivo: reubicacionActivo]
    }

    def asignaReubicacion = {
        def reubicacionActivo
        try {
            Activo.withTransaction {
                reubicacionActivo = new ReubicacionActivo(params)
                def activo = activoService.reubicacion(reubicacionActivo)
                flash.message = message(code:"activo.reubica",args:[activo.folio])
                redirect(action:"ver", id:reubicacionActivo.activo.id)
            }
        } catch(Exception e) {
            log.error("No se pudo reubicar el activo",e)
            if (reubicacionActivo) {
                reubicacionActivo.discard()
            }
            flash.message = message(code:"activo.noReubica")
            render(view:"reubica", model: [reubicacionActivo: reubicacionActivo])
        }
    }

    def proveedores = {
        params.filtro = params.term
        def lista = []
        for(proveedor in proveedorService.lista(params)) {
            lista << [id:proveedor.id,value:proveedor.nombre]
        }
        def result = lista as grails.converters.JSON
        render result
    }

    def proveedorDiv = {
        log.debug "Se pide actualizar el div de proveedores con $params"
        def proveedor = proveedorService.obtiene(params.id)
        def div = """
                                      <table>
                                        <thead>
                                          <tr>
                                            <th>RFC</th>
                                            <th>Proveedor</th>
                                            <th></th>
                                          </tr>
                                        </thead>
                                        <tbody>
                                          <tr>
                                            <td>${proveedor?.rfc}</td>
                                            <td>${proveedor?.nombre}</td>
                                            <td>${proveedor?.nombreCompleto}</td>
                                          </tr>
                                        </tbody>
                                      </table>
        """
        render div
    }

    def centrosDeCosto = {
        params.filtro = params.term
        def lista = []
        for(centroCosto in cuentaService.listaDeCentrosDeCosto(params)) {
            lista << [id:centroCosto.id,value:"$centroCosto.nombre | $centroCosto.descripcion",nombre:"$centroCosto.nombre"]
        }
        def result = lista as grails.converters.JSON
        render result
    }

    def centroCostoDiv = {
        def centroCosto = cuentaService.obtiene(params.id)
        def div = """
                                      <table>
                                        <thead>
                                          <tr>
                                            <th>${message(code:"activo.centroCosto")}</th>
                                            <th>${message(code:"centroCosto.descripcion")}</th>
                                          </tr>
                                        </thead>
                                        <tbody>
                                          <tr>
                                            <td>${centroCosto?.nombre}</td>
                                            <td>${centroCosto?.descripcion}</td>
                                          </tr>
                                        </tbody>
                                      </table>
        """

        render div
    }

    def tiposDeActivo = {
        params.filtro = params.term
        def lista = []
        for(tipoActivo in tipoActivoService.lista(params)) {
            lista << [id:tipoActivo.id,value:"$tipoActivo.nombre | $tipoActivo.cuenta",nombre:"$tipoActivo.cuenta.descripcion"]
        }
        def result = lista as grails.converters.JSON
        render result
    }

    def responsables = {
        log.debug "Buscando responsables por $params"
        def lista = []
        for(responsable in activoService.buscaPorResponsable(params?.term)) {
            lista << [id:responsable.nombre,value:responsable.nombre]
        }
        def result = lista as grails.converters.JSON
        render result
    }

    List motivos() {
        def motivos = []
        motivos << new Expando(key:'COMPRA',value:message(code:'motivo.compra'))
        motivos << new Expando(key:'DONACION',value:message(code:'motivo.donacion'))
        return motivos
    }

    List motivosBaja() {
        log.debug "Creando lista de motivos"
        def motivos = []
        motivos << new Expando(key:'OBSOLETO',value:message(code:'motivo.obsoleto'))
        motivos << new Expando(key:'PERDIDA', value:message(code:'motivo.perdida'))
        motivos << new Expando(key:'DONACION',value:message(code:'motivo.donacion'))
        motivos << new Expando(key:'VENTA',   value:message(code:'motivo.venta'))
        return motivos
    }

    def depreciar = {
        try {
            if (params.fecha) {
                def fecha = new Date().parse('dd/MM/yyyy',params.fecha)
                def activo
                if (params.id) {
                    activo = activoService.obtiene(params.id)
                }
                log.debug "Mandando depreciar activos"
                activoService.depreciar(activo, fecha)
                log.debug "Termino de depreciar"

                flash.message = message(code:'activo.mensaje.depreciar')
                redirect(action:'lista')
            }
        } catch(Exception e) {
            log.error "No se pudo depreciar la lista de activos",e
            flash.message = message(code:'activo.mensaje.noDepreciar')
            redirect(action:'lista')
        }
    }
        
    def reportes = {}

    def acumuladaCentroCosto = {
        try {
            def fecha
            def resultado = activoService.depreciacionAcumuladaPorCentroCosto(params)

            return [activos: resultado.activos, total: resultado.totales, tiposDeActivo: resultado.tiposDeActivo, fecha: resultado.fecha]

        } catch(Exception e) {
            log.error "No se pudo crear el reporte de depreciacion acumulada por centro de costo", e
            flash.message = message(code:'activo.mensaje.noAcumuladaCentroCosto')
            redirect(action:'reportes')
        }
    }
	
	def acumuladaCentroCostoDetalle = {
		try {
			params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
			def centroCosto = cuentaService.obtiene(params.id)
			params.centroCosto = centroCosto.nombre
			def resultado = activoService.depreciacionAcumuladaConCentroCosto(params)
            return [activos: resultado.lista, totalDeActivos: resultado.cantidad, acumulada: resultado.acumulada, mensual: resultado.mensual, anual: resultado.anual, costoTotal: resultado.costoTotal]
		} catch(Exception e) {
			log.error "No se pudo crear el reporte con el detalle de la depreciacion acumulada por el centro de costo ${params?.id}", e
			flash.message = message(code:'activo.mensaje.noAcumuladaCentroCostoDetalle',args:[params?.id])
			redirect(action:'reportes')
		}
	}
	
	def acumuladaGrupo = {
		try {
            def fecha
			def resultados = activoService.depreciacionAcumuladaPorTipoActivo(params)

            return [activos:resultados.activos.values(), totalAcumulada: resultados.depreciacionAcumulada, totalMensual: resultados.depreciacionMensual, fecha: resultados.fecha]
		} catch(Exception e) {
			log.error "No se pudo crear el reporte de depreciacion acumulada por grupo", e
			flash.message = message(code:'activo.mensaje.noAcumuladaGrupo')
			redirect(action:'reportes')
		}
	}
	
	def acumuladaGrupoDetalle = {
		try {
			params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
			def tipoActivo = tipoActivoService.obtiene(params.id)
			params.tipoActivo = tipoActivo.cuenta.descripcion
			def resultado = activoService.depreciacionAcumuladaConTipoActivo(params)
            return [activos: resultado.lista, totalDeActivos: resultado.cantidad, acumulada: resultado.acumulada, mensual: resultado.mensual, anual: resultado.anual, costoTotal: resultado.costoTotal]
		} catch(Exception e) {
			log.error "No se pudo crear el detalle del reporte de depreciacion acumulada por grupo", e
			flash.message = message(code:'activo.mensaje.noAcumuladaGrupoDetalle')
			redirect(action:'reportes')
		}
	}

    def mensualCentroCosto = {
        try {
            def resultados = activoService.depreciacionMensualPorCentroCosto(params)

            return [activos: resultados.activos, total: resultados.totales, tiposDeActivo: resultados.tiposDeActivo, fecha: resultados.fecha]
        } catch(Exception e) {
            log.error "No se pudo crear el reporte de depreciacion mensual por centro de costo", e
            flash.message = message(code:'activo.mensaje.noMensualCentroCosto')
            redirect(action:'reportes')
        }
    }
	
	def mensualCentroCostoDetalle = {
		try {
			params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
			def centroCosto = cuentaService.obtiene(params.id)
			params.centroCosto = centroCosto.nombre
			def resultado = activoService.depreciacionAcumuladaConCentroCosto(params)
			render view:'acumuladaCentroCostoDetalle', model:[activos: resultado.lista, totalDeActivos: resultado.cantidad]
		} catch(Exception e) {
			log.error "No se pudo crear el reporte con el detalle de la depreciacion mensual por el centro de costo ${params?.id}", e
			flash.message = message(code:'activo.mensaje.noMensualCentroCostoDetalle',args:[params?.id])
			redirect(action:'reportes')
		}
	}
	
	def mensualGrupo = {
		try {
			def resultados = activoService.depreciacionMensualPorTipoActivo(params)
            return [activos:resultados.activos.values(), total: resultados.depreciacionMensual, fecha: resultados.fecha]
		} catch(Exception e) {
			log.error "No se pudo crear el reporte de depreciacion mensual por grupo", e
			flash.message = message(code:'activo.mensaje.noMensualGrupo')
			redirect(action:'reportes')
		}
	}
	
	def mensualGrupoDetalle = {
		try {
			params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
			def tipoActivo = tipoActivoService.obtiene(params.id)
			params.tipoActivo= tipoActivo.nombre
			def resultado = activoService.depreciacionAcumuladaConTipoActivo(params)
			render view:'acumuladaGrupoDetalle', model:[activos: resultado.lista, totalDeActivos: resultado.cantidad]
		} catch(Exception e) {
			log.error "No se pudo crear el detalle del reporte de depreciacion mensual por grupo", e
			flash.message = message(code:'activo.mensaje.noMensualGrupoDetalle')
			redirect(action:'reportes')
		}
	}

    def bajas = {
        try {
            params.baja = true
            redirect(action: lista, params: params)
        } catch(Exception e) {
            log.error "No se pudo crear el reporte de bajas", e
            flash.message = message(code:'activo.mensaje.noBajas')
            redirect(action:'reportes')
        }
    }

    def reubicaciones = {
        try {
            params.reubicacion = true
            redirect(action: lista, params: params)
        } catch(Exception e) {
            log.error "No se pudo crear el reporte de bajas", e
            flash.message = message(code:'activo.mensaje.noReubicaciones')
            redirect(action:'reportes')
        }
    }

    def imagen = {
        def activo = Activo.get(params.id)
        def imagen
        for(x in activo.imagenes) {
            imagen = x
        }
        if (!imagen) {
            def directorio = servletContext.getRealPath("/images")
            def file = new File("${directorio}/activo_fijo.jpg")
            imagen = new Imagen(
                nombre : 'activoFijo.jpg'
                , tipoContenido : 'image/jpeg'
                , tamano : file.size()
                , archivo : file.getBytes() 
            )
        }
        log.debug "Mostrando imagen ${imagen.nombre}"
        //response.setHeader("Content-disposition", "attachment; filename=${imagen.nombre}")
        response.contentType = imagen.tipoContenido
        response.contentLength = imagen.tamano
        response.outputStream << imagen.archivo
        //response.outputStream.flush()
    }

    def preparaSubir = {
        log.debug "prepara subir"
        render(view:'subir')
    }

    def sube = {
        def file = request.getFile('archivo')
        activoService.subeActivos(file)
        flash.message = "Termino de subir los activos para esta empresa"
        redirect(action:'lista')
    }

    def actualizar = {
        def cont = 0
        def sql = Sql.newInstance(dataSource)
        sql.withTransaction {
            sql.eachRow('select id, responsable from activos') { a ->
                sql.execute 'update activos set ubicacion = ? where id = ?', [a.responsable, a.id]
                sql.execute 'update xactivos set ubicacion = ? where activo_id = ?', [a.responsable, a.id]
                if (cont++ % 100 == 0) {
                    log.debug "${cont-1} activos"
                }
            }
            log.debug "$cont activos"
        }
        flash.message = 'Termino de actualizar'
        redirect(action:'lista')
    }

    def dia = {
        log.debug "Reporte DIA"
        Calendar cal = Calendar.instance
        def anio = cal.get(Calendar.YEAR)
        def anios = anio..(anio-10)
        def vars = [:]
        vars.anios = anios
        if (params.anio) {
            anio = new Integer(params.anio)
            def resultado = activoService.reporteDia(anio)
            vars.anio = anio
            vars.activos = resultado.lista
            vars.totalCosto = resultado.totalCosto
            vars.totalCompras = resultado.totalCompras
            vars.totalBajas = resultado.totalBajas
            vars.costoFinal = resultado.costoFinal
            vars.totalDepreciacionAcumulada = resultado.totalDepreciacionAcumulada
            vars.totalComprasAcumuladas = resultado.totalComprasAcumuladas
            vars.totalBajasAcumuladas = resultado.totalBajasAcumuladas
            vars.totalDepreciacionFinal = resultado.totalDepreciacionFinal
            vars.valorNeto = resultado.valorNeto
        }
        return vars
    }

}
