package general

import java.util.List
import auditoria.general.XProveedor

class ProveedorService {

    static transactional = true

    def springSecurityService

    List<Proveedor> lista(params) {
        log.debug "Lista de proveedores"
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def proveedores = []
        if (params?.filtro) {
            proveedores = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).buscaPorFiltro(params.filtro).list(params)
        } else {
            proveedores = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).list(params)
        }
        return proveedores
    }
	
    Map listaConCantidad(params) {
        def usuario = Usuario.obtieneConRelaciones().get(springSecurityService.getPrincipal().id)
        def proveedores = []
        def cantidad = 0
        if (params?.filtro) {
            proveedores = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).buscaPorFiltro(params.filtro).list(params)
            cantidad = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).buscaPorFiltro(params.filtro).count()
        } else {
            proveedores = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).list(params)
            cantidad = Proveedor.buscaPorEmpresa(usuario.almacen.empresa).count()
        }
        return [lista:proveedores, cantidad:cantidad]
    }
	
    Proveedor obtiene(id) {
        def proveedor = Proveedor.get(id)
        if (!proveedor) {
            throw new RuntimeException("No se encontro al proveedor $id")
        }
        return proveedor
    }
	
    Proveedor crea(proveedor) {
        def usuario = Usuario.get(springSecurityService.getPrincipal().id)
        proveedor.empresa = usuario.almacen.empresa
        
        proveedor.save()

        audita(proveedor,Constantes.CREAR)

        return proveedor
    }
	
    Proveedor creaConEmpresa(proveedor) {
        proveedor.save()

        audita(proveedor,Constantes.CREAR)

        return proveedor
    }
	
    Proveedor actualiza(proveedor) {
        log.debug "Actualizando al proveedor $proveedor"
        proveedor.save()
        audita(proveedor,Constantes.ACTUALIZAR)
        return proveedor
    }
	
    String elimina(id) {
        def proveedor = Proveedor.get(id)
        if (!proveedor.base) {
            String nombre = proveedor.nombre
            proveedor.delete()
            audita(proveedor,Constantes.ELIMINAR)
            return nombre
        } else {
            throw new RuntimeException("No se puede eliminar un proveedor base")
        }
    }

    void audita(proveedor, actividad) {
        log.debug "[AUDITA] $actividad proveedor $proveedor"
        def creador = springSecurityService.authentication.name
        def xproveedor = new XProveedor(proveedor.properties)
        xproveedor.proveedorId = proveedor.id
        xproveedor.empresaId = proveedor.empresa.id
        xproveedor.creador = creador
        xproveedor.actividad = actividad
        xproveedor.save()
    }
}


