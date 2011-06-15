package activos

import contabilidad.Cuenta
import general.Empresa

class TipoActivo implements java.io.Serializable {
    String nombre
    String descripcion
    Cuenta cuenta
    BigDecimal porciento
    Long vidaUtil
    Empresa empresa
    Set activos

    static belongsTo = [Empresa, Cuenta]

    static hasMany = [activos: Activo]

    static constraints = {
        nombre maxSize:64, unique : 'empresa'
        descripcion maxSize:128, nullable:true
        porciento scale:2, precision: 8
    }

    static mapping = {
        table 'tipos_activo'
    }

    static namedQueries = {
        buscaPorEmpresa { filtro ->
            empresa {
                idEq(filtro.id)
            }
        }

        buscaPorFiltro { filtro ->
            filtro = "%$filtro%"
            or {
                ilike 'nombre', filtro
                ilike 'descripcion', filtro
                cuenta {
                    or {
                        ilike 'nombre', filtro
                        ilike 'descripcion', filtro
                    }
                }
            }
        }
    }


    String toString() {
        return nombre
    }
}
