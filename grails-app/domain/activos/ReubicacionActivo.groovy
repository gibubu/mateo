package activos

import contabilidad.*

/**
 *
 * @author jdmr
 */
class ReubicacionActivo implements java.io.Serializable {
    String creador
    Date dateCreated
    String comentarios
    Activo activo
    Cuenta centroCosto

    static belongsTo = [Cuenta, Activo]

    static constraints = {
        creador(maxSize:64,blank:false)
        comentarios(maxSize:200,nullable:true)
    }

    static mapping = {
        table 'reubicaciones_activo'
    }
}
