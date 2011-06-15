package activos

import auditoria.activos.XTipoActivo
import general.*

class TipoActivoService {

    static transactional = true

    def springSecurityService

    List<TipoActivo> lista(def params) {
        log.debug "Lista de tipos de activo"
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def query = TipoActivo.buscaPorEmpresa(usuario.almacen.empresa)
        if (params?.filtro) {
            query = query.buscaPorFiltro(params.filtro)
        }
        return query.list(params)
    }
	
    def listaConCantidad(def params) {
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def query = TipoActivo.buscaPorEmpresa(usuario.almacen.empresa)
        if (params?.filtro) {
            query = query.buscaPorFiltro(params.filtro)
        }
        return [lista:query.list(params), cantidad:query.count()]
    }
	
    TipoActivo obtiene(String id) {
        return TipoActivo.get(id)
    }
	
    TipoActivo crea(TipoActivo tipoActivo) {
        def usuario = Usuario.get(springSecurityService.getPrincipal().id)
        tipoActivo.empresa = usuario.almacen.empresa
        
        tipoActivo.save()

        audita(tipoActivo,Constantes.CREAR)

        return tipoActivo
    }
	
    TipoActivo creaConEmpresa(TipoActivo tipoActivo) {
        log.debug "Creando el tipo de activo $tipoActivo en la empresa $tipoActivo.empresa"
        tipoActivo.save()

        audita(tipoActivo,Constantes.CREAR)

        return tipoActivo
    }
	
    TipoActivo actualiza(TipoActivo tipoActivo) {
        tipoActivo.save()
        audita(tipoActivo,Constantes.ACTUALIZAR)
        return tipoActivo
    }
	
    String elimina(String id) {
        def tipoActivo = TipoActivo.get(id)
        String nombre = tipoActivo.nombre
        tipoActivo.delete()
        audita(tipoActivo,Constantes.ELIMINAR)
        return nombre
    }

    void audita(TipoActivo tipoActivo, String actividad) {
        log.debug "[AUDITA] $actividad tipoActivo $tipoActivo"
        def creador = springSecurityService.authentication.name
        def xtipoActivo = new XTipoActivo(tipoActivo.properties)
        xtipoActivo.tipoActivoId = tipoActivo.id
        xtipoActivo.empresaId = tipoActivo.empresa.id
        xtipoActivo.creador = creador
        xtipoActivo.actividad = actividad
        xtipoActivo.save()
    }
}
