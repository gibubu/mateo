package activos

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

@TestFor(TipoControllerController)
class TipoActivoControllerIntegrationTests extends BaseIntegrationTest {

    @Test
    void debieraMostrarListaDeTipoActivo() {
        authenticateAdmin()
        for(i in 1..20) {
            new TipoActivo (
                codigo: "TST$i"
                , nombre: "TEST-$i"
                , descripcion : "TEST$i"
                , porciento : "TEST$i"
                , cuenta : "TEST$i"
                , empresa : "TEST$i"
                , vidaUtil : "TEST$i"
            ).save()
        }

        def controller = new TipoActivoController()
        controller.index()
        assert '/tipoActivo/lista', controller.response.redirectedUrl

        def model = controller.lista()
        assertEquals 10, model.tipoActivo.size()
        assert 20 <= model.totalDeTipoActivos
    }

    @Test
    void debieraCrearTipoActivo() {
        authenticateAdmin()
        def controller = new TipoActivoController()
        def model = controller.nueva()
        assert model
        assert model.tipoActivo

        controller.params.codigo = 'TST1'
        controller.params.nombre = 'TEST-1'
        controller.params.descripcion = 'TEST-1'
        controller.params.porciento = 'TEST-1'
        controller.params.cuenta = 'TEST-1'
        controller.params.empresa = 'TEST-1'
        controller.params.vidaUtil = 'TEST-1'
        controller.crea()
        assert controller.response.redirectedUrl.startsWith('/tipoActivo/edita')
    }

    @Test
    void debieraActualizarTipoActivo() {
        authenticateAdmin()
        def tipoActivo = new TipoActivo (
             codigo: "TST$i"
                , nombre: "TEST-$i"
                , descripcion : "TEST$i"
                , porciento : "TEST$i"
                , cuenta : "TEST$i"
                , empresa : "TEST$i"
                , vidaUtil : "TEST$i"
            ).save()

        def controller = new TipoActivoController()
        controller.params.id = tipoActivo.id
        def model = controller.ver()
        assert model.proveedor
        assertEquals 'TEST-1', model.tipoActivo.nombre

        controller.params.id = tipoActivo.id
        model = controller.edita()
        assert model.tipoActivo
        assertEquals 'TEST-1', model.tipoActivo.nombre

        controller.params.id = tipoActivo.id
        controller.params.version = tipoActivo.version
        controller.params.nombre = 'TEST-2'
        controller.actualiza()
        assertEquals "/tipoActivo/ver/${tipoActivo.id}", controller.response.redirectedUrl

        tipoActivo.refresh()
        assertEquals 'TEST-2', tipoActivo.nombre
    }

    @Test
    void debieraEliminarTipoActivo() {
        authenticateAdmin()
        def tipoActivo = new TipoActivo (
            codigo: "TST$i"
                , nombre: "TEST-$i"
                , descripcion : "TEST$i"
                , porciento : "TEST$i"
                , cuenta : "TEST$i"
                , empresa : "TEST$i"
                , vidaUtil : "TEST$i"
            ).save()

        def controller = new TipoActivoController()
        controller.params.id = tipoActivo.id
        def model = controller.ver()
        assert model.tipoActivo
        assertEquals 'TEST-1', model.tipoActivo.nombre

        controller.params.id = tipoActivo.id
        controller.elimina()
        assertEquals "/tipoActivo/lista", controller.response.redirectedUrl

        model = TipoActivo.get(tipoActivo.id)
        assert !model
    }
}
