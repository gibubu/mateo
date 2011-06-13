package inventario

class TipoProducto implements {//java.io.Serializable {
	String nombre
	String descripcion
	//Boolean base = false
	//Almacen almacen
	
    static belongsTo = Almacen
	

    static constraints = {
    	nombre(blank:false maxSize: 64)
    	descripcion(maxSize:200)
    }
    
    static mapping = {
        table 'tipos_producto'
    }
    
    String toString() {
    	nombre
    }
}
