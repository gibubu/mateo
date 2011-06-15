package activos

import general.*
import contabilidad.*

class Activo implements java.io.Serializable {
    String folio
    Date dateCreated
    Date fechaCompra
    String procedencia
    String factura
    String pedimento
    String moneda
    BigDecimal tipoCambio
    String condicion
    String poliza
    String codigo
    String descripcion
    String marca
    String modelo
    String serial
    BigDecimal moi = new BigDecimal('0')
    BigDecimal valorRescate = 1
    BigDecimal inpc = new BigDecimal('0')
    String ubicacion
    Boolean inactivo = false
    Date fechaInactivo
    TipoActivo tipoActivo
    Proveedor proveedor
    Cuenta centroCosto
    Empresa empresa
    String responsable
    String motivo = 'COMPRA'
    Boolean garantia = false
    Integer mesesGarantia = 0
    Boolean seguro = false
    Date depreciacionFecha
    BigDecimal depreciacionAnual = new BigDecimal('0')
    BigDecimal depreciacionMensual = new BigDecimal('0')
    BigDecimal depreciacionAcumulada = new BigDecimal('0')
    BigDecimal valorNeto = new BigDecimal('0')
    Set imagenes

    static belongsTo = [TipoActivo, Proveedor, Cuenta, Empresa]

    static hasMany = [reubicaciones: ReubicacionActivo, imagenes:Imagen]

    static constraints = {
        folio       maxSize:64, unique:'empresa'
        procedencia maxSize:64, nullable:true
        factura     maxSize:32, nullable:true
        pedimento   maxSize:64, nullable:true
        moneda      maxSize:32, nullable:true
        tipoCambio  nullable:true
        condicion   maxSize:64, nullable:true
        poliza      maxSize:64, nullable:true
        codigo      maxSize:64, nullable:true
        descripcion maxSize:128, nullable:true
        marca       maxSize:32, nullable:true
        modelo      maxSize:32, nullable:true
        serial      maxSize:64, nullable:true
        moi         scale:2, precision:8
        valorRescate scale:2, precision:8
        inpc        scale:2, precision:8
        ubicacion   maxSize:64, nullable:true
        fechaInactivo nullable:true
        responsable maxSize:128, nullable:true
        motivo      maxSize:32, inList:['COMPRA','DONACION']
        depreciacionAnual       scale:2, precision:8
        depreciacionMensual     scale:2, precision:8
        depreciacionAcumulada   scale:2, precision:8
        valorNeto               scale:2, precision:8
    }

    static mapping = {
        table 'activos'
        codigo index:'activo_codigo_idx'
        imagenes cascade:'all-delete-orphan'
    }

    static namedQueries = {
        buscaPorEmpresa { empresaId ->
            empresa {
                idEq(empresaId)
            }
            join 'centroCosto'
            join 'tipoActivo'
            join 'proveedor'
        }

        buscaPorCodigo { filtro ->
            filtro = "%$filtro%"
            ilike 'codigo',filtro
        }

        buscaPorFecha { fechaInicial, fechaFinal ->
            between 'fechaCompra', fechaInicial, fechaFinal
        }

        buscaPorProveedor { filtro ->
            filtro = "%$filtro%"
            proveedor {
                or {
                    ilike 'nombre',filtro
                    ilike 'nombreCompleto', filtro
                    ilike 'rfc', filtro
                }
            }
        }

        buscaPorTipoActivo { filtro ->
            filtro = "%$filtro%"
            tipoActivo {
                or {
                    ilike 'nombre', filtro
                    cuenta {
                        or {
                            ilike 'nombre', filtro
                            ilike 'descripcion', filtro
                        }
                    }
                }
            }
        }

        buscaPorCentroCosto { filtro ->
            filtro = "%$filtro%"
            centroCosto {
                eq('centroCosto',true)
                or {
                    ilike 'nombre', filtro
                    ilike 'descripcion', filtro
                }
            }
        }

        buscaPorResponsable { filtro ->
            filtro = "%$filtro%"
            or {
                ilike 'ubicacion', filtro
                ilike 'responsable', filtro
            }
        }

        buscaPorDescripcion { filtro ->
            filtro = "%$filtro%"
            ilike 'descripcion', filtro
        }

        buscaPorBaja {
            eq 'inactivo', true
        }

        buscaPorReubicado { 
            reubicaciones {
                isNotNull 'id'
            }
        }

        suma {
            projections {
                sum 'depreciacionAnual'
                sum 'depreciacionMensual'
                sum 'depreciacionAcumulada'
                sum 'moi'
            }
        }

        conTipoActivo {
            join 'tipoActivo'
        }

        activo {
            eq 'inactivo', false
        }

    }

    String toString() {
        return codigo
    }
}
