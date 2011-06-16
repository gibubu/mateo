
<%@ page import="activos.TipoActivo" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tipoActivo.label', default: 'TipoActivo')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-tipoActivo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="lista"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="nuevo"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-tipoActivo" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list tipoActivo">
			
				<g:if test="${tipoActivo?.nombre}">
				<li class="fieldcontain">
					<span id="nombre-label" class="property-label"><g:message code="tipoActivo.nombre.label" default="Nombre" /></span>
					
						<span class="property-value" aria-labelledby="nombre-label"><g:fieldValue bean="${tipoActivo}" field="nombre"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.descripcion}">
				<li class="fieldcontain">
					<span id="descripcion-label" class="property-label"><g:message code="tipoActivo.descripcion.label" default="Descripcion" /></span>
					
						<span class="property-value" aria-labelledby="descripcion-label"><g:fieldValue bean="${tipoActivo}" field="descripcion"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.porciento}">
				<li class="fieldcontain">
					<span id="porciento-label" class="property-label"><g:message code="tipoActivo.porciento.label" default="Porciento" /></span>
					
						<span class="property-value" aria-labelledby="porciento-label"><g:fieldValue bean="${tipoActivo}" field="porciento"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.activos}">
				<li class="fieldcontain">
					<span id="activos-label" class="property-label"><g:message code="tipoActivo.activos.label" default="Activos" /></span>
					
						<g:each in="${tipoActivo.activos}" var="a">
						<span class="property-value" aria-labelledby="activos-label"><g:link controller="activo" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.cuenta}">
				<li class="fieldcontain">
					<span id="cuenta-label" class="property-label"><g:message code="tipoActivo.cuenta.label" default="Cuenta" /></span>
					
						<span class="property-value" aria-labelledby="cuenta-label"><g:link controller="cuenta" action="show" id="${tipoActivo?.cuenta?.id}">${tipoActivo?.cuenta?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.empresa}">
				<li class="fieldcontain">
					<span id="empresa-label" class="property-label"><g:message code="tipoActivo.empresa.label" default="Empresa" /></span>
					
						<span class="property-value" aria-labelledby="empresa-label"><g:link controller="empresa" action="show" id="${tipoActivo?.empresa?.id}">${tipoActivo?.empresa?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${tipoActivo?.vidaUtil}">
				<li class="fieldcontain">
					<span id="vidaUtil-label" class="property-label"><g:message code="tipoActivo.vidaUtil.label" default="Vida Util" /></span>
					
						<span class="property-value" aria-labelledby="vidaUtil-label"><g:fieldValue bean="${tipoActivo}" field="vidaUtil"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${tipoActivo?.id}" />
					<g:link class="edit" action="edita" id="${tipoActivo?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="elimina" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
