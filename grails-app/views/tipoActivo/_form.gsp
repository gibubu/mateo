<%@ page import="activos.TipoActivo" %>



<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'nombre', 'error')} ">
	<label for="nombre">
		<g:message code="tipoActivo.nombre.label" default="Nombre" />
		
	</label>
	<g:textField name="nombre" maxlength="64" value="${tipoActivo?.nombre}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'descripcion', 'error')} ">
	<label for="descripcion">
		<g:message code="tipoActivo.descripcion.label" default="Descripcion" />
		
	</label>
	<g:textField name="descripcion" maxlength="128" value="${tipoActivo?.descripcion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'porciento', 'error')} required">
	<label for="porciento">
		<g:message code="tipoActivo.porciento.label" default="Porciento" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="porciento" required="" value="${fieldValue(bean: tipoActivo, field: 'porciento')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'activos', 'error')} ">
	<label for="activos">
		<g:message code="tipoActivo.activos.label" default="Activos" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${tipoActivo?.activos?}" var="a">
    <li><g:link controller="activo" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="activo" action="create" params="['tipoActivo.id': tipoActivoInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'activo.label', default: 'Activo')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'cuenta', 'error')} required">
	<label for="cuenta">
		<g:message code="tipoActivo.cuenta.label" default="Cuenta" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="cuenta" name="cuenta.id" from="${contabilidad.Cuenta.list()}" optionKey="id" required="" value="${tipoActivo?.cuenta?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'empresa', 'error')} required">
	<label for="empresa">
		<g:message code="tipoActivo.empresa.label" default="Empresa" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="empresa" name="empresa.id" from="${general.Empresa.list()}" optionKey="id" required="" value="${tipoActivo?.empresa?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tipoActivo, field: 'vidaUtil', 'error')} required">
	<label for="vidaUtil">
		<g:message code="tipoActivo.vidaUtil.label" default="Vida Util" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="vidaUtil" required="" value="${fieldValue(bean: tipoActivo, field: 'vidaUtil')}"/>
</div>

