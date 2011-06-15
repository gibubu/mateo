package activos

/**
 *
 * @author jdmr
 */
class BajaActivo implements java.io.Serializable {
    String creador
    Date fechaBaja
    Date dateCreated
    String motivo
    String comentarios
    Activo activo

    static belongsTo = [Activo]

    static constraints = {
        creador(maxSize:64, blank:false)
        motivo(maxSize:32, inList:['OBSOLETO','PERDIDA','DONACION','VENTA'])
        comentarios(maxSize:200,nullable:true)
    }

    static mapping = {
        table 'bajas_activo'
    }
}
