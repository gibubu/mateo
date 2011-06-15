<%@ page import="activos.Activo" %>



<div class="fieldcontain ${hasErrors(bean: activo, field: 'folio', 'error')} ">
	<label for="folio">
		<g:message code="activo.folio.label" default="Folio" />
		
	</label>
	<g:textField name="folio" maxlength="64" value="${activo?.folio}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'procedencia', 'error')} ">
	<label for="procedencia">
		<g:message code="activo.procedencia.label" default="Procedencia" />
		
	</label>
	<g:textField name="procedencia" maxlength="64" value="${activo?.procedencia}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'factura', 'error')} ">
	<label for="factura">
		<g:message code="activo.factura.label" default="Factura" />
		
	</label>
	<g:textField name="factura" maxlength="32" value="${activo?.factura}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'pedimento', 'error')} ">
	<label for="pedimento">
		<g:message code="activo.pedimento.label" default="Pedimento" />
		
	</label>
	<g:textField name="pedimento" maxlength="64" value="${activo?.pedimento}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'moneda', 'error')} ">
	<label for="moneda">
		<g:message code="activo.moneda.label" default="Moneda" />
		
	</label>
	<g:textField name="moneda" maxlength="32" value="${activo?.moneda}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'tipoCambio', 'error')} ">
	<label for="tipoCambio">
		<g:message code="activo.tipoCambio.label" default="Tipo Cambio" />
		
	</label>
	<g:field type="number" name="tipoCambio" value="${fieldValue(bean: activo, field: 'tipoCambio')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'condicion', 'error')} ">
	<label for="condicion">
		<g:message code="activo.condicion.label" default="Condicion" />
		
	</label>
	<g:textField name="condicion" maxlength="64" value="${activo?.condicion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'poliza', 'error')} ">
	<label for="poliza">
		<g:message code="activo.poliza.label" default="Poliza" />
		
	</label>
	<g:textField name="poliza" maxlength="64" value="${activo?.poliza}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'codigo', 'error')} ">
	<label for="codigo">
		<g:message code="activo.codigo.label" default="Codigo" />
		
	</label>
	<g:textField name="codigo" maxlength="64" value="${activo?.codigo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'descripcion', 'error')} ">
	<label for="descripcion">
		<g:message code="activo.descripcion.label" default="Descripcion" />
		
	</label>
	<g:textField name="descripcion" maxlength="128" value="${activo?.descripcion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'marca', 'error')} ">
	<label for="marca">
		<g:message code="activo.marca.label" default="Marca" />
		
	</label>
	<g:textField name="marca" maxlength="32" value="${activo?.marca}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'modelo', 'error')} ">
	<label for="modelo">
		<g:message code="activo.modelo.label" default="Modelo" />
		
	</label>
	<g:textField name="modelo" maxlength="32" value="${activo?.modelo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'serial', 'error')} ">
	<label for="serial">
		<g:message code="activo.serial.label" default="Serial" />
		
	</label>
	<g:textField name="serial" maxlength="64" value="${activo?.serial}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'moi', 'error')} required">
	<label for="moi">
		<g:message code="activo.moi.label" default="Moi" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="moi" required="" value="${fieldValue(bean: activo, field: 'moi')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'valorRescate', 'error')} required">
	<label for="valorRescate">
		<g:message code="activo.valorRescate.label" default="Valor Rescate" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="valorRescate" required="" value="${fieldValue(bean: activo, field: 'valorRescate')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'inpc', 'error')} required">
	<label for="inpc">
		<g:message code="activo.inpc.label" default="Inpc" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="inpc" required="" value="${fieldValue(bean: activo, field: 'inpc')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'ubicacion', 'error')} ">
	<label for="ubicacion">
		<g:message code="activo.ubicacion.label" default="Ubicacion" />
		
	</label>
	<g:textField name="ubicacion" maxlength="64" value="${activo?.ubicacion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'fechaInactivo', 'error')} ">
	<label for="fechaInactivo">
		<g:message code="activo.fechaInactivo.label" default="Fecha Inactivo" />
		
	</label>
	<g:datePicker name="fechaInactivo" precision="day" value="${activo?.fechaInactivo}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'responsable', 'error')} ">
	<label for="responsable">
		<g:message code="activo.responsable.label" default="Responsable" />
		
	</label>
	<g:textField name="responsable" maxlength="128" value="${activo?.responsable}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'motivo', 'error')} ">
	<label for="motivo">
		<g:message code="activo.motivo.label" default="Motivo" />
		
	</label>
	<g:select name="motivo" from="${activo.constraints.motivo.inList}" value="${activo?.motivo}" valueMessagePrefix="activo.motivo" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'depreciacionAnual', 'error')} required">
	<label for="depreciacionAnual">
		<g:message code="activo.depreciacionAnual.label" default="Depreciacion Anual" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="depreciacionAnual" required="" value="${fieldValue(bean: activo, field: 'depreciacionAnual')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'depreciacionMensual', 'error')} required">
	<label for="depreciacionMensual">
		<g:message code="activo.depreciacionMensual.label" default="Depreciacion Mensual" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="depreciacionMensual" required="" value="${fieldValue(bean: activo, field: 'depreciacionMensual')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'depreciacionAcumulada', 'error')} required">
	<label for="depreciacionAcumulada">
		<g:message code="activo.depreciacionAcumulada.label" default="Depreciacion Acumulada" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="depreciacionAcumulada" required="" value="${fieldValue(bean: activo, field: 'depreciacionAcumulada')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'valorNeto', 'error')} required">
	<label for="valorNeto">
		<g:message code="activo.valorNeto.label" default="Valor Neto" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="valorNeto" required="" value="${fieldValue(bean: activo, field: 'valorNeto')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'centroCosto', 'error')} required">
	<label for="centroCosto">
		<g:message code="activo.centroCosto.label" default="Centro Costo" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="centroCosto" name="centroCosto.id" from="${contabilidad.Cuenta.list()}" optionKey="id" required="" value="${activo?.centroCosto?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'depreciacionFecha', 'error')} required">
	<label for="depreciacionFecha">
		<g:message code="activo.depreciacionFecha.label" default="Depreciacion Fecha" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="depreciacionFecha" precision="day" value="${activo?.depreciacionFecha}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'empresa', 'error')} required">
	<label for="empresa">
		<g:message code="activo.empresa.label" default="Empresa" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="empresa" name="empresa.id" from="${general.Empresa.list()}" optionKey="id" required="" value="${activo?.empresa?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'fechaCompra', 'error')} required">
	<label for="fechaCompra">
		<g:message code="activo.fechaCompra.label" default="Fecha Compra" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="fechaCompra" precision="day" value="${activo?.fechaCompra}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'garantia', 'error')} ">
	<label for="garantia">
		<g:message code="activo.garantia.label" default="Garantia" />
		
	</label>
	<g:checkBox name="garantia" value="${activo?.garantia}" />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'imagenes', 'error')} ">
	<label for="imagenes">
		<g:message code="activo.imagenes.label" default="Imagenes" />
		
	</label>
	<g:select name="imagenes" from="${general.Imagen.list()}" multiple="multiple" optionKey="id" size="5" value="${activo?.imagenes*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'inactivo', 'error')} ">
	<label for="inactivo">
		<g:message code="activo.inactivo.label" default="Inactivo" />
		
	</label>
	<g:checkBox name="inactivo" value="${activo?.inactivo}" />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'mesesGarantia', 'error')} required">
	<label for="mesesGarantia">
		<g:message code="activo.mesesGarantia.label" default="Meses Garantia" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="mesesGarantia" required="" value="${fieldValue(bean: activo, field: 'mesesGarantia')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'proveedor', 'error')} required">
	<label for="proveedor">
		<g:message code="activo.proveedor.label" default="Proveedor" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="proveedor" name="proveedor.id" from="${general.Proveedor.list()}" optionKey="id" required="" value="${activo?.proveedor?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'reubicaciones', 'error')} ">
	<label for="reubicaciones">
		<g:message code="activo.reubicaciones.label" default="Reubicaciones" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${activo?.reubicaciones?}" var="r">
    <li><g:link controller="reubicacionActivo" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="reubicacionActivo" action="create" params="['activo.id': activoInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'reubicacionActivo.label', default: 'ReubicacionActivo')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'seguro', 'error')} ">
	<label for="seguro">
		<g:message code="activo.seguro.label" default="Seguro" />
		
	</label>
	<g:checkBox name="seguro" value="${activo?.seguro}" />
</div>

<div class="fieldcontain ${hasErrors(bean: activo, field: 'tipoActivo', 'error')} required">
	<label for="tipoActivo">
		<g:message code="activo.tipoActivo.label" default="Tipo Activo" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tipoActivo" name="tipoActivo.id" from="${activos.TipoActivo.list()}" optionKey="id" required="" value="${activo?.tipoActivo?.id}" class="many-to-one"/>
</div>

