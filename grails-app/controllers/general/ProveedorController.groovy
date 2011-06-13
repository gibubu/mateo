package general

import grails.plugins.springsecurity.Secured


@Secured(['ROLE_EMP'])
class ProveedorController {

    def springSecurityService
    def proveedorService

    def index = {
        redirect(action: lista, params: params)
    }

    def lista = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        //def resultado = proveedorService.listaConCantidad(params)
        def usuario = springSecurityService.currentUser
        [proveedores: Proveedor.findAllByEmpresa(usuario.empresa, params), totalDeProveedores: Proveedor.countByEmpresa(usuario.empresa)]
    }

    def nuevo = {
        Proveedor proveedor = new Proveedor(params)
        return [proveedor:proveedor]
    }

    def crea = {
        //Proveedor.withTransaction {
        def proveedor = new Proveedor(params)
        if (proveedor.save(flush: true)) {
                
            flash.message = message(code: 'default.created.message', args: [message(code: 'proveedor.label', default: 'Proveedor'), proveedor.nombre])
            redirect( action: "ver", id: proveedor.id)
            }
            else {
                render(view: "nueva", model: [proveedor: proveedor])
            }
            //}
        }

        def ver = {
            //def proveedor = proveedorService.obtiene(params.id)
            def proveedor = Proveedor.get(params.id)
            if (!proveedor) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'proveedor.label', default: 'Proveedor'), params.id])
                redirect(action: "lista")
            }
            else {
                [proveedor: proveedor]
            }
        }

        def edita = {
            //def proveedor = proveedorService.obtiene(params.id)
            def proveedor = Proveedor.get(params.id)
            return [proveedor:proveedor]
        }

        def actualiza = {
            //def proveedor = proveedorService.obtiene(params.id)
            def proveedor = Proveedor.get(params.id)
            try {
                Proveedor.withTransaction {
                    proveedor.properties = params
                    //proveedor = proveedorService.actualiza(proveedor)
                    flash.message = message(code:'default.updated.message',args:[proveedor])
                    redirect(action:"ver",id:proveedor.id)
                }
            } catch(Exception e) {
                log.error("No se pudo actualizar la proveedor",e)
                if (proveedor) {
                    proveedor.discard()
                }
                flash.message = message(code:"proveedor.noActualiza")
                render(view:"edita",model:[proveedor:proveedor])
            }

        }

        def elimina = {
            // Proveedor.withTransaction {
            def proveedor = Proveedor.get(params.id)
            if (proveedor) {
                def nombre
                try {
                    nombre = proveedor.nombre
                    proveedor.delete(flush: true)
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'proveedor.label', default: 'proveedor'), nombre])
                    redirect(action: "lista")
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'proveedo.label', default: 'proveedor'), nombre])
                    redirect(action: "ver", id: params.id)
                }
            }
            else {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'proveedor.label', default: 'proveedor'), params.id])
                redirect(action: "lista")
            }
            // }
        }
        }


