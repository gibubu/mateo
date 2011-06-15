package activos

import general.*

class FolioActivo implements java.io.Serializable {
    String nombre
    Long valor
    Organizacion organizacion

    static belongsTo = Organizacion

    static constraints = {
        nombre maxSize:32, unique:'organizacion'
    }

    static mapping = {
        table 'folio_activos'
        nombre index:'folio_activo_idx'
        organizacion index:'folio_activo_idx'
    }

    String toString() {
        return "$organizacion.codigo | $nombre | $valor"
    }
}
