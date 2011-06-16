package general


import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

@TestFor(ProveedorController)
class ProveedorControllerIntegrationTests extends BaseIntegrationTest {

  @Test
    void debieraMostrarListaDeProveedor() {
        authenticateAdmin()
        for(i in 1..20) {
            new Proveedor (
                codigo: "TST$i"
                , nombre: "TEST-$i"
                , nombreCompleto : "TEST$i"
                , rfc : "TEST$i"
                , curp : "TEST$i"
                , direccion : "TEST$i"
                , telefono : "TEST$i"
            ).save()
        }

        def controller = new OrganizacionController()
        controller.index()
        assert '/proveedor/lista', controller.response.redirectedUrl

        def model = controller.lista()
        assertEquals 10, model.proveedor.size()
        assert 20 <= model.totalDeProveedores
    }

    @Test
    void debieraCrearProveedor() {
        authenticateAdmin()
        def controller = new ProveedorController()
        def model = controller.nueva()
        assert model
        assert model.proveedor

        controller.params.codigo = 'TST1'
        controller.params.nombre = 'TEST-1'
        controller.params.nombreCompleto = 'TEST-1'
         controller.params.rfc = 'TEST-1'
        controller.params.curp = 'TEST-1'
         controller.params.direccion = 'TEST-1'
        controller.params.telefono = 'TEST-1'
        controller.crea()
        assert controller.response.redirectedUrl.startsWith('/proveedor/edita')
    }

    @Test
    void debieraActualizarProveedor() {
        authenticateAdmin()
        def proveedor = new Proveedor (
             codigo: "TST$i"
                , nombre: "TEST-$i"
                , nombreCompleto : "TEST$i"
                , rfc : "TEST$i"
                , curp : "TEST$i"
                , direccion : "TEST$i"
                , telefono : "TEST$i"
        ).save()

        def controller = new ProveedorController()
        controller.params.id = proveedor.id
        def model = controller.ver()
        assert model.proveedor
        assertEquals 'TEST-1', model.proveedor.nombre

        controller.params.id = proveedor.id
        model = controller.edita()
        assert model.proveedor
        assertEquals 'TEST-1', model.proveedor.nombre

        controller.params.id = proveedor.id
        controller.params.version = proveedor.version
        controller.params.nombre = 'TEST-2'
        controller.actualiza()
        assertEquals "/proveedor/ver/${proveedor.id}", controller.response.redirectedUrl

        proveedor.refresh()
        assertEquals 'TEST-2', proveedor.nombre
    }

    @Test
    void debieraEliminarOrganizacion() {
        authenticateAdmin()
        def proveedor = new Proveedor (
           codigo: "TST$i"
                , nombre: "TEST-$i"
                , nombreCompleto : "TEST$i"
                , rfc : "TEST$i"
                , curp : "TEST$i"
                , direccion : "TEST$i"
                , telefono : "TEST$i"
        ).save()

        def controller = new ProveedorController()
        controller.params.id = proveedor.id
        def model = controller.ver()
        assert model.proveedor
        assertEquals 'TEST-1', model.proveedor.nombre

        controller.params.id = proveedor.id
        controller.elimina()
        assertEquals "/proveedor/lista", controller.response.redirectedUrl

        model = Proveedor.get(proveedor.id)
        assert !model
    }
}
