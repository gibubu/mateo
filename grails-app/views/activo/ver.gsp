
<%@ page import="activos.Activo" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'activo.label', default: 'Activo')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-activo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="lista"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="nuevo"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-activo" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list activo">
			
				<g:if test="${activo?.folio}">
				<li class="fieldcontain">
					<span id="folio-label" class="property-label"><g:message code="activo.folio.label" default="Folio" /></span>
					
						<span class="property-value" aria-labelledby="folio-label"><g:fieldValue bean="${activo}" field="folio"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.procedencia}">
				<li class="fieldcontain">
					<span id="procedencia-label" class="property-label"><g:message code="activo.procedencia.label" default="Procedencia" /></span>
					
						<span class="property-value" aria-labelledby="procedencia-label"><g:fieldValue bean="${activo}" field="procedencia"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.factura}">
				<li class="fieldcontain">
					<span id="factura-label" class="property-label"><g:message code="activo.factura.label" default="Factura" /></span>
					
						<span class="property-value" aria-labelledby="factura-label"><g:fieldValue bean="${activo}" field="factura"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.pedimento}">
				<li class="fieldcontain">
					<span id="pedimento-label" class="property-label"><g:message code="activo.pedimento.label" default="Pedimento" /></span>
					
						<span class="property-value" aria-labelledby="pedimento-label"><g:fieldValue bean="${activo}" field="pedimento"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.moneda}">
				<li class="fieldcontain">
					<span id="moneda-label" class="property-label"><g:message code="activo.moneda.label" default="Moneda" /></span>
					
						<span class="property-value" aria-labelledby="moneda-label"><g:fieldValue bean="${activo}" field="moneda"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.tipoCambio}">
				<li class="fieldcontain">
					<span id="tipoCambio-label" class="property-label"><g:message code="activo.tipoCambio.label" default="Tipo Cambio" /></span>
					
						<span class="property-value" aria-labelledby="tipoCambio-label"><g:fieldValue bean="${activo}" field="tipoCambio"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.condicion}">
				<li class="fieldcontain">
					<span id="condicion-label" class="property-label"><g:message code="activo.condicion.label" default="Condicion" /></span>
					
						<span class="property-value" aria-labelledby="condicion-label"><g:fieldValue bean="${activo}" field="condicion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.poliza}">
				<li class="fieldcontain">
					<span id="poliza-label" class="property-label"><g:message code="activo.poliza.label" default="Poliza" /></span>
					
						<span class="property-value" aria-labelledby="poliza-label"><g:fieldValue bean="${activo}" field="poliza"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.codigo}">
				<li class="fieldcontain">
					<span id="codigo-label" class="property-label"><g:message code="activo.codigo.label" default="Codigo" /></span>
					
						<span class="property-value" aria-labelledby="codigo-label"><g:fieldValue bean="${activo}" field="codigo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.descripcion}">
				<li class="fieldcontain">
					<span id="descripcion-label" class="property-label"><g:message code="activo.descripcion.label" default="Descripcion" /></span>
					
						<span class="property-value" aria-labelledby="descripcion-label"><g:fieldValue bean="${activo}" field="descripcion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.marca}">
				<li class="fieldcontain">
					<span id="marca-label" class="property-label"><g:message code="activo.marca.label" default="Marca" /></span>
					
						<span class="property-value" aria-labelledby="marca-label"><g:fieldValue bean="${activo}" field="marca"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.modelo}">
				<li class="fieldcontain">
					<span id="modelo-label" class="property-label"><g:message code="activo.modelo.label" default="Modelo" /></span>
					
						<span class="property-value" aria-labelledby="modelo-label"><g:fieldValue bean="${activo}" field="modelo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.serial}">
				<li class="fieldcontain">
					<span id="serial-label" class="property-label"><g:message code="activo.serial.label" default="Serial" /></span>
					
						<span class="property-value" aria-labelledby="serial-label"><g:fieldValue bean="${activo}" field="serial"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.moi}">
				<li class="fieldcontain">
					<span id="moi-label" class="property-label"><g:message code="activo.moi.label" default="Moi" /></span>
					
						<span class="property-value" aria-labelledby="moi-label"><g:fieldValue bean="${activo}" field="moi"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.valorRescate}">
				<li class="fieldcontain">
					<span id="valorRescate-label" class="property-label"><g:message code="activo.valorRescate.label" default="Valor Rescate" /></span>
					
						<span class="property-value" aria-labelledby="valorRescate-label"><g:fieldValue bean="${activo}" field="valorRescate"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.inpc}">
				<li class="fieldcontain">
					<span id="inpc-label" class="property-label"><g:message code="activo.inpc.label" default="Inpc" /></span>
					
						<span class="property-value" aria-labelledby="inpc-label"><g:fieldValue bean="${activo}" field="inpc"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.ubicacion}">
				<li class="fieldcontain">
					<span id="ubicacion-label" class="property-label"><g:message code="activo.ubicacion.label" default="Ubicacion" /></span>
					
						<span class="property-value" aria-labelledby="ubicacion-label"><g:fieldValue bean="${activo}" field="ubicacion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.fechaInactivo}">
				<li class="fieldcontain">
					<span id="fechaInactivo-label" class="property-label"><g:message code="activo.fechaInactivo.label" default="Fecha Inactivo" /></span>
					
						<span class="property-value" aria-labelledby="fechaInactivo-label"><g:formatDate date="${activo?.fechaInactivo}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.responsable}">
				<li class="fieldcontain">
					<span id="responsable-label" class="property-label"><g:message code="activo.responsable.label" default="Responsable" /></span>
					
						<span class="property-value" aria-labelledby="responsable-label"><g:fieldValue bean="${activo}" field="responsable"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.motivo}">
				<li class="fieldcontain">
					<span id="motivo-label" class="property-label"><g:message code="activo.motivo.label" default="Motivo" /></span>
					
						<span class="property-value" aria-labelledby="motivo-label"><g:fieldValue bean="${activo}" field="motivo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.depreciacionAnual}">
				<li class="fieldcontain">
					<span id="depreciacionAnual-label" class="property-label"><g:message code="activo.depreciacionAnual.label" default="Depreciacion Anual" /></span>
					
						<span class="property-value" aria-labelledby="depreciacionAnual-label"><g:fieldValue bean="${activo}" field="depreciacionAnual"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.depreciacionMensual}">
				<li class="fieldcontain">
					<span id="depreciacionMensual-label" class="property-label"><g:message code="activo.depreciacionMensual.label" default="Depreciacion Mensual" /></span>
					
						<span class="property-value" aria-labelledby="depreciacionMensual-label"><g:fieldValue bean="${activo}" field="depreciacionMensual"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.depreciacionAcumulada}">
				<li class="fieldcontain">
					<span id="depreciacionAcumulada-label" class="property-label"><g:message code="activo.depreciacionAcumulada.label" default="Depreciacion Acumulada" /></span>
					
						<span class="property-value" aria-labelledby="depreciacionAcumulada-label"><g:fieldValue bean="${activo}" field="depreciacionAcumulada"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.valorNeto}">
				<li class="fieldcontain">
					<span id="valorNeto-label" class="property-label"><g:message code="activo.valorNeto.label" default="Valor Neto" /></span>
					
						<span class="property-value" aria-labelledby="valorNeto-label"><g:fieldValue bean="${activo}" field="valorNeto"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.centroCosto}">
				<li class="fieldcontain">
					<span id="centroCosto-label" class="property-label"><g:message code="activo.centroCosto.label" default="Centro Costo" /></span>
					
						<span class="property-value" aria-labelledby="centroCosto-label"><g:link controller="cuenta" action="show" id="${activo?.centroCosto?.id}">${activo?.centroCosto?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="activo.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${activo?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.depreciacionFecha}">
				<li class="fieldcontain">
					<span id="depreciacionFecha-label" class="property-label"><g:message code="activo.depreciacionFecha.label" default="Depreciacion Fecha" /></span>
					
						<span class="property-value" aria-labelledby="depreciacionFecha-label"><g:formatDate date="${activo?.depreciacionFecha}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.empresa}">
				<li class="fieldcontain">
					<span id="empresa-label" class="property-label"><g:message code="activo.empresa.label" default="Empresa" /></span>
					
						<span class="property-value" aria-labelledby="empresa-label"><g:link controller="empresa" action="show" id="${activo?.empresa?.id}">${activo?.empresa?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.fechaCompra}">
				<li class="fieldcontain">
					<span id="fechaCompra-label" class="property-label"><g:message code="activo.fechaCompra.label" default="Fecha Compra" /></span>
					
						<span class="property-value" aria-labelledby="fechaCompra-label"><g:formatDate date="${activo?.fechaCompra}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.garantia}">
				<li class="fieldcontain">
					<span id="garantia-label" class="property-label"><g:message code="activo.garantia.label" default="Garantia" /></span>
					
						<span class="property-value" aria-labelledby="garantia-label"><g:formatBoolean boolean="${activo?.garantia}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.imagenes}">
				<li class="fieldcontain">
					<span id="imagenes-label" class="property-label"><g:message code="activo.imagenes.label" default="Imagenes" /></span>
					
						<g:each in="${activo.imagenes}" var="i">
						<span class="property-value" aria-labelledby="imagenes-label"><g:link controller="imagen" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.inactivo}">
				<li class="fieldcontain">
					<span id="inactivo-label" class="property-label"><g:message code="activo.inactivo.label" default="Inactivo" /></span>
					
						<span class="property-value" aria-labelledby="inactivo-label"><g:formatBoolean boolean="${activo?.inactivo}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.mesesGarantia}">
				<li class="fieldcontain">
					<span id="mesesGarantia-label" class="property-label"><g:message code="activo.mesesGarantia.label" default="Meses Garantia" /></span>
					
						<span class="property-value" aria-labelledby="mesesGarantia-label"><g:fieldValue bean="${activo}" field="mesesGarantia"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.proveedor}">
				<li class="fieldcontain">
					<span id="proveedor-label" class="property-label"><g:message code="activo.proveedor.label" default="Proveedor" /></span>
					
						<span class="property-value" aria-labelledby="proveedor-label"><g:link controller="proveedor" action="show" id="${activo?.proveedor?.id}">${activo?.proveedor?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.reubicaciones}">
				<li class="fieldcontain">
					<span id="reubicaciones-label" class="property-label"><g:message code="activo.reubicaciones.label" default="Reubicaciones" /></span>
					
						<g:each in="${activo.reubicaciones}" var="r">
						<span class="property-value" aria-labelledby="reubicaciones-label"><g:link controller="reubicacionActivo" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.seguro}">
				<li class="fieldcontain">
					<span id="seguro-label" class="property-label"><g:message code="activo.seguro.label" default="Seguro" /></span>
					
						<span class="property-value" aria-labelledby="seguro-label"><g:formatBoolean boolean="${activo?.seguro}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${activo?.tipoActivo}">
				<li class="fieldcontain">
					<span id="tipoActivo-label" class="property-label"><g:message code="activo.tipoActivo.label" default="Tipo Activo" /></span>
					
						<span class="property-value" aria-labelledby="tipoActivo-label"><g:link controller="tipoActivo" action="show" id="${activo?.tipoActivo?.id}">${activo?.tipoActivo?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${activo?.id}" />
					<g:link class="edit" action="edita" id="${activo?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="elimina" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
