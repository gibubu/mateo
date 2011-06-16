package activos

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_EMP'])
class TipoActivoController {

    def tipoActivoService
    def cuentaService

    def index = {
        redirect(action: lista, params: params)
    }

//    def lista = {
//        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
//        def resultado = tipoActivoService.listaConCantidad(params)
//        [tiposDeActivo: resultado.lista(params), totalDeTiposDeActivo: resultado.cantidad.count()]
//    }
def lista = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
                def tipoActivo = TipoActivo.get(params.id)
		[tipoActivos: TipoActivo.list(params), totalDeTipoActivos: TipoActivo.count()]
	}

    def nuevo = {
        TipoActivo tipoActivo = new TipoActivo(params)
        return [tipoActivo:tipoActivo]
    }

    def crea = {
        def tipoActivo

        try {
           // TipoActivo.withTransaction {
                tipoActivo = new TipoActivo(params)
                //tipoActivo = tipoActivoService.crea(tipoActivo)
                if (tipoActivo.save(flush: true)) {  
                    flash.message = message(code:"tipoActivo.crea",args:[tipoActivo])
                    redirect(action:"ver", id:tipoActivo.id)
                }  
                
            //}
        } catch(Exception e) {
            log.error("No se pudo crear la tipoActivo",e)
            if (tipoActivo) {
                tipoActivo.discard()
            }
            flash.message = message(code:"tipoActivo.noCrea")
            render(view:"nuevo", model: [tipoActivo: tipoActivo])
        }
    }

    def ver = {
        //def tipoActivo = tipoActivoService.obtiene(params.id)
        def tipoActivo = TipoActivo.get(params.id)
        return [tipoActivo:tipoActivo]
    }

    def edita = {
        //def tipoActivo = tipoActivoService.obtiene(params.id)
        def tipoActivo = TipoActivo.get(params.id)
        return [tipoActivo:tipoActivo]
    }

    def actualiza = {
       // def tipoActivo = tipoActivoService.obtiene(params.id)
        def tipoActivo = TipoActivo.get(params.id)
        try {
           // TipoActivo.withTransaction {
                tipoActivo.properties = params
                //tipoActivo = tipoActivoService.actualiza(tipoActivo)
                flash.message = message(code:"tipoActivo.actualiza",args:[tipoActivo])
                redirect(action:"ver",id:tipoActivo.id)
            //}
        } catch(Exception e) {
            log.error("No se pudo actualizar la tipoActivo",e)
            if (tipoActivo) {
                tipoActivo.discard()
            }
            flash.message = message(code:"tipoActivo.noActualiza")
            render(view:"edita",model:[tipoActivo:tipoActivo])
        }

    }

//    def elimina = {
//        try {
//            TipoActivo.withTransaction {
//                def nombre = tipoActivoService.elimina(params.id)
//                flash.message = message(code:"tipoActivo.baja", args:[nombre])
//                redirect(action:'lista')
//            }
//        } catch(Exception e) {
//            log.error("No se pudo dar de baja la tipoActivo",e)
//            flash.message = message(code:"tipoActivo.noBaja")
//            render(view:'ver',model:[tipoActivo:tipoActivoService.obtiene(params.id)])
//        }
//    }
    def elimina = {
        def tipoActivo = TipoActivo.get(params.id)
        if (tipoActivo) {
            def nombre
            try {
                nombre = tipoActivo.nombre
                tipoActivo.delete(flush: true)
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'tipoActivo.label', default: 'TipoActivo'), nombre])
                redirect(action: "lista")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tipoActivo.label', default: 'TipoActivo'), nombre])
                redirect(action: "ver", id: params.id)
            }
        }
        else {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tipoActivo.label', default: 'TipoActivo'), params.id])
            redirect(action: "lista")
        }
    }   




    def cuentas = {
        params.filtro = params.term
        def lista = []
        for(cuenta in cuentaService.lista(params)) {
            lista << [id:cuenta.id,value:"$cuenta.nombre"]
        }
        def result = lista as grails.converters.JSON
        render result
    }

    def cuentaDiv = {
        def cuenta = cuentaService.obtiene(params.id)
        def div = """
                                      <table>
                                        <thead>
                                          <tr>
                                            <th>${message(code:'tipoActivo.cuenta')}</th>
                                            <th>${message(code:'cuenta.descripcion')}</th>
                                          </tr>
                                        </thead>
                                        <tbody>
                                          <tr>
                                            <td>${cuenta?.nombre}</td>
                                            <td>${cuenta?.descripcion}</td>
                                          </tr>
                                        </tbody>
                                      </table>
        """
        render div
    }
}
