<%@ page import="general.Proveedor" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'proveedor.label', default: 'Proveedor')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-proveedor" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="lista"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="nuevo"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-proveedor" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list proveedor">

				<g:if test="${proveedor?.nombre}">
				<li class="fieldcontain">
					<span id="nombre-label" class="property-label"><g:message code="proveedor.nombre.label" default="Nombre" /></span>

						<span class="property-value" aria-labelledby="nombre-label"><g:fieldValue bean="${proveedor}" field="nombre"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.nombreCompleto}">
				<li class="fieldcontain">
					<span id="nombreCompleto-label" class="property-label"><g:message code="proveedor.nombreCompleto.label" default="Nombre Completo" /></span>

						<span class="property-value" aria-labelledby="nombreCompleto-label"><g:fieldValue bean="${proveedor}" field="nombreCompleto"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.rfc}">
				<li class="fieldcontain">
					<span id="rfc-label" class="property-label"><g:message code="proveedor.rfc.label" default="Rfc" /></span>

						<span class="property-value" aria-labelledby="rfc-label"><g:fieldValue bean="${proveedor}" field="rfc"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.curp}">
				<li class="fieldcontain">
					<span id="curp-label" class="property-label"><g:message code="proveedor.curp.label" default="Curp" /></span>

						<span class="property-value" aria-labelledby="curp-label"><g:fieldValue bean="${proveedor}" field="curp"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.direccion}">
				<li class="fieldcontain">
					<span id="direccion-label" class="property-label"><g:message code="proveedor.direccion.label" default="Direccion" /></span>

						<span class="property-value" aria-labelledby="direccion-label"><g:fieldValue bean="${proveedor}" field="direccion"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.telefono}">
				<li class="fieldcontain">
					<span id="telefono-label" class="property-label"><g:message code="proveedor.telefono.label" default="Telefono" /></span>

						<span class="property-value" aria-labelledby="telefono-label"><g:fieldValue bean="${proveedor}" field="telefono"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.fax}">
				<li class="fieldcontain">
					<span id="fax-label" class="property-label"><g:message code="proveedor.fax.label" default="Fax" /></span>

						<span class="property-value" aria-labelledby="fax-label"><g:fieldValue bean="${proveedor}" field="fax"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.contacto}">
				<li class="fieldcontain">
					<span id="contacto-label" class="property-label"><g:message code="proveedor.contacto.label" default="Contacto" /></span>

						<span class="property-value" aria-labelledby="contacto-label"><g:fieldValue bean="${proveedor}" field="contacto"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.correo}">
				<li class="fieldcontain">
					<span id="correo-label" class="property-label"><g:message code="proveedor.correo.label" default="Correo" /></span>

						<span class="property-value" aria-labelledby="correo-label"><g:fieldValue bean="${proveedor}" field="correo"/></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.base}">
				<li class="fieldcontain">
					<span id="base-label" class="property-label"><g:message code="proveedor.base.label" default="Base" /></span>

						<span class="property-value" aria-labelledby="base-label"><g:formatBoolean boolean="${proveedor?.base}" /></span>

				</li>
				</g:if>

				<g:if test="${proveedor?.empresa}">
				<li class="fieldcontain">
					<span id="empresa-label" class="property-label"><g:message code="proveedor.empresa.label" default="Empresa" /></span>

						<span class="property-value" aria-labelledby="empresa-label"><g:link controller="empresa" action="show" id="${proveedor?.empresa?.id}">${proveedor?.empresa?.encodeAsHTML()}</g:link></span>

				</li>
				</g:if>

			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${proveedor?.id}" />
					<g:link class="edit" action="edita" id="${proveedor?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="elimina" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>