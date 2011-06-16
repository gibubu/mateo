
<%@ page import="activos.TipoActivo" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tipoActivo.label', default: 'TipoActivo')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-tipoActivo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="nuevo"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-tipoActivo" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="nombre" title="${message(code: 'tipoActivo.nombre.label', default: 'Nombre')}" />
					
						<g:sortableColumn property="descripcion" title="${message(code: 'tipoActivo.descripcion.label', default: 'Descripcion')}" />
					
						<g:sortableColumn property="porciento" title="${message(code: 'tipoActivo.porciento.label', default: 'Porciento')}" />
					
						<th><g:message code="tipoActivo.cuenta.label" default="Cuenta" /></th>
					
						<th><g:message code="tipoActivo.empresa.label" default="Empresa" /></th>
					
						<g:sortableColumn property="vidaUtil" title="${message(code: 'tipoActivo.vidaUtil.label', default: 'Vida Util')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${tipoActivos}" status="i" var="tipoActivo">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="ver" id="${tipoActivo.id}">${fieldValue(bean: tipoActivo, field: "nombre")}</g:link></td>
					
						<td>${fieldValue(bean: tipoActivo, field: "descripcion")}</td>
					
						<td>${fieldValue(bean: tipoActivo, field: "porciento")}</td>
					
						<td>${fieldValue(bean: tipoActivo, field: "cuenta")}</td>
					
						<td>${fieldValue(bean: tipoActivo, field: "empresa")}</td>
					
						<td>${fieldValue(bean: tipoActivo, field: "vidaUtil")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${totalDeTipoActivos}" />
			</div>
		</div>
	</body>
</html>
