package activos

class FolioActivoService {

    static transactional = true

    def activo(organizacion) {
        def folio = FolioActivo.findByOrganizacionAndNombre(organizacion, 'ACTIVOS',[lock:true, flush:true])
        if (!folio) {
            folio = new FolioActivo(nombre:'ACTIVOS',valor:0,organizacion:organizacion).save(flush:true)
            folio = activo(organizacion)
        }
        return folio
    }

    def numberFormat = {
        java.text.NumberFormat nf = java.text.DecimalFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(7);
        nf.setMaximumIntegerDigits(7);
        nf.setMaximumFractionDigits(0);
        return nf
    }
}
